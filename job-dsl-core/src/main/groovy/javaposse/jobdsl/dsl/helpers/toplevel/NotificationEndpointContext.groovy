package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

import static com.google.common.base.Preconditions.checkArgument

class NotificationEndpointContext implements Context {
    private static final List<String> EVENTS = ['all', 'started', 'completed', 'finalized']

    private final JobManagement jobManagement

    String event = 'all'
    int timeout = 30000

    NotificationEndpointContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    @RequiresPlugin(id = 'notification', minimumVersion = '1.6')
    void event(String event) {
        checkArgument(EVENTS.contains(event), "event must be one of ${EVENTS.join(', ')}")

        this.event = event
    }

    @RequiresPlugin(id = 'notification', minimumVersion = '1.6')
    void timeout(int timeout) {
        this.timeout = timeout
    }
}
