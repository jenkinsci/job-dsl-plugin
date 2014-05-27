package javaposse.jobdsl.plugin

import hudson.EnvVars
import hudson.model.AbstractBuild
import hudson.model.Failure
import javaposse.jobdsl.dsl.ConfigurationMissingException
import javaposse.jobdsl.dsl.NameNotProvidedException
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

import static hudson.model.Result.UNSTABLE

class JenkinsJobManagementSpec extends Specification {
    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule();

    ByteArrayOutputStream buffer = new ByteArrayOutputStream()
    AbstractBuild build = Mock(AbstractBuild)
    JenkinsJobManagement jobManagement = new JenkinsJobManagement(new PrintStream(buffer), new EnvVars(), build)

    def 'getItemNameFromFullName'() {
        expect:
        JenkinsJobManagement.getItemNameFromFullName(fullName) == itemName

        where:
        fullName     || itemName
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
}
