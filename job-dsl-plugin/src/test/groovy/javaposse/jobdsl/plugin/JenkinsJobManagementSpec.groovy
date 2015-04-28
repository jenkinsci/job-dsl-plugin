package javaposse.jobdsl.plugin

import com.cloudbees.hudson.plugins.folder.Folder
import com.google.common.io.Resources
import hudson.EnvVars
import hudson.model.AbstractBuild
import hudson.model.Failure
import hudson.model.FreeStyleBuild
import hudson.model.FreeStyleProject
import hudson.model.ListView
import hudson.model.View
import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.ConfigFile
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.ConfigurationMissingException
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.NameNotProvidedException
import javaposse.jobdsl.dsl.helpers.step.StepContext
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import org.jvnet.hudson.test.WithoutJenkins
import spock.lang.Specification

import static com.google.common.base.Charsets.UTF_8
import static com.google.common.io.Resources.getResource
import static hudson.model.Result.UNSTABLE

class JenkinsJobManagementSpec extends Specification {
    private static final String FILE_NAME = 'test.txt'
    private static final String JOB_NAME = 'test-job'

    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule()

    ByteArrayOutputStream buffer = new ByteArrayOutputStream()
    AbstractBuild build = Mock(AbstractBuild)
    JenkinsJobManagement jobManagement = new JenkinsJobManagement(new PrintStream(buffer), new EnvVars(), build)

    @WithoutJenkins
    def 'createOrUpdateView without name'() {
        when:
        jobManagement.createOrUpdateView(null, '<View/>', true)

        then:
        thrown(NameNotProvidedException)

        when:
        jobManagement.createOrUpdateView('', '<View/>', true)

        then:
        thrown(NameNotProvidedException)
    }

    @WithoutJenkins
    def 'createOrUpdateView without config'() {
        when:
        jobManagement.createOrUpdateView('test', null, true)

        then:
        thrown(ConfigurationMissingException)

        when:
        jobManagement.createOrUpdateView('test', null, true)

        then:
        thrown(ConfigurationMissingException)
    }

    @WithoutJenkins
    def 'createOrUpdateView with invalid name'() {
        when:
        jobManagement.createOrUpdateView('t<e*st', '<View/>', true)

        then:
        thrown(Failure)
    }

    def 'checkMinimumPluginVersion not installed'() {
        when:
        jobManagement.requireMinimumPluginVersion('foo', '1.2.3')

        then:
        1 * build.setResult(UNSTABLE)
        buffer.size() > 0
    }

    def 'checkMinimumPluginVersion too old'() {
        when:
        jobManagement.requireMinimumPluginVersion('ldap', '20.0')

        then:
        1 * build.setResult(UNSTABLE)
        buffer.size() > 0
    }

    def 'checkMinimumPluginVersion success'() {
        when:
        jobManagement.requireMinimumPluginVersion('ldap', '1.1')

        then:
        0 * build.setResult(UNSTABLE)
        buffer.size() == 0
    }

    def 'requireMinimumCoreVersion success'() {
        when:
        jobManagement.requireMinimumCoreVersion('1.480')

        then:
        0 * build.setResult(UNSTABLE)
        buffer.size() == 0
    }

    def 'requireMinimumCoreVersion failed'() {
        when:
        jobManagement.requireMinimumCoreVersion('1.600')

        then:
        1 * build.setResult(UNSTABLE)
        buffer.size() > 0
    }

    def 'callExtension not found'() {
        when:
        Node result = jobManagement.callExtension('foo', StepContext)

        then:
        result == null
    }

    def 'callExtension with no args'() {
        when:
        Node result = jobManagement.callExtension('test', StepContext)

        then:
        isXmlIdentical('extension.xml', result)
    }

    def 'callExtension defined twice'() {
        when:
        jobManagement.callExtension('twice', StepContext)

        then:
        Exception e = thrown(DslException)
        e.message.contains(TestContextExtensionPoint.name)
        e.message.contains(TestContextExtensionPoint2.name)
    }

    def 'callExtension with object result'() {
        when:
        Node result = jobManagement.callExtension('testComplexObject', StepContext, 'foo', 42, true)

        then:
        isXmlIdentical('extension.xml', result)
    }

