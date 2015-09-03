package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

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

    /**
     * Specifies a target to be invoked. Can be called multiple times to add more targets.
     */
    void target(String target) {
        targets << target
    }

    /**
     * Specifies targets to be invoked. Can be called multiple times to add more targets.
     */
    void targets(Iterable<String> addlTargets) {
        addlTargets.each {
            target(it)
        }
    }

    /**
     * Selects a Grails installation to use.
     */
    void name(String name) {
        this.name = name
    }

    /**
     * Specify a value for the {@code grails.work.dir} system property.
     */
    void grailsWorkDir(String grailsWorkDir) {
        this.grailsWorkDir = grailsWorkDir
    }

    /**
     * Specifies a value for the {@code grails.project.work.dir} system property.
     */
    void projectWorkDir(String projectWorkDir) {
        this.projectWorkDir = projectWorkDir
    }

    /**
     * Specifies a path to the root of the Grails project.
     */
    void projectBaseDir(String projectBaseDir) {
        this.projectBaseDir = projectBaseDir
    }

    /**
     * Specifies a value for the {@code server.port} system property.
     */
    void serverPort(String serverPort) {
        this.serverPort = serverPort
    }

    /**
     * Adds an additional system property. Can be called multiple times to add more properties.
     */
    void prop(String key, String value) {
        props[key] = value
    }

    /**
     * Adds additional system properties. Can be called multiple times to add more properties.
     */
    void props(Map<String, String> map) {
        props += map
    }

    /**
     * Runs {@code grails upgrade --non-interactive} first. Defaults to {@code false}.
     */
    void forceUpgrade(boolean forceUpgrade) {
        this.forceUpgrade = forceUpgrade
    }

    /**
     * Appends {@code --non-interactive} to all build targets. Defaults to {@code true}.
     */
    void nonInteractive(boolean nonInteractive) {
        this.nonInteractive = nonInteractive
    }

    /**
     * Use the Grails wrapper to invoke the build script. Defaults to {@code false}.
     */
    void useWrapper(boolean useWrapper) {
        this.useWrapper = useWrapper
    }
}
