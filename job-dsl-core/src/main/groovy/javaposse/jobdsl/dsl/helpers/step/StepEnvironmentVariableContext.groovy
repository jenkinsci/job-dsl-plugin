package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context

class StepEnvironmentVariableContext implements Context {
    List<String> props = []
    String propertiesFilePath = ''

    void env(Object key, Object value) {
        props << "${key}=${value}"
    }

    void envs(Map<Object, Object> map) {
        map.entrySet().each {
            env(it.key, it.value)
        }
    }

    void propertiesFile(String propertiesFilePath) {
        this.propertiesFilePath = propertiesFilePath
    }

    void addInfoToBuilder(builder) {
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