    def 'callExtension with closure'() {
        setup:
        Closure closure = {
            value1('foo')
            value2(42)
            value3(true)
        }

        when:
        Node result = jobManagement.callExtension('withNestedContext', StepContext, closure)

        then:
        isXmlIdentical('extension.xml', result)
    }

    def 'create job with nonexisting parent'() {
        when:
        jobManagement.createOrUpdateConfig(
                'nonexistingfolder/project', Resources.toString(getResource('minimal-job.xml'), UTF_8), true
        )

        then:
        DslException e = thrown()
        e.message == 'Could not create item, unknown parent path in "nonexistingfolder/project"'
    }

    def 'create view with nonexisting parent'() {
        when:
        jobManagement.createOrUpdateView(
                'nonexistingfolder/view', Resources.toString(getResource('minimal-view.xml'), UTF_8), true
        )

        then:
        DslException e = thrown()
        e.message == 'Could not create view, unknown parent path in "nonexistingfolder/view"'
    }

    def 'rename job'() {
        setup:
        jenkinsRule.createFreeStyleProject('oldName')

        when:
        jobManagement.renameJobMatching('oldName', 'newName')

        then:
        jenkinsRule.jenkins.getItemByFullName('newName') != null
        jenkinsRule.jenkins.getItemByFullName('oldName') == null
    }

    def 'rename job relative to seed job'() {
        setup:
        Folder folder = jenkinsRule.jenkins.createProject(Folder, 'folder')
        folder.createProject(FreeStyleProject, 'oldName')
        FreeStyleProject seedJob = folder.createProject(FreeStyleProject, 'seed')
        AbstractBuild build = seedJob.scheduleBuild2(0).get()
        JobManagement jobManagement = new JenkinsJobManagement(
                new PrintStream(buffer), new EnvVars(), build, LookupStrategy.SEED_JOB
        )

        when:
        jobManagement.renameJobMatching('oldName', 'newName')

        then:
        jenkinsRule.jenkins.getItemByFullName('folder/newName') != null
        jenkinsRule.jenkins.getItemByFullName('folder/oldName') == null
    }

    def 'rename job relative to seed job with absolute destination'() {
        setup:
        Folder folder = jenkinsRule.jenkins.createProject(Folder, 'folder')
        folder.createProject(FreeStyleProject, 'oldName')
        FreeStyleProject seedJob = folder.createProject(FreeStyleProject, 'seed')
        AbstractBuild build = seedJob.scheduleBuild2(0).get()
        JobManagement jobManagement = new JenkinsJobManagement(
                new PrintStream(buffer), new EnvVars(), build, LookupStrategy.SEED_JOB
        )

        when:
        jobManagement.renameJobMatching('oldName', '/newName')

        then:
        jenkinsRule.jenkins.getItemByFullName('newName') != null
        jenkinsRule.jenkins.getItemByFullName('oldName') == null
    }

    def 'move Job to other Folder'() {
        setup:
        Folder oldFolder = jenkinsRule.jenkins.createProject(Folder, 'oldFolder')
        jenkinsRule.jenkins.createProject(Folder, 'newFolder')
        oldFolder.createProject(FreeStyleProject, 'oldName')

        when:
        jobManagement.renameJobMatching('oldFolder/oldName', 'newFolder/newName')

        then:
        jenkinsRule.jenkins.getItemByFullName('newFolder/newName') != null
        jenkinsRule.jenkins.getItemByFullName('oldFolder/oldName') == null
    }

    def 'createOrUpdateConfig relative to folder'() {
        setup:
        Folder folder = jenkinsRule.jenkins.createProject(Folder, 'folder')
        FreeStyleProject project = folder.createProject(FreeStyleProject, 'seed')
        AbstractBuild build = project.scheduleBuild2(0).get()
        JenkinsJobManagement jobManagement = new JenkinsJobManagement(
                new PrintStream(buffer), new EnvVars(), build, LookupStrategy.SEED_JOB
        )

        when:
        jobManagement.createOrUpdateConfig('project', Resources.toString(getResource('minimal-job.xml'), UTF_8), true)

        then:
        jenkinsRule.jenkins.getItemByFullName('/folder/project') != null
    }

