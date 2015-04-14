package javaposse.jobdsl.dsl

/**
 * Abstract base class for {@link Context} implementations which provides access to {@link JobManagement}.
 */
class AbstractContext implements Context {
    protected final JobManagement jobManagement

    protected AbstractContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }
}
