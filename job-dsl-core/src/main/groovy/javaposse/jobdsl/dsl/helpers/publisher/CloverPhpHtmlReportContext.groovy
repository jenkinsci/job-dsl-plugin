package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class CloverPhpHtmlReportContext implements Context {
    boolean disableArchiving

    /**
     * If set, does not archive HTML reports. Defaults to {@code false}.
     */
    void disableArchiving(boolean change = true) {
        this.disableArchiving = change
    }
}
