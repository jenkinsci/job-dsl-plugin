package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class NotificationEndpointContext extends AbstractContext {
    private static final List<String> EVENTS = ['all', 'started', 'completed', 'finalized']

    String event = 'all'
    int timeout = 30000
    int logLines
    int retries

    NotificationEndpointContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets the job lifecycle event triggering notification. Defaults to {@code 'all'}.
     *
     * Possible values are {@code 'all'}, {@code 'started'}, {@code 'completed'} and {@code 'finalized'}.
     */
    void event(String event) {
        checkArgument(EVENTS.contains(event), "event must be one of ${EVENTS.join(', ')}")

        this.event = event
    }

    /**
     * Sets a timeout in milliseconds. Defaults to {@code 30000}.
     */
    void timeout(int timeout) {
        this.timeout = timeout
    }

    /**
     * Sets the number lines of log messages to send. Defaults to {@code 0}.
     *
     * @since 1.43
     */
    void logLines(int lines) {
        this.logLines = lines
    }

    /**
     * Sets the number of times the endpoint should be retried
     *
     * @since 1.60
     */
    @RequiresPlugin(id = 'notification', minimumVersion = '1.12')
    void retries(int retries) {
        this.retries = retries
    }
}
