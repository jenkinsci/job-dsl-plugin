package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class SeleniumReportContext implements Context {
    boolean useTestCommands = false

    /**
     * If set, Use status of test commands instead of suites to determine UNSTABLE/FAILURE/SUCCESS.
     * Defaults to {@code false}.
     */
    void useTestCommands(boolean useTestCommands = true) {
        this.useTestCommands = useTestCommands
    }
}
