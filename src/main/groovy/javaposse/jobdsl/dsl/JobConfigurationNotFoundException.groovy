package javaposse.jobdsl.dsl

/**
 * @author aharmel-law
 */
public class JobConfigurationNotFoundException extends RuntimeException {
    public JobConfigurationNotFoundException(String jobName) {
        super("The job with name " + jobName + " could not be found.")
    }
}