package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class RundeckContext implements Context {
    Map<String, String> options = [:]
    Map<String, String> nodeFilters = [:]
    String tag = ''
    boolean shouldWaitForRundeckJob
    boolean shouldFailTheBuild

    void options(Map<String, String> options) {
        this.options.putAll(options)
    }

    void option(String key, String value) {
        this.options[key] = value
    }

    void nodeFilters(Map<String, String> nodeFilters) {
        this.nodeFilters.putAll(nodeFilters)
    }

    void nodeFilter(String key, String value) {
        this.nodeFilters[key] = value
    }

    void tag(String tag) {
        this.tag = tag
    }

    void shouldWaitForRundeckJob(boolean shouldWaitForRundeckJob = true) {
        this.shouldWaitForRundeckJob = shouldWaitForRundeckJob
    }

    void shouldFailTheBuild(boolean shouldFailTheBuild = true) {
        this.shouldFailTheBuild = shouldFailTheBuild
    }
}
