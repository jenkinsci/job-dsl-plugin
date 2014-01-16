package javaposse.jobdsl.dsl.helpers.publisher

import groovy.transform.Canonical
import javaposse.jobdsl.dsl.helpers.Context

class HtmlReportContext implements Context {
    def targets = []

    def report(String reportDir, String reportName = null, String reportFiles = null, Boolean keepAll = null) {

        if (!reportDir) {
            throw new RuntimeException("Report directory for html publisher is required")
        }
        targets << new HtmlPublisherTarget(
                reportName: reportName ?: '',
                reportDir: reportDir ?: '',
                reportFiles: reportFiles ?: 'index.html',
                keepAll: keepAll ? 'true' : 'false',
                wrapperName: 'htmlpublisher-wrapper.html')
    }

    def report(Map args) {
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
