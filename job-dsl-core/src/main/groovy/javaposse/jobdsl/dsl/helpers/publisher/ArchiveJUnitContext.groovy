package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

class ArchiveJUnitContext implements Context {
    final TestDataPublishersContext testDataPublishersContext
    boolean retainLongStdout = false

    ArchiveJUnitContext(JobManagement jobManagement) {
        testDataPublishersContext = new TestDataPublishersContext(jobManagement)
    }

    /**
     * If set, retains any standard output or error from a test suite in the test results after the build completes.
     */
    void retainLongStdout(boolean retain = true) {
        retainLongStdout = retain
    }

    /**
     * Adds additional test report features provided by other Jenkins plugins.
     */
    void testDataPublishers(@DslContext(TestDataPublishersContext) Closure testDataPublishersClosure) {
        ContextHelper.executeInContext(testDataPublishersClosure, testDataPublishersContext)
    }
}
