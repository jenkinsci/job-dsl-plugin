package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

class ArchiveJUnitContext extends AbstractContext {
    final TestDataPublishersContext testDataPublishersContext
    boolean allowEmptyResults
    boolean retainLongStdout
    double healthScaleFactor = 1.0

    ArchiveJUnitContext(JobManagement jobManagement) {
        super(jobManagement)
        testDataPublishersContext = new TestDataPublishersContext(jobManagement)
    }

    /**
     * If set, does not fail the build on empty test results. Defaults to {@code false}.
     *
     * @since 1.43
     */
    void allowEmptyResults(boolean allow = true) {
        allowEmptyResults = allow
    }

    /**
     * If set, retains any standard output or error from a test suite in the test results after the build completes.
     * Defaults to {@code false}.
     */
    void retainLongStdout(boolean retain = true) {
        retainLongStdout = retain
    }

    /**
     * Sets the amplification factor to apply to test failures when computing the test result contribution to the
     * build health score. The default factor is {@code 1.0}.
     *
     * @since 1.52
     */
    void healthScaleFactor(double factor) {
        healthScaleFactor = factor
    }

    /**
     * Adds additional test report features provided by other Jenkins plugins.
     */
    void testDataPublishers(@DslContext(TestDataPublishersContext) Closure testDataPublishersClosure) {
        ContextHelper.executeInContext(testDataPublishersClosure, testDataPublishersContext)
    }
}
