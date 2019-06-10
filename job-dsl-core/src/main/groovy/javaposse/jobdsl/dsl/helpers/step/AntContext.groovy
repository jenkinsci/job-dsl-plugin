package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class AntContext implements Context {
    List<String> targets = []
    List<String> props = []
    String buildFile = null
    List<String> antOpts = []
    String antName = null

    /**
     * Specifies an Ant target to be invoked. Can be called multiple times to add more targets.
     */
    void target(String target) {
        targets << target
    }

    /**
     * Specifies Ant targets to be invoked. Can be called multiple times to add more targets.
     */
    void targets(Iterable<String> targets) {
        targets.each {
            target(it)
        }
    }

    /**
     * Specifies a property for the Ant build. Can be called multiple times to add more properties.
     */
    void prop(Object key, Object value) {
        props << "${key}=${value}"
    }

    /**
     * Specifies properties for the Ant build. Can be called multiple times to add more properties.
     */
    void props(Map<String, String> map) {
        map.entrySet().each {
            prop(it.key, it.value)
        }
    }

    /**
     * Specifies the build file to be invoked.
     */
    void buildFile(String buildFile) {
        this.buildFile = buildFile
    }

    /**
     * Specifies custom {@code ANT_OPTS}. Can be called multiple times to add more options.
     */
    void javaOpt(String opt) {
        antOpts << opt
    }

    /**
     * Specifies custom {@code ANT_OPTS}.  Can be called multiple times to add more options.
     */
    void javaOpts(Iterable<String> opts) {
        opts.each { javaOpt(it) }
    }

    /**
     * Specifies the name of the Ant installation to be used for this build step.
     */
    void antInstallation(String antInstallationName) {
        antName = antInstallationName
    }
}
