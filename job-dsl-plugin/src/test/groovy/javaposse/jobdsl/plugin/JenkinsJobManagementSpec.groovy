package javaposse.jobdsl.plugin

import hudson.model.Failure
import javaposse.jobdsl.dsl.ConfigurationMissingException
import javaposse.jobdsl.dsl.NameNotProvidedException
import spock.lang.Specification

class JenkinsJobManagementSpec extends Specification {
    JenkinsJobManagement jobManagement = new JenkinsJobManagement()

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
}
