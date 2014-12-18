package javaposse.jobdsl.dsl

class JobTypeMismatchException extends RuntimeException {
    JobTypeMismatchException(String jobName, String templateName) {
        super("The type of job '${jobName}' does not match the type of its template '${templateName}'.")
    }
}
