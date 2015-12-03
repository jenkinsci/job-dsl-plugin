package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class SeleniumHtmlContext implements Context {
    boolean failOnExceptions = true

    /**
     * If set, Set build result state to failure if an exception occurred while parsing result files.
     * Defaults to {@code true}.
     */
    void failOnExceptions(boolean failOnExceptions = true) {
        this.failOnExceptions = failOnExceptions
    }
}
