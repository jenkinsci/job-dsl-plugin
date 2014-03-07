package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context

class StepEnvironmentVariableContext implements Context {
    def props = []
    String propertiesFilePath = ''

    def env(Object key, Object value) {
        props << "${key}=${value}"
    }

    def envs(Map<Object, Object> map) {
        map.entrySet().each {
            env(it.key, it.value)
        }
    }

    def propertiesFile(String propertiesFilePath) {
        this.propertiesFilePath = propertiesFilePath
    }

    def addInfoToBuilder(builder) {
        builder.info {
            addInfoContentToBuilder(builder)
        }
    }

    protected addInfoContentToBuilder(builder) {
        if (propertiesFilePath) {
            builder.propertiesFilePath(propertiesFilePath)
        }
        if (props) {
            builder.propertiesContent(props.join('\n'))
        }
    }

}
