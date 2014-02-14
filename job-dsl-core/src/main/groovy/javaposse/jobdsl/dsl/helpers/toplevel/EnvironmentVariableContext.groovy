package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.helpers.Context


class EnvironmentVariableContext implements Context {
    def props = []
    String groovyScript = ''
    String script = ''
    String scriptFilePath = ''
    String propertiesFilePath = ''
    boolean loadFilesFromMaster = false
    boolean keepSystemVariables = true
    boolean keepBuildVariables = true

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

    def script(String script) {
        this.script = script
    }

    def scriptFile(String scriptFilePath) {
        this.scriptFilePath = scriptFilePath
    }

    def propertiesFile(String propertiesFilePath) {
        this.propertiesFilePath = propertiesFilePath
    }

    def loadFilesFromMaster(boolean loadFilesFromMaster) {
        this.loadFilesFromMaster = loadFilesFromMaster
    }

    def keepSystemVariables(boolean keepSystemVariables) {
        this.keepSystemVariables = keepSystemVariables
    }

    def keepBuildVariables(boolean keepBuildVariables) {
        this.keepBuildVariables = keepBuildVariables
    }
}
