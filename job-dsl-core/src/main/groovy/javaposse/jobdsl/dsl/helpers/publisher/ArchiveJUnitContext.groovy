package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

class ArchiveJUnitContext implements Context {
    final TestDataPublishersContext testDataPublishersContext
    boolean retainLongStdout = false

    ArchiveJUnitContext(JobManagement jobManagement) {
        testDataPublishersContext = new TestDataPublishersContext(jobManagement)
    }

    void retainLongStdout(boolean retain = true) {
        retainLongStdout = retain
    }

    void testDataPublishers(Closure testDataPublishersClosure) {
        AbstractContextHelper.executeInContext(testDataPublishersClosure, testDataPublishersContext)
    }
}
