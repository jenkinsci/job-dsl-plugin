package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class SeleniumReportContext implements Context {
    boolean useTestCommands

    /**
     * If set, uses the status of test commands instead of suites to determine build result.
     * Defaults to {@code false}.
     */
    void useTestCommands(boolean useTestCommands = true) {
        this.useTestCommands = useTestCommands
    }
}
