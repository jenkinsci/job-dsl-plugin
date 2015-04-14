package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class HtmlReportTargetContext extends AbstractContext {
    final String reportDir

    String reportName = ''
    String reportFiles = 'index.html'
    boolean keepAll
    boolean allowMissing

    HtmlReportTargetContext(JobManagement jobManagement, String reportDir) {
        super(jobManagement)
        this.reportDir = reportDir
    }

    void reportName(String reportName) {
        this.reportName = reportName
    }

    void reportFiles(String reportFiles) {
        this.reportFiles = reportFiles
    }

    void keepAll(boolean keepAll = true) {
        this.keepAll = keepAll
    }

    @RequiresPlugin(id = 'htmlpublisher', minimumVersion = '1.3')
    void allowMissing(boolean allowMissing = true) {
        this.allowMissing = allowMissing
    }
}