    def 'createOrUpdateConfig with absolute path'() {
        setup:
        Folder folder = jenkinsRule.jenkins.createProject(Folder, 'folder')
        FreeStyleProject project = folder.createProject(FreeStyleProject, 'seed')
        AbstractBuild build = project.scheduleBuild2(0).get()
        JenkinsJobManagement jobManagement = new JenkinsJobManagement(
                new PrintStream(buffer), new EnvVars(), build, LookupStrategy.SEED_JOB
        )

        when:
        jobManagement.createOrUpdateConfig('/project', Resources.toString(getResource('minimal-job.xml'), UTF_8), true)

        then:
        jenkinsRule.jenkins.getItemByFullName('/project') != null
    }

    def 'createOrUpdateView relative to folder'() {
        setup:
        Folder folder = jenkinsRule.jenkins.createProject(Folder, 'folder')
        FreeStyleProject project = folder.createProject(FreeStyleProject, 'seed')
        AbstractBuild build = project.scheduleBuild2(0).get()
        JenkinsJobManagement jobManagement = new JenkinsJobManagement(
                new PrintStream(buffer), new EnvVars(), build, LookupStrategy.SEED_JOB
        )

        when:
        jobManagement.createOrUpdateView('view', Resources.toString(getResource('minimal-view.xml'), UTF_8), true)

        then:
        folder.getView('view') != null
    }

    def 'get plugin version'() {
        when:
        VersionNumber version = jobManagement.getPluginVersion('cvs')

        then:
        version != null
    }

    def 'get plugin version of unknown plugin'() {
        when:
        VersionNumber version = jobManagement.getPluginVersion('foo')

        then:
        version == null
    }

    def 'getJenkinsVersion returns a version'() {
        when:
        VersionNumber versionNumber = jobManagement.jenkinsVersion

        then:
        versionNumber != null
    }

    def 'get vSphere cloud hash without vSphere cloud plugin'() {
        when:
        Integer hash = jobManagement.getVSphereCloudHash('test')

        then:
        hash == null
    }

    def 'read file from any workspace, job does not exist'() {
        when:
        String result = jobManagement.readFileInWorkspace(JOB_NAME, FILE_NAME)

        then:
        result == null
        buffer.toString().contains(FILE_NAME)
        buffer.toString().contains(JOB_NAME)
    }

    def 'read file from any workspace, no build, no workspace'() {
        setup:
        jenkinsRule.createFreeStyleProject(JOB_NAME)

        when:
        String result = jobManagement.readFileInWorkspace(JOB_NAME, FILE_NAME)

        then:
        result == null
        buffer.toString().contains(FILE_NAME)
        buffer.toString().contains(JOB_NAME)
    }

    def 'read file from any workspace, file does not exist'() {
        setup:
        jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject(JOB_NAME))

        when:
        String result = jobManagement.readFileInWorkspace(JOB_NAME, FILE_NAME)

