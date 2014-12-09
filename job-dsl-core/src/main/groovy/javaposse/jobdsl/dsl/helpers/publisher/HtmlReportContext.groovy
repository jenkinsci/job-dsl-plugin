package javaposse.jobdsl.dsl.helpers.publisher

import groovy.transform.Canonical
import javaposse.jobdsl.dsl.helpers.Context

import static com.google.common.base.Preconditions.checkArgument

class HtmlReportContext implements Context {
    List<HtmlPublisherTarget> targets = []

    void report(String reportDir, String reportName = null, String reportFiles = null, Boolean keepAll = null) {
        checkArgument(reportDir != null && reportDir.length() > 0, 'Report directory for html publisher is required')

        targets << new HtmlPublisherTarget(
                reportName: reportName ?: '',
                reportDir: reportDir ?: '',
                reportFiles: reportFiles ?: 'index.html',
                keepAll: keepAll ? 'true' : 'false',
                wrapperName: 'htmlpublisher-wrapper.html')
    }

    void report(Map args) {
        report(args.reportDir, args.reportName, args.reportFiles, args.keepAll)
    }

    @Canonical
    static class HtmlPublisherTarget {
        String reportName
        String reportDir
        String reportFiles
        String keepAll
        String wrapperName // Not sure what this is for
    }
}
