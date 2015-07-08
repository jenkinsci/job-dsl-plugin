package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkNotNull

class JobsContext implements Context {
    Set<String> jobNames = []
    String regex

    void name(String jobName) {
        checkNotNull(jobName, 'jobName must not be null')

        this.jobNames.add(jobName)
    }

    void names(String... jobNames) {
        for (String jobName : jobNames) {
            name(jobName)
        }
    }

    void regex(String regex) {
        this.regex = regex
    }
}
