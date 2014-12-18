package javaposse.jobdsl.dsl

class ConfigurationMissingException extends RuntimeException {
    ConfigurationMissingException() {
        super('The provided job or view configuration was lacking somewhat.')
    }
}
