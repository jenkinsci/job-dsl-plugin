package javaposse.jobdsl.dsl

/**
 * @author aharmel-law
 */
class JobConfigurationMissingException extends Throwable {
    public JobConfigurationMissingException() {
        super("The provided job configuration was lacking somewhat.")
    }
}
