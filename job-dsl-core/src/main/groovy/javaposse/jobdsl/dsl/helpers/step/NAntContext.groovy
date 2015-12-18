package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class NAntContext implements Context {
    List<String> targets = []
    List<String> props = []
    String buildFile
    String nantInstallation = '(Default)'

    /**
     * Specifies an NAnt target to be invoked. Can be called multiple times to add more targets.
     */
    void target(String target) {
        targets << target
    }

    /**
     * Specifies NAnt targets to be invoked. Can be called multiple times to add more targets.
     */
    void targets(Iterable<String> targets) {
        targets.each {
            target(it)
        }
    }

    /**
     * Specifies a property for the NAnt build. Can be called multiple times to add more properties.
     */
    void prop(Object key, Object value) {
        props << "${key}=${value}"
    }

    /**
     * Specifies properties for the NAnt build. Can be called multiple times to add more properties.
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
     * Specifies the name of the NAnt installation to be used for this build step. Defaults to {@code '(Default)'}.
     */
    void nantInstallation(String nantInstallationName) {
        nantInstallation = nantInstallationName
    }
}
