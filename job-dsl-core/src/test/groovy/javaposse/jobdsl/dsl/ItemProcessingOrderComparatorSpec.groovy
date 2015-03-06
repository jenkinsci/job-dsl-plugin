package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.BuildFlowJob
import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import javaposse.jobdsl.dsl.jobs.MavenJob
import javaposse.jobdsl.dsl.jobs.MultiJob
import spock.lang.Shared
import spock.lang.Specification

class ItemProcessingOrderComparatorSpec extends Specification {
    @Shared
    JobManagement jobManagement = Mock(JobManagement)

    def 'compare'(Item o1, Item o2, int result) {
        expect:
        new ItemProcessingOrderComparator().compare(o1, o2) == result

        where:
        o1                              | o2                              | result
        new Folder(jobManagement)       | new Folder(jobManagement)       | 0
        new Folder(jobManagement)       | new FreeStyleJob(jobManagement) | -1
        new FreeStyleJob(jobManagement) | new Folder(jobManagement)       | 1
        new BuildFlowJob(jobManagement) | new BuildFlowJob(jobManagement) | 0
        new MultiJob(jobManagement)     | new BuildFlowJob(jobManagement) | 1
        new BuildFlowJob(jobManagement) | new MultiJob(jobManagement)     | -1
        new MultiJob(jobManagement)     | new FreeStyleJob(jobManagement) | 1
        new FreeStyleJob(jobManagement) | new MultiJob(jobManagement)     | -1
        new MultiJob(jobManagement)     | new MavenJob(jobManagement)     | 1
        new MavenJob(jobManagement)     | new MultiJob(jobManagement)     | -1
        new MavenJob(jobManagement)     | new FreeStyleJob(jobManagement) | 0
        new MultiJob(jobManagement)     | new MultiJob(jobManagement)     | 0
    }
}
