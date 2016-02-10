package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class NotificationEndpointContext extends AbstractContext {
    private static final List<String> EVENTS = ['all', 'started', 'completed', 'finalized']

    String event = 'all'
    int timeout = 30000
    int loglines = 0

    NotificationEndpointContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets the job lifecycle event triggering notification.
     *
     * Possible values are {@code 'all'}, {@code 'started'}, {@code 'completed'} and {@code 'finalized'}.
     */
    @RequiresPlugin(id = 'notification', minimumVersion = '1.6')
    void event(String event) {
        checkArgument(EVENTS.contains(event), "event must be one of ${EVENTS.join(', ')}")

        this.event = event
    }

    /**
     * Sets a timeout in milliseconds.
     */
    @RequiresPlugin(id = 'notification', minimumVersion = '1.6')
    void timeout(int timeout) {
        this.timeout = timeout
    }

    /**
     * Sets a loglines.
     * @since 1.43
     */
    @RequiresPlugin(id = 'notification', minimumVersion = '1.8')
    void loglines(int loglines) {
        this.loglines = loglines
    }

}
