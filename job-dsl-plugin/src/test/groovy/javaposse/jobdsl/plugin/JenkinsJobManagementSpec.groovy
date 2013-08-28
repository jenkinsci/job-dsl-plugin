package javaposse.jobdsl.plugin

import spock.lang.Specification

class JenkinsJobManagementSpec extends Specification {

    def 'getJobNameFromFullName'() {
        expect:
        JenkinsJobManagement.getJobNameFromFullName(fullName) == jobName

        where:
        fullName     || jobName
        'a/b/c'      || 'c'
        'folder/job' || 'job'
        'myjob'      || 'myjob'
    }

}
