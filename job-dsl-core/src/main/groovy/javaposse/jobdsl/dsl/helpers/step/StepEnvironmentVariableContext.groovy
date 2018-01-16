package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.NoDoc

class StepEnvironmentVariableContext extends AbstractContext {
    List<String> props = []
    String propertiesFilePath = ''

    StepEnvironmentVariableContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds an environment variable to the build.
     */
    void env(Object key, Object value) {
        props << "${key}=${value}"
    }

    /**
     * Adds environment variables to the build.
     */
    void envs(Map<Object, Object> map) {
        map.entrySet().each {
            env(it.key, it.value)
        }
    }

    /**
     * Adds environment variables from a properties file.
     */
    void propertiesFile(String propertiesFilePath) {
        this.propertiesFilePath = propertiesFilePath
    }

    @NoDoc
    void addInfoToBuilder(Object builder) {
        builder.info {
            addInfoContentToBuilder(builder)
        }
    }

    protected void addInfoContentToBuilder(Object builder) {
        if (propertiesFilePath) {
            builder.propertiesFilePath(propertiesFilePath)
        }
        if (props) {
            builder.propertiesContent(props.join('\n'))
        }
    }
}
