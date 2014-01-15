package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.helpers.Context


class EnvironmentVariableContext implements Context {
    def props = []
    def groovyScript

    def env(Object key, Object value) {
        props << "${key}=${value}"
    }

    def envs(Map<Object, Object> map) {
        map.entrySet().each {
            env(it.key, it.value)
        }
    }

    def groovy(String script) {
        groovyScript = script
    }
}
