package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context

class GrailsContext implements Context {
    List<String> targets = []
    String name = '(Default)'
    String grailsWorkDir = ''
    String projectWorkDir = ''
    String projectBaseDir = ''
    String serverPort = ''
    Map<String, String> props = [:]
    boolean forceUpgrade = false
    boolean nonInteractive = true
    boolean useWrapper = false

    def target(String target) {
        targets << target
    }

    def targets(Iterable<String> addlTargets) {
        addlTargets.each {
            target(it)
        }
    }

    String getTargetsString() {
        targets.join(' ')
    }

    def name(String name) {
        this.name = name
    }

    def grailsWorkDir(String grailsWorkDir) {
        this.grailsWorkDir = grailsWorkDir
    }

    def projectWorkDir(String projectWorkDir) {
        this.projectWorkDir = projectWorkDir
    }

    def projectBaseDir(String projectBaseDir) {
        this.projectBaseDir = projectBaseDir
    }

    def serverPort(String serverPort) {
        this.serverPort = serverPort
    }

    def prop(String key, String value) {
        props[key] = value
    }

    def props(Map<String, String> map) {
        props += map
    }

    String getPropertiesString() {
        props.collect { k, v -> "$k=$v" }.join('\n')
    }

    def forceUpgrade(boolean forceUpgrade) {
        this.forceUpgrade = forceUpgrade
    }

    def nonInteractive(boolean nonInteractive) {
        this.nonInteractive = nonInteractive
    }

    def useWrapper(boolean useWrapper) {
        this.useWrapper = useWrapper
    }

}
