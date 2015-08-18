package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class CredentialsParameterContext implements Context {
    String type = 'com.cloudbees.plugins.credentials.common.StandardCredentials'
    boolean required
    String defaultValue
    String description

    void type(String type) {
        Preconditions.checkNotNullOrEmpty(type, 'type must not be null or empty')
        this.type = type
    }

    void required(boolean required = true) {
        this.required = required
    }

    void defaultValue(String defaultValue) {
        this.defaultValue = defaultValue
    }

    void description(String description) {
        this.description = description
    }
}
