package javaposse.jobdsl.dsl.helpers.publisher

import com.google.common.base.Strings
import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static com.google.common.base.Preconditions.checkArgument

class HtmlReportContext extends AbstractContext {
    final List<HtmlReportTargetContext> targets = []

    HtmlReportContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void report(String reportDir, @DslContext(HtmlReportTargetContext) Closure closure = null) {
        checkArgument(!Strings.isNullOrEmpty(reportDir), 'Report directory for html publisher is required')

        HtmlReportTargetContext context = new HtmlReportTargetContext(jobManagement, reportDir)
        ContextHelper.executeInContext(closure, context)

        targets << context
    }

    @Deprecated
    void report(String reportDir, String reportName, String reportFiles = null, Boolean keepAll = null) {
        jobManagement.logDeprecationWarning()

        report(reportDir) {
            if (reportName != null) {
                delegate.reportName(reportName)
            }
            if (reportFiles != null) {
                delegate.reportFiles(reportFiles)
            }
            if (keepAll != null) {
                delegate.keepAll(keepAll)
            }
        }
    }

    @Deprecated
    void report(Map args) {
        report(args.reportDir, args.reportName, args.reportFiles, args.keepAll)
    }
}
