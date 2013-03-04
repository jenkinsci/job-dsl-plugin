package javaposse.jobdsl.dsl

public class JobTypeMismatchException extends RuntimeException {
    public JobTypeMismatchException(String jobName, String templateName) {
        super("The type of job '${jobName}' does not match the type of its template '${templateName}'.")
    }
}
