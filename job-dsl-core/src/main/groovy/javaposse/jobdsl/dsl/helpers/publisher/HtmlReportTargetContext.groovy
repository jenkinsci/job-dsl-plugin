package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class HtmlReportTargetContext extends AbstractContext {
    final String reportDir

    String reportName
    String reportFiles = 'index.html'
    String reportTitles
    boolean keepAll
    boolean allowMissing
    boolean alwaysLinkToLastBuild

    HtmlReportTargetContext(JobManagement jobManagement, String reportDir) {
        super(jobManagement)
        this.reportDir = reportDir
    }

    /**
     * Sets a title for the report.
     */
    void reportName(String reportName) {
        this.reportName = reportName
    }

    /**
     * Sets the path to the HTML report directory relative to the workspace.
     */
    void reportFiles(String reportFiles) {
        this.reportFiles = reportFiles
    }

    /**
     * Sets the title for HTML files.
     *
     * @since 1.64
     */
    void reportTitles(String reportFiles) {
        this.reportTitles = reportFiles
    }

    /**
     * If set, archives reports for all successful builds. Defaults to {@code false}.
     */
    void keepAll(boolean keepAll = true) {
        this.keepAll = keepAll
    }

    /**
     * If set, allows report to be missing. Defaults to {@code false}.
     */
    void allowMissing(boolean allowMissing = true) {
        this.allowMissing = allowMissing
    }

    /**
     * Publishes the link on project level even if build failed.
     *
     * @since 1.35
     */
    void alwaysLinkToLastBuild(boolean alwaysLinkToLastBuild = true) {
        this.alwaysLinkToLastBuild = alwaysLinkToLastBuild
    }
}
