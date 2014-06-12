package javaposse.jobdsl.dsl

/**
 * @author aharmel-law
 */
class ConfigurationMissingException extends RuntimeException {
    ConfigurationMissingException() {
        super("The provided job or view configuration was lacking somewhat.")
    }
}
