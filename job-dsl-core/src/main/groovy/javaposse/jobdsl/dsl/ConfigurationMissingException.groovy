package javaposse.jobdsl.dsl

/**
 * @author aharmel-law
 */
class ConfigurationMissingException extends RuntimeException {
    public ConfigurationMissingException() {
        super("The provided job or view configuration was lacking somewhat.")
    }
}
