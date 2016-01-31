package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class ArchiveJUnitContext extends AbstractContext {
    final TestDataPublishersContext testDataPublishersContext
    boolean allowEmptyResults = false
    boolean retainLongStdout = false

    ArchiveJUnitContext(JobManagement jobManagement) {
        super(jobManagement)
        testDataPublishersContext = new TestDataPublishersContext(jobManagement)
    }

    /**
     * If set, does not fail the build on empty test results.
     *
     * @since 1.43
     */
    @RequiresPlugin(id = 'junit', minimumVersion = '1.10')
    void allowEmptyResults(boolean allow = true) {
        allowEmptyResults = allow
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