        then:
        result == null
        buffer.toString().contains(FILE_NAME)
        buffer.toString().contains(JOB_NAME)
    }

    def 'read file from any workspace, file exists'() {
        setup:
        FreeStyleBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject(JOB_NAME))
        build.workspace.child(FILE_NAME).write('hello', 'UTF-8')

        when:
        String result = jobManagement.readFileInWorkspace(JOB_NAME, FILE_NAME)

        then:
        result == 'hello'
    }

    def 'get config file id without config files provider plugin'() {
        when:
        String id = jobManagement.getConfigFileId(ConfigFileType.MavenSettings, 'test')

        then:
        id == null
    }

    def 'create config file without config files provider plugin'() {
        setup:
        ConfigFile configFile = Mock(ConfigFile)
        configFile.name >> 'foo'

        when:
        jobManagement.createOrUpdateConfigFile(configFile, false)

        then:
        thrown(DslException)
    }

    @WithoutJenkins
    def 'create config file without name'() {
        setup:
        ConfigFile configFile = Mock(ConfigFile)

        when:
        jobManagement.createOrUpdateConfigFile(configFile, false)

        then:
        thrown(NameNotProvidedException)
    }

    def 'getCredentialsId without Credentials Plugin'() {
        when:
        String id = jobManagement.getCredentialsId('test')

        then:
        id == null
    }

    def 'create view'() {
        when:
        jobManagement.createOrUpdateView('test-view', '<hudson.model.ListView/>', false)

        then:
        View view = jenkinsRule.instance.getView('test-view')
        view instanceof ListView
    }

    def 'update view'() {
        setup:
        jenkinsRule.instance.addView(new ListView('test-view'))

        when:
        jobManagement.createOrUpdateView(
                'test-view',
                '<hudson.model.ListView><description>lorem ipsum</description></hudson.model.ListView>',
                false
        )

        then:
        View view = jenkinsRule.instance.getView('test-view')
        view instanceof ListView
        view.description == 'lorem ipsum'
    }

    def 'update view ignoring changes'() {
        setup:
        jenkinsRule.instance.addView(new ListView('test-view'))

        when:
        jobManagement.createOrUpdateView(
                'test-view',
                '<hudson.model.ListView><description>lorem ipsum</description></hudson.model.ListView>',
                true
        )

        then:
        View view = jenkinsRule.instance.getView('test-view')
        view instanceof ListView
        view.description == null
    }

    def 'create view with invalid config'() {
        when:
        jobManagement.createOrUpdateView('test-view', '<hudson.model.ListView>', false)

        then:
        jenkinsRule.instance.getView('test-view') == null
    }

    def 'readFileFromWorkspace with exception'() {
        setup:
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(jenkinsRule.createFreeStyleProject())
        jobManagement = new JenkinsJobManagement(System.out, new EnvVars(), build)
        String fileName = 'test.txt'

        when:
        jobManagement.readFileInWorkspace(fileName)

        then:
        Exception e = thrown(IllegalStateException)
        e.message.contains(fileName)
    }

    def 'get all job permissions'() {
        setup:
        String propertyName = 'hudson.security.AuthorizationMatrixProperty'

        when:
        Set<String> permissions = jobManagement.getPermissions(propertyName)

        then:
        'hudson.model.Item.Delete' in permissions
        'hudson.model.Item.Configure' in permissions
        'hudson.model.Item.Read' in permissions
        'hudson.model.Item.Discover' in permissions
        'hudson.model.Item.Build' in permissions
        'hudson.model.Item.Workspace' in permissions
        'hudson.model.Item.Cancel' in permissions
        'hudson.model.Item.Move' in permissions
        'hudson.model.Run.Delete' in permissions
        'hudson.model.Run.Update' in permissions
        'hudson.scm.SCM.Tag' in permissions
    }

    def 'get all folder permissions'() {
        setup:
        String propertyName = 'com.cloudbees.hudson.plugins.folder.properties.AuthorizationMatrixProperty'

        when:
        Set<String> permissions = jobManagement.getPermissions(propertyName)

        then:
        'hudson.model.Item.Create' in permissions
        'hudson.model.Item.Delete' in permissions
        'hudson.model.Item.Configure' in permissions
        'hudson.model.Item.Read' in permissions
        'hudson.model.Item.Discover' in permissions
        'hudson.model.Item.Build' in permissions
        'hudson.model.Item.Workspace' in permissions
        'hudson.model.Item.Cancel' in permissions
        'hudson.model.Item.Move' in permissions
        'hudson.model.Run.Delete' in permissions
        'hudson.model.Run.Update' in permissions
    }

    private static boolean isXmlIdentical(String expected, Node actual) throws Exception {
        XMLUnit.ignoreWhitespace = true
        XMLUnit.compareXML(loadResource(expected), nodeToString(actual)).identical()
    }

    private static String nodeToString(Node node) {
        StringWriter writer = new StringWriter()
        new XmlNodePrinter(new PrintWriter(writer)).print(node)
        writer.toString()
    }

    private static String loadResource(String resourceName) {
        Resources.toString(getResource(resourceName), UTF_8)
    }
}
