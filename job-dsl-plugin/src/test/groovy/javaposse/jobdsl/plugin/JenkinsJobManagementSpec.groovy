package javaposse.jobdsl.plugin

import com.cloudbees.hudson.plugins.folder.Folder
import com.google.common.io.Resources
import hudson.EnvVars
import hudson.model.AbstractBuild
import hudson.model.Failure
import hudson.model.FreeStyleProject
import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.ConfigurationMissingException
import javaposse.jobdsl.dsl.NameNotProvidedException
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

import static com.google.common.base.Charsets.UTF_8
import static com.google.common.io.Resources.getResource
import static hudson.model.Result.UNSTABLE

class JenkinsJobManagementSpec extends Specification {
    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule()

    ByteArrayOutputStream buffer = new ByteArrayOutputStream()
    AbstractBuild build = Mock(AbstractBuild)
    JenkinsJobManagement jobManagement = new JenkinsJobManagement(new PrintStream(buffer), new EnvVars(), build)

    def 'getItemNameFromFullName'() {
        expect:
        JenkinsJobManagement.getItemNameFromPath(path) == itemName

        where:
        path          || itemName
        'a/b/c'       || 'c'
        'folder/job'  || 'job'
        'myjob'       || 'myjob'
        '/folder/job' || 'job'
        '/myjob'      || 'myjob'
    }

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

    def 'get vSphere cloud hash without vSphere cloud plugin'() {
        when:
        Integer hash = jobManagement.getVSphereCloudHash('test')

        then:
        hash == null
    }
}
