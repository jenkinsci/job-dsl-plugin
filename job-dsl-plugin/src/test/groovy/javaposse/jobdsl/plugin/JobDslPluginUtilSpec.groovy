package javaposse.jobdsl.plugin

import spock.lang.Specification

@SuppressWarnings("GroovyAccessibility")
class JobDslPluginUtilSpec extends Specification {

    def 'getJobNameFromFullName'() {
        expect:
        JobDslPluginUtil.getJobNameFromFullName(fullName) == jobName

        where:
        fullName     || jobName
        'a/b/c'      || 'c'
        'folder/job' || 'job'
        'myjob'      || 'myjob'
    }

}
