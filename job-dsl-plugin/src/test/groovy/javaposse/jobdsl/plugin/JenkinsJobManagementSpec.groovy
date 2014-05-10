package javaposse.jobdsl.plugin

import com.cloudbees.hudson.plugins.folder.Folder
import hudson.model.Failure
import hudson.model.FreeStyleProject
import javaposse.jobdsl.dsl.ConfigurationMissingException
import javaposse.jobdsl.dsl.NameNotProvidedException
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class JenkinsJobManagementSpec extends Specification {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    JenkinsJobManagement jobManagement = new JenkinsJobManagement()

    def MINIMAL_PROJECT_CONFIG = """
        <project>
            <actions/>
            <properties/>
            <scm class="hudson.scm.NullSCM"/>
            <disabled>false</disabled>
            <triggers/>
            <builders/>
            <publishers/>
            <buildWrappers/>
        </project>"""

    def 'getJobNameFromFullName'() {
        expect:
        JenkinsJobManagement.getJobNameFromFullName(fullName) == jobName

        where:
        fullName     || jobName
        'a/b/c'      || 'c'
        'folder/job' || 'job'
        'myjob'      || 'myjob'
    }

    def 'createOrUpdateView without name'() {
        when:
        jobManagement.createOrUpdateView(null, "<View/>", true)

        then:
        thrown(NameNotProvidedException)

        when:
        jobManagement.createOrUpdateView("", "<View/>", true)

        then:
        thrown(NameNotProvidedException)
    }

    def 'createOrUpdateView without config'() {
        when:
        jobManagement.createOrUpdateView("test", null, true)

        then:
        thrown(ConfigurationMissingException)

        when:
        jobManagement.createOrUpdateView("test", null, true)

        then:
        thrown(ConfigurationMissingException)
    }

    def 'createOrUpdateView with invalid name'() {
        when:
        jobManagement.createOrUpdateView("t<e*st", "<View/>", true)

        then:
        thrown(Failure)
    }

    def 'createOrUpdateConfig relative to folder'() {
        setup:
        Folder folder = jenkinsRule.jenkins.createProject(Folder.class, 'folder')
        FreeStyleProject project = folder.createProject(FreeStyleProject.class, 'project')
        jobManagement.build = project.scheduleBuild2(0).get()

        when:
        jobManagement.createOrUpdateConfig('project', MINIMAL_PROJECT_CONFIG, true)

        then:
        jenkinsRule.jenkins.getItemByFullName('/folder/project') != null
    }
}
