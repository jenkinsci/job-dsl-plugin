package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class ParameterizedRemoteTriggerContext implements Context {
    Map<String, String> parameters = [:]
    boolean shouldNotFailBuild = false
    int pollInterval = 10
    boolean preventRemoteBuildQueue = false
    boolean blockBuildUntilComplete = false

    void parameter(String name, String value) {
        this.parameters[name] = value
    }

    void parameters(Map<String, String> parameters) {
        this.parameters.putAll(parameters)
    }

    void shouldNotFailBuild(boolean shouldNotFailBuild = true) {
        this.shouldNotFailBuild = shouldNotFailBuild
    }

    void pollInterval(int pollInterval) {
        this.pollInterval = pollInterval
    }

    boolean preventRemoteBuildQueue(boolean preventRemoteBuildQueue = true) {
        this.preventRemoteBuildQueue = preventRemoteBuildQueue
    }

    boolean blockBuildUntilComplete(boolean blockUntilBuildComplete = true) {
        this.blockBuildUntilComplete = blockUntilBuildComplete
    }
}
