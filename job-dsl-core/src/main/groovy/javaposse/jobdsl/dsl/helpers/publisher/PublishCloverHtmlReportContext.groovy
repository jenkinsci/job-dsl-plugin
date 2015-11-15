package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class PublishCloverHtmlReportContext implements Context {
    String reportDir
    boolean disableArchiving = false

    /**
     * Sets the path to the report folder.
     */
    void reportDir(String reportDir) {
        this.reportDir = reportDir
    }

    /**
     * If set, disable Archiving Html Reports. Defaults to {@code false}.
     */
    void disableArchiving(boolean change = true) {
        this.disableArchiving = change
    }
}
