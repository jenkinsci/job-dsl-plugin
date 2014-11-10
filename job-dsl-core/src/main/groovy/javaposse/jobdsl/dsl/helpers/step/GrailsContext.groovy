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

    void target(String target) {
        targets << target
    }

    void targets(Iterable<String> addlTargets) {
        addlTargets.each {
            target(it)
        }
    }

    String getTargetsString() {
        targets.join(' ')
    }

    void name(String name) {
        this.name = name
    }

    void grailsWorkDir(String grailsWorkDir) {
        this.grailsWorkDir = grailsWorkDir
    }

    void projectWorkDir(String projectWorkDir) {
        this.projectWorkDir = projectWorkDir
    }

    void projectBaseDir(String projectBaseDir) {
        this.projectBaseDir = projectBaseDir
    }

    void serverPort(String serverPort) {
        this.serverPort = serverPort
    }

    void prop(String key, String value) {
        props[key] = value
    }

    void props(Map<String, String> map) {
        props += map
    }

    String getPropertiesString() {
        props.collect { k, v -> "$k=$v" }.join('\n')
    }

    void forceUpgrade(boolean forceUpgrade) {
        this.forceUpgrade = forceUpgrade
    }

    void nonInteractive(boolean nonInteractive) {
        this.nonInteractive = nonInteractive
    }

    void useWrapper(boolean useWrapper) {
        this.useWrapper = useWrapper
    }

}
