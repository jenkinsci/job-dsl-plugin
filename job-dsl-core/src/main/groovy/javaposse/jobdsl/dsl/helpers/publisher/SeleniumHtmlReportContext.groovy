package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class SeleniumHtmlReportContext implements Context {
    boolean failOnExceptions = true

    /**
     * Sets the build result state to failure if an exception occurred while parsing result files.
     * Defaults to {@code true}.
     */
    void failOnExceptions(boolean failOnExceptions = true) {
        this.failOnExceptions = failOnExceptions
    }
}
