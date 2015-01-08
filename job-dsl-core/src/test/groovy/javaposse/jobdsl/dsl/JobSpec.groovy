package javaposse.jobdsl.dsl

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.helpers.Permissions
import javaposse.jobdsl.dsl.helpers.common.MavenContext
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.atomic.AtomicBoolean

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class JobSpec extends Specification {
    private final resourcesDir = new File(getClass().getResource('/simple.dsl').toURI()).parentFile
    private final JobManagement jobManagement = Mock(JobManagement)
    private Job job = new Job(jobManagement)

    def setup() {
        XMLUnit.setIgnoreWhitespace(true)
    }

    def 'set name on a manually constructed job'() {
        when:
        job.name('NAME')

        then:
        job.name == 'NAME'
    }

    def 'load an empty template from a manually constructed job'() {
        when:
        job.using('TMPL')
        job.xml

        then:
        1 * jobManagement.getConfig('TMPL') >> minimalXml
    }

    def 'load an empty template from a manually constructed job and generate xml from it'() {
        when:
        job.using('TMPL')
        def xml = job.xml

        then:
        _ * jobManagement.getConfig('TMPL') >> minimalXml
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + minimalXml, xml
    }

    def 'load large template from file'() {
        setup:
        JobManagement jm = new FileJobManagement(resourcesDir)
        Job job = new Job(jm)

        when:
        job.using('config')
        String project = job.xml

        then:
        project.contains('<description>Description</description>')
    }

    def 'generate job from missing template - throws JobTemplateMissingException'() {
        setup:
        JobManagement jm = new FileJobManagement(resourcesDir)
        Job job = new Job(jm)

        when:
        job.using('TMPL-NOT_THERE')
        job.xml

        then:
        thrown(JobTemplateMissingException)
    }

    def 'run engine and ensure canRoam values'() {
        setup:
        JobManagement jm = new FileJobManagement(resourcesDir)
        Job job = new Job(jm)

        when:
        def projectRoaming = job.node

        then:
        // See that jobs can roam by default
        projectRoaming.canRoam[0].value() == ['true']

        when:
        job.label('Ubuntu')
        def projectLabelled = job.node

        then:
        projectLabelled.canRoam[0].value() == false
    }

    def 'create withXml blocks'() {
        when:
        job.configure { Node node ->
            // Not going to actually execute this
        }

        job.configure { Node node ->
            // Not going to actually execute this
        }

        then:
        !job.withXmlActions.empty
    }

    def 'update Node using withXml'() {
        setup:
        Node project = new XmlParser().parse(new StringReader(minimalXml))
        Job job = new Job(null)
        AtomicBoolean boolOutside = new AtomicBoolean(true)

        when: 'Simple update'
        job.configure { Node node ->
            node.description[0].value = 'Test Description'
        }
        job.executeWithXmlActions(project)

        then:
        project.description[0].text() == 'Test Description'

        when: 'Update using variable from outside scope'
        job.configure { Node node ->
            node.keepDependencies[0].value = boolOutside.get()
        }
        job.executeWithXmlActions(project)

        then:
        project.keepDependencies[0].text() == 'true'

        then:
        project.description[0].text() == 'Test Description'

        when: 'Change value on outside scope variable to ensure closure is being run again'
        boolOutside.set(false)
        job.executeWithXmlActions(project)

        then:
        project.keepDependencies[0].text() == 'false'

        when: 'Append node'
        job.configure { Node node ->
            def actions = node.actions[0]
            actions.appendNode('action', 'Make Breakfast')
        }
        job.executeWithXmlActions(project)

        then:
        project.actions[0].children().size() == 1

        when: 'Execute withXmlActions again'
        job.executeWithXmlActions(project)

        then:
        project.actions[0].children().size() == 2
    }

    def 'construct simple Maven job and generate xml from it'() {
        setup:
        job.type = JobType.Maven

        when:
        def xml = job.xml

        then:
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + mavenXml, xml
    }

    def 'free-style job extends Maven template and fails to generate xml'() {
        when:
        job.using('TMPL')
        job.xml

        then:
        1 * jobManagement.getConfig('TMPL') >> mavenXml
        thrown(JobTypeMismatchException)
    }

    def 'Maven job extends free-style template and fails to generate xml'() {
        setup:
        job.type = JobType.Maven

        when:
        job.using('TMPL')
        job.xml

        then:
        1 * jobManagement.getConfig('TMPL') >> minimalXml
        thrown(JobTypeMismatchException)
    }

    def 'construct simple Build Flow job and generate xml from it'() {
        setup:
        job.type = JobType.BuildFlow

        when:
        def xml = job.xml

        then:
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + buildFlowXml, xml
    }

    def 'construct simple Matrix job and generate xml from it'() {
        setup:
        job.type = JobType.Matrix

        when:
        def xml = job.xml

        then:
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + matrixJobXml, xml
    }

    def 'call authorization'() {
        when:
        job.authorization {
            permission('hudson.model.Item.Configure:jill')
            permission('hudson.model.Item.Configure:jack')
        }

        then:
        NodeList permissions = job.node.properties[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 2
        permissions[0].text() == 'hudson.model.Item.Configure:jill'
        permissions[1].text() == 'hudson.model.Item.Configure:jack'
    }

    def 'call permission'() {
        when:
        job.permission('hudson.model.Item.Configure:jill')
        job.permission(Permissions.ItemRead, 'jack')
        job.permission('RunUpdate', 'joe')

        then:
        NodeList permissions = job.node.properties[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 3
        permissions[0].text() == 'hudson.model.Item.Configure:jill'
        permissions[1].text() == 'hudson.model.Item.Read:jack'
        permissions[2].text() == 'hudson.model.Run.Update:joe'
    }

    def 'call parameters via helper'() {
        when:
        job.parameters {
            booleanParam('myBooleanParam', true)
        }

        then:
        with(job.node.properties[0].'hudson.model.ParametersDefinitionProperty'[0].parameterDefinitions[0]) {
            children()[0].name() == 'hudson.model.BooleanParameterDefinition'
            children()[0].name.text() == 'myBooleanParam'
            children()[0].defaultValue.text() == 'true'
        }
    }

    def 'call scm'() {
        when:
        job.scm {
            git {
                wipeOutWorkspace()
            }
        }

        then:
        job.node.scm[0].wipeOutWorkspace[0].text() == 'true'
    }

    def 'duplicate scm calls allowed with multiscm'() {
        when:
        job.multiscm {
            git('git://github.com/jenkinsci/jenkins.git')
            git('git://github.com/jenkinsci/job-dsl-plugin.git')
        }

        then:
        noExceptionThrown()
        job.node.scm[0].scms[0].scm.size() == 2
    }

    def 'call wrappers'() {
        when:
        job.wrappers {
            maskPasswords()
        }

        then:
        job.node.buildWrappers[0].children()[0].name() ==
                'com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsBuildWrapper'
    }

    def 'call triggers'() {
        when:
        job.triggers {
            scm('2 3 * * * *')
        }

        then:
        job.node.triggers[0].'hudson.triggers.SCMTrigger'[0].spec[0].text() == '2 3 * * * *'
    }

    def 'no steps for Maven jobs'() {
        setup:
        job.type = JobType.Maven

        when:
        job.steps {
        }

        then:
        thrown(IllegalStateException)
    }

    def 'call steps'() {
        when:
        job.steps {
            shell('ls')
        }

        then:
        job.node.builders[0].'hudson.tasks.Shell'[0].command[0].text() == 'ls'
    }

    def 'call publishers'() {
        when:
        job.publishers {
            chucknorris()
        }

        then:
        job.node.publishers[0].'hudson.plugins.chucknorris.CordellWalkerRecorder'[0].factGenerator[0].text() == ''
    }

    def 'cannot create Build Flow for free style jobs'() {
        when:
        job.buildFlow('build block')

        then:
        thrown(IllegalStateException)
    }

    def 'buildFlow constructs xml'() {
        setup:
        job.type = JobType.BuildFlow

        when:
        job.buildFlow('build Flow Block')

        then:
        job.node.dsl.size() == 1
        job.node.dsl[0].value() == 'build Flow Block'
    }

    def 'cannot run combinationFilter for free style jobs'() {
        when:
        job.combinationFilter('LABEL1 == "TEST"')

        then:
        thrown(IllegalStateException)
    }

    def 'combinationFilter constructs xml'() {
        setup:
        job.type = JobType.Matrix

        when:
        job.combinationFilter('LABEL1 == "TEST"')

        then:
        job.node.combinationFilter.size() == 1
        job.node.combinationFilter[0].value() == 'LABEL1 == "TEST"'
    }

    def 'cannot set runSequentially for free style jobs'() {
        when:
        job.runSequentially()

        then:
        thrown(IllegalStateException)
    }

    def 'runSequentially constructs xml'() {
        setup:
        job.type = JobType.Matrix

        when:
        job.runSequentially(false)

        then:
        job.node.executionStrategy.runSequentially.size() == 1
        job.node.executionStrategy.runSequentially[0].value() == false
    }

    def 'cannot set touchStoneFilter for free style jobs'() {
        when:
        job.touchStoneFilter('LABEL1 == "TEST"', true)

        then:
        thrown(IllegalStateException)
    }

    def 'touchStoneFilter constructs xml'() {
        setup:
        job.type = JobType.Matrix

        when:
        job.touchStoneFilter('LABEL1 == "TEST"', true)

        then:
        with(job.node.executionStrategy) {
            touchStoneCombinationFilter.size() == 1
            touchStoneCombinationFilter[0].value() == 'LABEL1 == "TEST"'
            touchStoneResultCondition.size() == 1
            touchStoneResultCondition[0].children().size() == 3
            touchStoneResultCondition[0].name[0].value() == 'UNSTABLE'
            touchStoneResultCondition[0].color[0].value() == 'YELLOW'
            touchStoneResultCondition[0].ordinal[0].value() == 1
        }

        when:
        job.touchStoneFilter('LABEL1 == "TEST"', false)

        then:
        with(job.node.executionStrategy) {
            touchStoneCombinationFilter.size() == 1
            touchStoneCombinationFilter[0].value() == 'LABEL1 == "TEST"'
            touchStoneResultCondition.size() == 1
            touchStoneResultCondition[0].children().size() == 3
            touchStoneResultCondition[0].name[0].value() == 'STABLE'
            touchStoneResultCondition[0].color[0].value() == 'BLUE'
            touchStoneResultCondition[0].ordinal[0].value() == 0
        }
    }

    def 'cannot run axis for free style jobs'() {
        when:
        job.axes {
            label('LABEL1', 'a', 'b', 'c')
        }

        then:
        thrown(IllegalStateException)
    }

    def 'can set axis'() {
        setup:
        job.type = JobType.Matrix

        when:
        job.axes {
            label('LABEL1', 'a', 'b', 'c')
        }

        then:
        job.node.axes.size() == 1
        job.node.axes[0].children().size() == 1
        job.node.axes[0].children()[0].name() == 'hudson.matrix.LabelAxis'
    }

    def 'axes configure block constructs xml'() {
        setup:
        job.type = JobType.Matrix

        when:
        job.axes {
            configure { axes ->
                axes << 'FooAxis'()
            }
        }

        then:
        job.node.axes.size() == 1
        job.node.axes[0].children().size() == 1
        job.node.axes[0].children()[0].name() == 'FooAxis'
    }

    def 'cannot run rootPOM for free style jobs'() {
        when:
        job.rootPOM('pom.xml')

        then:
        thrown(IllegalStateException)
    }

    def 'rootPOM constructs xml'() {
        setup:
        job.type = JobType.Maven

        when:
        job.rootPOM('my_module/pom.xml')

        then:
        job.node.rootPOM.size() == 1
        job.node.rootPOM[0].value() == 'my_module/pom.xml'
    }

    def 'cannot run goals for free style jobs'() {
        when:
        job.goals('package')

        then:
        thrown(IllegalStateException)
    }

    def 'goals constructs xml'() {
        setup:
        job.type = JobType.Maven

        when:
        job.goals('clean')
        job.goals('verify')

        then:
        job.node.goals.size() == 1
        job.node.goals[0].value() == 'clean verify'
    }

    def 'cannot run mavenOpts for free style jobs'() {
        when:
        job.mavenOpts('-Xmx512m')

        then:
        thrown(IllegalStateException)
    }

    def 'mavenOpts constructs xml'() {
        setup:
        job.type = JobType.Maven

        when:
        job.mavenOpts('-Xms256m')
        job.mavenOpts('-Xmx512m')

        then:
        job.node.mavenOpts.size() == 1
        job.node.mavenOpts[0].value() == '-Xms256m -Xmx512m'
    }

    def 'cannot run perModuleEmail for free style jobs'() {
        when:
        job.perModuleEmail(false)

        then:
        thrown(IllegalStateException)
    }

    def 'perModuleEmail constructs xml'() {
        setup:
        job.type = JobType.Maven

        when:
        job.perModuleEmail(false)

        then:
        job.node.perModuleEmail.size() == 1
        job.node.perModuleEmail[0].value() == false
    }

    def 'cannot run archivingDisabled for free style jobs'() {
        when:
        job.archivingDisabled(false)

        then:
        thrown(IllegalStateException)
    }

    def 'archivingDisabled constructs xml'() {
        setup:
        job.type = JobType.Maven

        when:
        job.archivingDisabled(true)

        then:
        job.node.archivingDisabled.size() == 1
        job.node.archivingDisabled[0].value() == true
    }

    def 'cannot run runHeadless for free style jobs'() {
        when:
        job.runHeadless(false)

        then:
        thrown(IllegalStateException)
    }

    def 'runHeadless constructs xml'() {
        setup:
        job.type = JobType.Maven

        when:
        job.runHeadless(true)

        then:
        job.node.runHeadless.size() == 1
        job.node.runHeadless[0].value() == true
    }

    def 'cannot run localRepository for free style jobs'() {
        when:
        job.localRepository(MavenContext.LocalRepositoryLocation.LocalToExecutor)

        then:
        thrown(IllegalStateException)
    }

    def 'cannot run localRepository with null argument'() {
        setup:
        job.type = JobType.Maven

        when:
        job.localRepository(null)

        then:
        thrown(NullPointerException)
    }

    def 'localRepository constructs xml for LocalToExecutor'() {
        setup:
        job.type = JobType.Maven

        when:
        job.localRepository(MavenContext.LocalRepositoryLocation.LocalToExecutor)

        then:
        job.node.localRepository[0].attribute('class') == 'hudson.maven.local_repo.PerExecutorLocalRepositoryLocator'
    }

    def 'localRepository constructs xml for LocalToWorkspace'() {
        setup:
        job.type = JobType.Maven

        when:
        job.localRepository(MavenContext.LocalRepositoryLocation.LocalToWorkspace)

        then:
        job.node.localRepository[0].attribute('class') == 'hudson.maven.local_repo.PerJobLocalRepositoryLocator'
    }

    def 'cannot run preBuildSteps for freestyle jobs'() {
        when:
        job.preBuildSteps {
        }

        then:
        thrown(IllegalStateException)
    }

    def 'can add preBuildSteps'() {
        setup:
        job.type = JobType.Maven

        when:
        job.preBuildSteps {
            shell('ls')
        }

        then:
        job.node.prebuilders[0].children()[0].name() == 'hudson.tasks.Shell'
        job.node.prebuilders[0].children()[0].command[0].value() == 'ls'
    }

    def 'cannot run postBuildSteps for freestyle jobs'() {
        when:
        job.postBuildSteps {
        }

        then:
        thrown(IllegalStateException)
    }

    def 'can add postBuildSteps'() {
        setup:
        job.type = JobType.Maven

        when:
        job.postBuildSteps {
            shell('ls')
        }

        then:
        job.node.postbuilders[0].children()[0].name() == 'hudson.tasks.Shell'
        job.node.postbuilders[0].children()[0].command[0].value() == 'ls'
    }

    def 'cannot run mavenInstallation for free style jobs'() {
        when:
        job.mavenInstallation('test')

        then:
        thrown(IllegalStateException)
    }

    def 'mavenInstallation constructs xml'() {
        setup:
        job.type = JobType.Maven

        when:
        job.mavenInstallation('test')

        then:
        job.node.mavenName.size() == 1
        job.node.mavenName[0].value() == 'test'
    }

    def 'call maven method with unknown provided settings'() {
        setup:
        job.type = JobType.Maven
        String settingsName = 'lalala'

        when:
        job.providedSettings(settingsName)

        then:
        Exception e = thrown(NullPointerException)
        e.message.contains(settingsName)
    }

    def 'call maven method with provided settings'() {
        setup:
        job.type = JobType.Maven
        String settingsName = 'maven-proxy'
        String settingsId = '123123415'
        jobManagement.getConfigFileId(ConfigFileType.MavenSettings, settingsName) >> settingsId

        when:
        job.providedSettings(settingsName)

        then:
        job.node.settings.size() == 1
        job.node.settings[0].attribute('class') == 'org.jenkinsci.plugins.configfiles.maven.job.MvnSettingsProvider'
        job.node.settings[0].children().size() == 1
        job.node.settings[0].settingsConfigId[0].value() == settingsId
    }

    def 'add description'() {
        when:
        job.description('Description')

        then:
        job.node.description[0].value() == 'Description'

        when:
        job.description('Description2')

        then:
        job.node.description.size() == 1
        job.node.description[0].value() == 'Description2'

    }

    def 'environments work with map arg'() {
        when:
        job.environmentVariables([
                key1: 'val1',
                key2: 'val2'
        ])

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
    }

    def 'environments work with context'() {
        when:
        job.environmentVariables {
            envs([key1: 'val1', key2: 'val2'])
            env 'key3', 'val3'
        }

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
    }

    def 'environments work with combination'() {
        when:
        job.environmentVariables([key4: 'val4']) {
            env 'key3', 'val3'
        }

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key4=val4')
    }

    def 'environment from groovy script'() {
        when:
        job.environmentVariables {
            groovy '[foo: "bar"]'
        }

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].groovyScriptContent[0].value() == '[foo: "bar"]'
    }

    def 'environment from map and groovy script'() {
        when:
        job.environmentVariables {
            envs([key1: 'val1', key2: 'val2'])
            env 'key3', 'val3'
            groovy '[foo: "bar"]'
        }

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].groovyScriptContent[0].value() == '[foo: "bar"]'
    }

    @Unroll
    def 'environment from #method'(content, method, xmlElement) {
        when:
        job.environmentVariables {
            "$method"(content)
        }

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0]."$xmlElement"[0].value() == content

        where:
        content          || method               || xmlElement
        'some.properties' | 'propertiesFile'      | 'propertiesFilePath'
        '/some/path'      | 'scriptFile'          | 'scriptFilePath'
        'echo "Yeah"'     | 'script'              | 'scriptContent'
        true              | 'loadFilesFromMaster' | 'loadFilesFromMaster'
    }

    @Unroll
    def 'environment sets #method to #content'(method, content, xmlElement) {
        when:
        job.environmentVariables {
            "${method}"(content)
        }

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0]."${xmlElement}"[0].value() == content

        where:
        method               || content || xmlElement
        'keepSystemVariables' | true     | 'keepJenkinsSystemVariables'
        'keepSystemVariables' | false    | 'keepJenkinsSystemVariables'
        'keepBuildVariables'  | true     | 'keepBuildVariables'
        'keepBuildVariables'  | false    | 'keepBuildVariables'
    }

    def 'throttle concurrents enabled as project alone'() {
        when:
        job.throttleConcurrentBuilds {
            maxPerNode 1
            maxTotal 2
        }

        then:
        def throttleNode = job.node.properties[0].'hudson.plugins.throttleconcurrents.ThrottleJobProperty'[0]

        throttleNode.maxConcurrentPerNode[0].value() == 1
        throttleNode.maxConcurrentTotal[0].value() == 2
        throttleNode.throttleEnabled[0].value() == 'true'
        throttleNode.throttleOption[0].value() == 'project'
        throttleNode.categories[0].children().size() == 0
    }

    def 'throttle concurrents disabled'() {
        when:
        job.throttleConcurrentBuilds {
            throttleDisabled()
        }

        then:
        def throttleNode = job.node.properties[0].'hudson.plugins.throttleconcurrents.ThrottleJobProperty'[0]
        throttleNode.throttleEnabled[0].value() == 'false'
    }

    def 'throttle concurrents enabled as part of categories'() {
        when:
        job.throttleConcurrentBuilds {
            maxPerNode 1
            maxTotal 2
            categories(['cat-1', 'cat-2'])
        }

        then:
        def throttleNode = job.node.properties[0].'hudson.plugins.throttleconcurrents.ThrottleJobProperty'[0]
        throttleNode.maxConcurrentPerNode[0].value() == 1
        throttleNode.maxConcurrentTotal[0].value() == 2
        throttleNode.throttleEnabled[0].value() == 'true'
        throttleNode.throttleOption[0].value() == 'category'
        throttleNode.categories[0].children().size() == 2
        throttleNode.categories[0].string[0].value() == 'cat-1'
        throttleNode.categories[0].string[1].value() == 'cat-2'
    }

    def 'disable defaults to true'() {
        when:
        job.disabled()

        then:
        job.node.disabled.size() == 1
        job.node.disabled[0].value() == true

        when:
        job.disabled(false)

        then:
        job.node.disabled.size() == 1
        job.node.disabled[0].value() == false
    }

    def 'label constructs xml'() {
        when:
        job.label('FullTools')

        then:
        job.node.assignedNode[0].value() == 'FullTools'
        job.node.canRoam[0].value() == false
    }

    def 'without label leaves canRoam as true'() {
        when:
        job.label()

        then:
        job.node.assignedNode[0].value() == ''
        job.node.canRoam[0].value() == true
    }

    def 'lockable resources simple'() {
        when:
        job.lockableResources('lock-resource')

        then:
        with(job.node.properties[0].'org.jenkins.plugins.lockableresources.RequiredResourcesProperty'[0]) {
            children().size() == 1
            resourceNames.size() == 1
            resourceNamesVar.size() == 0
            resourceNumber.size() == 0
            resourceNames[0].value() == 'lock-resource'
        }
    }

    def 'lockable resources with all parameters'() {
        when:
        job.lockableResources('res0 res1 res2') {
            resourcesVariable('RESOURCES')
            resourceNumber(1)
        }

        then:
        with(job.node.properties[0].'org.jenkins.plugins.lockableresources.RequiredResourcesProperty'[0]) {
            children().size() == 3
            resourceNames.size() == 1
            resourceNamesVar.size() == 1
            resourceNumber.size() == 1
            resourceNames[0].value() == 'res0 res1 res2'
            resourceNamesVar[0].value() == 'RESOURCES'
            resourceNumber[0].value() == 1
        }
    }

    def 'log rotate xml'() {
        when:
        job.logRotator(14, 50)

        then:
        job.node.logRotator[0].daysToKeep[0].value() == 14
        job.node.logRotator[0].numToKeep[0].value() == 50
    }

    def 'build blocker xml'() {
        when:
        job.blockOn('MyProject')

        then:
        with(job.node.properties[0].'hudson.plugins.buildblocker.BuildBlockerProperty'[0]) {
            useBuildBlocker[0].value() == 'true'
            blockingJobs[0].value() == 'MyProject'
        }
    }

    def 'can run jdk'() {
        when:
        job.jdk('JDK1.6.0_32')

        then:
        job.node.jdk[0].value() == 'JDK1.6.0_32'
    }

    def 'can run jdk twice'() {
        when:
        job.jdk('JDK1.6.0_16')

        then:
        job.node.jdk[0].value() == 'JDK1.6.0_16'

        when:
        job.jdk('JDK1.6.0_17')

        then:
        job.node.jdk.size() == 1
        job.node.jdk[0].value() == 'JDK1.6.0_17'
    }

    def 'priority constructs xml'() {
        when:
        job.priority(99)

        then:
        job.node.properties.'hudson.queueSorter.PrioritySorterJobProperty'.priority[0].value() == 99
    }

    def 'add a quiet period'() {
        when:
        job.quietPeriod()

        then:
        job.node.quietPeriod[0].value() == 5

        when:
        job.quietPeriod(10)

        then:
        job.node.quietPeriod[0].value() == 10
    }

    def 'add SCM retry count' () {
        when:
        job.checkoutRetryCount()

        then:
        job.node.scmCheckoutRetryCount[0].value() == 3

        when:
        job.checkoutRetryCount(6)

        then:
        job.node.scmCheckoutRetryCount[0].value() == 6
    }

    def 'add display name' () {
        when:
        job.displayName('FooBar')

        then:
        job.node.displayName[0].value() == 'FooBar'
    }

    def 'add custom workspace' () {
        when:
        job.customWorkspace('/var/lib/jenkins/foobar')

        then:
        job.node.customWorkspace[0].value() == '/var/lib/jenkins/foobar'
    }

    def 'add block for up and downstream projects' () {
        when:
        job.blockOnUpstreamProjects()

        then:
        job.node.blockBuildWhenUpstreamBuilding[0].value() == true

        when:
        job.blockOnDownstreamProjects()

        then:
        job.node.blockBuildWhenDownstreamBuilding[0].value() == true
    }

    def 'set keep Dependencies'(keep) {
        when:
        job.keepDependencies(keep)

        then:
        job.node.keepDependencies.size() == 1
        job.node.keepDependencies[0].value() == keep

        where:
        keep << [true, false]
    }

    def 'set concurrentBuild with value'(allowConcurrentBuild) {
        when:
        job.concurrentBuild(allowConcurrentBuild)

        then:
        job.node.concurrentBuild.size() == 1
        job.node.concurrentBuild[0].value() == allowConcurrentBuild ? 'true' : 'false'

        where:
        allowConcurrentBuild << [true, false]
    }

    def 'set concurrentBuild default'() {
        when:
        job.concurrentBuild()

        then:
        job.node.concurrentBuild[0].value() == true
    }

    def 'add batch task'() {
        when:
        job.batchTask('Hello World', 'echo Hello World')

        then:
        job.node.'properties'.size() == 1
        job.node.'properties'[0].'hudson.plugins.batch__task.BatchTaskProperty'.size() == 1
        with(job.node.'properties'[0].'hudson.plugins.batch__task.BatchTaskProperty'[0]) {
            tasks.size() == 1
            tasks[0].'hudson.plugins.batch__task.BatchTask'.size() == 1
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].children().size() == 2
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].'name'[0].value() == 'Hello World'
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].'script'[0].value() == 'echo Hello World'
        }
    }

    def 'add two batch tasks'() {
        when:
        job.batchTask('Hello World', 'echo Hello World')
        job.batchTask('foo', 'echo bar')

        then:
        job.node.'properties'.size() == 1
        job.node.'properties'[0].'hudson.plugins.batch__task.BatchTaskProperty'.size() == 1
        with(job.node.'properties'[0].'hudson.plugins.batch__task.BatchTaskProperty'[0]) {
            tasks.size() == 1
            tasks[0].'hudson.plugins.batch__task.BatchTask'.size() == 2
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].children().size() == 2
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].'name'[0].value() == 'Hello World'
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].'script'[0].value() == 'echo Hello World'
            tasks[0].'hudson.plugins.batch__task.BatchTask'[1].children().size() == 2
            tasks[0].'hudson.plugins.batch__task.BatchTask'[1].'name'[0].value() == 'foo'
            tasks[0].'hudson.plugins.batch__task.BatchTask'[1].'script'[0].value() == 'echo bar'
        }
    }

    def 'delivery pipeline configuration with stage and task names'() {
        when:
        job.deliveryPipelineConfiguration('qa', 'integration-tests')

        then:
        job.node.'properties'.size() == 1
        job.node.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'.size() == 1
        with(job.node.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'[0]) {
            children().size() == 2
            taskName[0].value() == 'integration-tests'
            stageName[0].value() == 'qa'
        }
    }

    def 'delivery pipeline configuration with stage name'() {
        when:
        job.deliveryPipelineConfiguration('qa')

        then:
        job.node.'properties'.size() == 1
        job.node.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'.size() == 1
        with(job.node.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'[0]) {
            children().size() == 1
            stageName[0].value() == 'qa'
        }
    }

    def 'delivery pipeline configuration with task name'() {
        when:
        job.deliveryPipelineConfiguration(null, 'integration-tests')

        then:
        job.node.'properties'.size() == 1
        job.node.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'.size() == 1
        with(job.node.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'[0]) {
            children().size() == 1
            taskName[0].value() == 'integration-tests'
        }
    }

    def 'set custom icon'() {
        when:
        job.customIcon('myfancyicon.png')

        then:
        job.node.'properties'.size() == 1
        job.node.'properties'[0].'jenkins.plugins.jobicon.CustomIconProperty'.size() == 1
        with(job.node.'properties'[0].'jenkins.plugins.jobicon.CustomIconProperty'[0]) {
            children().size() == 1
            iconfile[0].value() == 'myfancyicon.png'
        }
    }

    def 'set notification with default properties'() {
        when:
        job.notifications {
            endpoint('http://endpoint.com')
        }

        then:
        with(job.node.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty') {
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 1
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].children().size() == 3
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint.com'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'HTTP'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'JSON'
        }
    }

    def 'set notification with all required properties'() {
        when:
        job.notifications {
            endpoint('http://endpoint.com', 'TCP', 'XML')
        }

        then:
        with(job.node.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty') {
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 1
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].children().size() == 3
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint.com'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'TCP'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'XML'
        }
    }

    def 'set notification with invalid parameters'(String url, String protocol, String format, String event) {
        setup:
        jobManagement.getPluginVersion('notification') >> new VersionNumber('1.6')

        when:
        job.notifications {
            endpoint(url, protocol, format) {
                delegate.event(event)
            }
        }

        then:
        thrown(IllegalArgumentException)

        where:
        url        | protocol | format  | event
        'foo:2300' | 'TCP'    | 'what?' | 'all'
        'foo:2300' | 'TCP'    | ''      | 'all'
        'foo:2300' | 'TCP'    | null    | 'all'
        'foo:2300' | 'test'   | 'JSON'  | 'all'
        'foo:2300' | ''       | 'JSON'  | 'all'
        'foo:2300' | null     | 'JSON'  | 'all'
        ''         | 'TCP'    | 'JSON'  | 'all'
        null       | 'TCP'    | 'JSON'  | 'all'
        'foo:2300' | 'TCP'    | 'JSON'  | 'acme'
        'foo:2300' | 'TCP'    | 'JSON'  | ''
        'foo:2300' | 'TCP'    | 'JSON'  | null
    }

    def 'set notification with default properties and using a closure'() {
        setup:
        jobManagement.getPluginVersion('notification') >> new VersionNumber('1.6')

        when:
        job.notifications {
            endpoint('http://endpoint.com') {
                event('started')
                timeout(10000)
            }
        }

        then:
        with(job.node.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty') {
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 1
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].children().size() == 5
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint.com'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'HTTP'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'JSON'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].event[0].text() == 'started'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].timeout[0].value() == 10000
        }
    }

    def 'set notification with all required properties and using a closure'() {
        setup:
        jobManagement.getPluginVersion('notification') >> new VersionNumber('1.6')

        when:
        job.notifications {
            endpoint('http://endpoint.com', 'TCP', 'XML') {
                event('started')
                timeout(10000)
            }
        }

        then:
        with(job.node.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty') {
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 1
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].children().size() == 5
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint.com'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'TCP'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'XML'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].event[0].text() == 'started'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].timeout[0].value() == 10000
        }
    }

    def 'set notification with multiple endpoints'() {
        setup:
        jobManagement.getPluginVersion('notification') >> new VersionNumber('1.6')

        when:
        job.notifications {
            endpoint('http://endpoint1.com')
            endpoint('http://endpoint2.com')
        }

        then:
        with(job.node.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty') {
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 2

            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].children().size() == 5
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint1.com'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'HTTP'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'JSON'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].event[0].text() == 'all'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].timeout[0].value() == 30000

            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].children().size() == 5
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].url[0].text() == 'http://endpoint2.com'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].protocol[0].text() == 'HTTP'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].format[0].text() == 'JSON'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].event[0].text() == 'all'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].timeout[0].value() == 30000
        }
    }

    private final minimalXml = '''
<project>
  <actions/>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
</project>
'''

    private final mavenXml = '''
<maven2-moduleset>
    <actions/>
    <description></description>
    <keepDependencies>false</keepDependencies>
    <properties/>
    <scm class="hudson.scm.NullSCM"/>
    <canRoam>true</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
    <triggers class="vector"/>
    <concurrentBuild>false</concurrentBuild>
    <aggregatorStyleBuild>true</aggregatorStyleBuild>
    <incrementalBuild>false</incrementalBuild>
    <perModuleEmail>false</perModuleEmail>
    <ignoreUpstremChanges>true</ignoreUpstremChanges>
    <archivingDisabled>false</archivingDisabled>
    <resolveDependencies>false</resolveDependencies>
    <processPlugins>false</processPlugins>
    <mavenValidationLevel>-1</mavenValidationLevel>
    <runHeadless>false</runHeadless>
    <publishers/>
    <buildWrappers/>
</maven2-moduleset>
'''

    private final buildFlowXml = '''
<com.cloudbees.plugins.flow.BuildFlow>
  <actions/>
  <description></description>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <scm class="hudson.scm.NullSCM"/>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers class="vector"/>
  <concurrentBuild>false</concurrentBuild>
  <builders/>
  <publishers/>
  <buildWrappers/>
  <icon/>
  <dsl></dsl>
</com.cloudbees.plugins.flow.BuildFlow>
'''

private final matrixJobXml = '''
<matrix-project>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <scm class="hudson.scm.NullSCM"/>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers class="vector"/>
  <concurrentBuild>false</concurrentBuild>
  <axes/>
  <builders/>
  <publishers/>
  <buildWrappers/>
  <executionStrategy class="hudson.matrix.DefaultMatrixExecutionStrategyImpl">
    <runSequentially>false</runSequentially>
  </executionStrategy>
</matrix-project>
'''
}
