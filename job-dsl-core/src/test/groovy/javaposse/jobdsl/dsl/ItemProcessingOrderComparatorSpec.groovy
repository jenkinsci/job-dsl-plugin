package javaposse.jobdsl.dsl

import spock.lang.Shared
import spock.lang.Specification

import static javaposse.jobdsl.dsl.JobType.BuildFlow
import static javaposse.jobdsl.dsl.JobType.Freeform
import static javaposse.jobdsl.dsl.JobType.Maven
import static javaposse.jobdsl.dsl.JobType.Multijob

class ItemProcessingOrderComparatorSpec extends Specification {
    @Shared
    JobManagement jobManagement = Mock(JobManagement)

    def 'compare'(Item o1, Item o2, int result) {
        expect:
        new ItemProcessingOrderComparator().compare(o1, o2) == result

        where:
        o1                                        | o2                                        | result
        new Folder(jobManagement)                 | new Folder(jobManagement)                 | 0
        new Folder(jobManagement)                 | new Job(jobManagement)                    | -1
        new Job(jobManagement)                    | new Folder(jobManagement)                 | 1
        new Job(jobManagement, [type: BuildFlow]) | new Job(jobManagement, [type: BuildFlow]) | 0
        new Job(jobManagement, [type: Multijob])  | new Job(jobManagement, [type: BuildFlow]) | 1
        new Job(jobManagement, [type: BuildFlow]) | new Job(jobManagement, [type: Multijob])  | -1
        new Job(jobManagement, [type: Multijob])  | new Job(jobManagement, [type: Freeform])  | 1
        new Job(jobManagement, [type: Freeform])  | new Job(jobManagement, [type: Multijob])  | -1
        new Job(jobManagement, [type: Multijob])  | new Job(jobManagement, [type: Maven])     | 1
        new Job(jobManagement, [type: Maven])     | new Job(jobManagement, [type: Multijob])  | -1
        new Job(jobManagement, [type: Maven])     | new Job(jobManagement, [type: Freeform])  | 0
        new Job(jobManagement, [type: Multijob])  | new Job(jobManagement, [type: Multijob])  | 0
    }
}
