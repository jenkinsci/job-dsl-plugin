package javaposse.jobdsl.dsl.helpers.publisher

import groovy.transform.Canonical
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class PostBuildTaskMultiplyContext implements Context {
    List<PostBuildTask> tasks = []

    /**
     * Adds a script that will be executed if the output in the console log matches the regular expression. Can be
     * called multiple times to add more scripts.
     */
    void task(List<String[]> conditions, String script, boolean escalate = false,
              boolean runIfSuccessful = false) {
        Preconditions.checkNotNullOrEmpty(script, 'Script to run is required!')

        tasks << new PostBuildTask(
                conditions: conditions,
                script: script,
                escalateStatus: escalate,
                runIfJobSuccessful: runIfSuccessful
        )
    }

    @Canonical
    static class PostBuildTask {
        List<String[]> conditions
        String script
        boolean escalateStatus
        boolean runIfJobSuccessful
    }
}
