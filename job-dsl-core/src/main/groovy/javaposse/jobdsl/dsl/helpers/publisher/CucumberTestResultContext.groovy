package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class CucumberTestResultContext implements Context {
    String jsonReportFiles
    boolean ignoreBadSteps

    /**
     * Sets the path to Cucumber JSON files in the Ant glob syntax.
     */
    void jsonReportFiles(String jsonReportFiles) {
        this.jsonReportFiles = jsonReportFiles
    }

    /**
     * If set, ignores bad steps. Defaults to {@code false}.
     */
    void ignoreBadSteps(boolean ignoreBadSteps = true) {
        this.ignoreBadSteps = ignoreBadSteps
    }
}
