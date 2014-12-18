package javaposse.jobdsl.dsl.helpers.step

class SystemGroovyContext extends AbstractGroovyContext {
    Map<String, String> bindings = [:]

    void binding(String name, String value) {
        bindings[name] = value
    }
}
