package javaposse.jobdsl.dsl

/**
 * @author aharmel-law
 */
class JobConfigurationNotFoundException extends RuntimeException {
    JobConfigurationNotFoundException(String jobName) {
        super("The job with name $jobName could not be found.")
    }
}
