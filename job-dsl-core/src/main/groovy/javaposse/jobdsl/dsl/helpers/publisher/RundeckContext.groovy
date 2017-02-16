package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class RundeckContext extends AbstractContext {
    Map<String, String> options = [:]
    Map<String, String> nodeFilters = [:]
    String tag
    boolean shouldWaitForRundeckJob
    boolean shouldFailTheBuild
    boolean includeRundeckLogs
    String rundeckInstance

    RundeckContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds options for the Rundeck job to execute. Can be called multiple times to add more options.
     */
    void options(Map<String, String> options) {
        this.options.putAll(options)
    }

    /**
     * Adds an option for the Rundeck job to execute. Can be called multiple times to add more options.
     */
    void option(String key, String value) {
        this.options[key] = value
    }

    /**
     * Adds filters to optionally filter the nodes on which Rundeck will run. Can be called multiple times to add more
     * filters.
     */
    void nodeFilters(Map<String, String> nodeFilters) {
        this.nodeFilters.putAll(nodeFilters)
    }

    /**
     * Add a filter to optionally filter the nodes on which Rundeck will run. Can be called multiple times to add more
     * filters.
     */
    void nodeFilter(String key, String value) {
        this.nodeFilters[key] = value
    }

    /**
     * If set, checks if the SCM changelog contains the given tag, and only schedules a job execution if it is present.
     */
    void tag(String tag) {
        this.tag = tag
    }

    /**
     * If set, waits for Rundeck job executions to finish. Defaults to {@code false}.
     */
    void shouldWaitForRundeckJob(boolean shouldWaitForRundeckJob = true) {
        this.shouldWaitForRundeckJob = shouldWaitForRundeckJob
    }

    /**
     * If set, fails the Jenkins build if the job execution on Rundeck could not be scheduled. Defaults to
     * {@code false}.
     */
    void shouldFailTheBuild(boolean shouldFailTheBuild = true) {
        this.shouldFailTheBuild = shouldFailTheBuild
    }

    /**
     * If set, job execution logs from Rundeck are included in the console output. Defaults to {@code false}.
     *
     * This also enables waiting for Rundeck job executions to finish.
     *
     * @since 1.43
     */
    void includeRundeckLogs(boolean includeRundeckLogs = true) {
        this.includeRundeckLogs = includeRundeckLogs
        if (includeRundeckLogs) {
            this.shouldWaitForRundeckJob = true
        }
    }

    /**
     * If set, selects the configured Rundeck instance from the global config.
     *
     * @since 1.51
     */
    void rundeckInstance(String rundeckInstance) {
        this.rundeckInstance = rundeckInstance
    }
}
