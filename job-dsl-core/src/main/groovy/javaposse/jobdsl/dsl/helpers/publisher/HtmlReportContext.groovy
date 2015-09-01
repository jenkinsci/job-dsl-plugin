package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions

class HtmlReportContext extends AbstractContext {
    final List<HtmlReportTargetContext> targets = []

    HtmlReportContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds a report to publish. Can be called multiple times to add publish reports.
     */
    void report(String reportDir, @DslContext(HtmlReportTargetContext) Closure closure = null) {
        Preconditions.checkNotNullOrEmpty(reportDir, 'Report directory for html publisher is required')

        HtmlReportTargetContext context = new HtmlReportTargetContext(jobManagement, reportDir)
        ContextHelper.executeInContext(closure, context)

        targets << context
    }
}
