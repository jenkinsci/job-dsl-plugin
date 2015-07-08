package javaposse.jobdsl.dsl.helpers.publisher

import groovy.transform.Canonical
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class PostBuildTaskContext implements Context {
    List<PostBuildTask> tasks = []

    void task(String logText, String script, boolean escalate = false, boolean runIfSuccessful = false) {
        Preconditions.checkNotNullOrEmpty(logText, 'Log Text to match is required!')
        Preconditions.checkNotNullOrEmpty(script, 'Script to run is required!')

        tasks << new PostBuildTask(
            logText: logText,
            script: script,
            operator: 'AND',
            escalateStatus: escalate,
            runIfJobSuccessful: runIfSuccessful
        )
    }

    @Canonical
    static class PostBuildTask {
        String logText
        String operator
        String script
        boolean escalateStatus
        boolean runIfJobSuccessful
    }
}
