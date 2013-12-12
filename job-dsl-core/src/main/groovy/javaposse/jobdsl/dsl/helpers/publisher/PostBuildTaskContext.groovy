package javaposse.jobdsl.dsl.helpers.publisher

import com.google.common.base.Preconditions
import groovy.transform.Canonical
import javaposse.jobdsl.dsl.helpers.Context

class PostBuildTaskContext implements Context {
	def tasks = []
	
	// TODO Allow for multiple pairs of logText and operator! Can we add a smart dsl method for the two operators: AND and OR?
	def task(String logText, String script, String operator = 'AND', boolean escalate = false, boolean runIfSuccessful = false) {
        Preconditions.checkArgument(logText != null && logText.length() > 0, "Log Text to match is required!")
        Preconditions.checkArgument(script != null && script.length() > 0, "Script to run is required!")

        tasks << new PostBuildTask(
            logText: logText,
            script: script,
            operator: operator,
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
