package javaposse.jobdsl.dsl.helpers.publisher;

import groovy.lang.Closure;
import javaposse.jobdsl.dsl.Context;
import javaposse.jobdsl.dsl.ContextHelper;
import javaposse.jobdsl.dsl.DslContext;
import javaposse.jobdsl.dsl.JobManagement;

/**
 * Created by tomcat on 6/10/15.
 */
public class ArchiveTestNGContext implements Context {
    final TestNGDataPublishersContext testDataPublishersContext
    boolean escapeTestDescription = false
    boolean escapeExceptionMessages = false
    boolean showFailedBuildsInTrendGraph = false
    boolean markBuildAsUnstableOnSkippedTests = false
    boolean markBuildAsFailureOnFailedConfiguration = false

    ArchiveTestNGContext(JobManagement jobManagement) {
        testDataPublishersContext = new TestNGDataPublishersContext(jobManagement)
    }

    void escapeTestDescription(boolean escape = true) {
        escapeTestDescription = escape
    }

    void escapeExceptionMessages(boolean escape = true) {
        escapeExceptionMessages = escape
    }

    void showFailedBuildsInTrendGraph(boolean show = true) {
        showFailedBuildsInTrendGraph = show
    }

    void markBuildAsUnstableOnSkippedTests(boolean mark = true) {
        markBuildAsUnstableOnSkippedTests = mark
    }

    void markBuildAsFailureOnFailedConfiguration(boolean mark = true) {
        markBuildAsFailureOnFailedConfiguration = mark
    }


    void testDataPublishers(@DslContext(TestNGDataPublishersContext) Closure testDataPublishersClosure) {
        ContextHelper.executeInContext(testDataPublishersClosure, testDataPublishersContext)
    }
}
