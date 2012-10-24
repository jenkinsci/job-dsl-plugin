package javaposse.jobdsl.dsl

/**
 * @author aharmel-law
 */
class JobConfigurationMissingException extends RuntimeException {
    public JobConfigurationMissingException() {
        super("The provided job configuration was lacking somewhat.")
    }
}
