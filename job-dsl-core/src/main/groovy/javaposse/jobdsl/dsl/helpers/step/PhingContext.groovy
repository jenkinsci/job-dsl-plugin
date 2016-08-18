package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class PhingContext implements Context {
    String phingName = '(Default)'
    boolean useModuleRoot = true
    String buildFile
    final List<String> targets = []
    final Map<String, String> properties = [:]
    final List<String> options = []

    /**
     * Specifies the name of the Phing installation to be used for this build step. Defaults to {@code '(Default)'}.
     */
    void phingInstallation(String phingInstallation) {
        this.phingName = phingInstallation
    }

    /**
     * If set to true, use ModuleRoot as working directory.
     * Defaults to {@code true}.
     */
    void useModuleRoot(boolean useModuleRoot = true) {
        this.useModuleRoot = useModuleRoot
    }

    /**
     * Specifies the custom build file directory.
     */
    void buildFile(String buildFile) {
        this.buildFile = buildFile
    }

    /**
     * Specifies a list of Phing targets to be invoked. Can be called multiple times to add more targets.
     */
    void targets(String targets) {
        this.targets << targets
    }

    /**
    * Specifies custom properties to be added to the Phing call. Can be called multiple times to add more properties.
     */
    void properties(String name, Object value) {
        this.properties[name] = value?.toString()
    }

    /**
     * Specifies options to be added to the Phing call. Can be called multiple times to add more options.
     */
    void options(String options) {
        this.options << options
    }
}
