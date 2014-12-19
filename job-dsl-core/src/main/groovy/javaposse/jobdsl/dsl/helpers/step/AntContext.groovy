package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class AntContext implements Context {
    List<String> targets = []
    List<String> props = []
    String buildFile = null
    List<String> antOpts = []
    String antName = null

    void target(String target) {
        targets << target
    }

    void targets(Iterable<String> addlTargets) {
        addlTargets.each {
            target(it)
        }
    }

    void prop(Object key, Object value) {
        props << "${key}=${value}"
    }

    void props(Map<String, String> map) {
        map.entrySet().each {
            prop(it.key, it.value)
        }
    }

    void buildFile(String buildFile) {
        this.buildFile = buildFile
    }

    void javaOpt(String opt) {
        antOpts << opt
    }

    void javaOpts(Iterable<String> opts) {
        opts.each { javaOpt(it) }
    }

    void antInstallation(String antInstallationName) {
        antName = antInstallationName
    }
}
