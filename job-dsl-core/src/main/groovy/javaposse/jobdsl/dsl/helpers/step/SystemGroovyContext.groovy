package javaposse.jobdsl.dsl.helpers.step

class SystemGroovyContext extends AbstractGroovyContext {
    Map<String, String> bindings = [:]

    /**
     * Adds a variable binding for the script. Can be called multiple times to add more bindings.
     */
    void binding(String name, String value) {
        bindings[name] = value
    }
}
