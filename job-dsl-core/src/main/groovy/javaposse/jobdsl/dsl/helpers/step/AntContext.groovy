package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context

class AntContext implements Context {
    def targets = []
    def props = []
    def buildFile = null
    def antOpts = []
    def antName = null

    def target(String target) {
        targets << target
    }

    def targets(Iterable<String> addlTargets) {
        addlTargets.each {
            target(it)
        }
    }

    def prop(Object key, Object value) {
        props << "${key}=${value}"
    }

    def props(Map<String, String> map) {
        map.entrySet().each {
            prop(it.key, it.value)
        }
    }

    def buildFile(String buildFile) {
        this.buildFile = buildFile
    }

    def javaOpt(String opt) {
        antOpts << opt
    }

    def javaOpts(Iterable<String> opts) {
        opts.each { javaOpt(it) }
    }

    def antInstallation(String antInstallationName) {
        antName = antInstallationName
    }
}
