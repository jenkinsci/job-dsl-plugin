package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkNotNull

class JobsContext implements Context {
    Set<String> jobNames = []
    String regex

    /**
     * Adds jobs to the view. Can be called multiple times to added more jobs.
     */
    void name(String jobName) {
        checkNotNull(jobName, 'jobName must not be null')

        this.jobNames.add(jobName)
    }

    /**
     * Adds jobs to the view. Can be called multiple times to added more jobs.
     */
    void names(String... jobNames) {
        for (String jobName : jobNames) {
            name(jobName)
        }
    }

    /**
     * If configured, the regular expression will be applied to all job names. Those that match the regular expression
     * will be shown in the view.
     */
    void regex(String regex) {
        this.regex = regex
    }
}
