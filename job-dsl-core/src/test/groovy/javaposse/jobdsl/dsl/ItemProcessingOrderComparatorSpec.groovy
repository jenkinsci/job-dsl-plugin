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
        o1                                      | o2                                      | result
        new Folder(jobManagement, 'test')       | new Folder(jobManagement, 'test')       | 0
        new Folder(jobManagement, 'test')       | new FreeStyleJob(jobManagement, 'test') | -1
        new FreeStyleJob(jobManagement, 'test') | new Folder(jobManagement, 'test')       | 1
        new BuildFlowJob(jobManagement, 'test') | new BuildFlowJob(jobManagement, 'test') | 0
        new MultiJob(jobManagement, 'test')     | new BuildFlowJob(jobManagement, 'test') | 1
        new BuildFlowJob(jobManagement, 'test') | new MultiJob(jobManagement, 'test')     | -1
        new MultiJob(jobManagement, 'test')     | new FreeStyleJob(jobManagement, 'test') | 1
        new FreeStyleJob(jobManagement, 'test') | new MultiJob(jobManagement, 'test')     | -1
        new MultiJob(jobManagement, 'test')     | new MavenJob(jobManagement, 'test')     | 1
        new MavenJob(jobManagement, 'test')     | new MultiJob(jobManagement, 'test')     | -1
        new MavenJob(jobManagement, 'test')     | new FreeStyleJob(jobManagement, 'test') | 0
        new MultiJob(jobManagement, 'test')     | new MultiJob(jobManagement, 'test')     | 0
    }
}
