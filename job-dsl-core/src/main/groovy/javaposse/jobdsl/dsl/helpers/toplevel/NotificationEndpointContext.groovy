package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement

import static com.google.common.base.Preconditions.checkArgument

class NotificationEndpointContext implements Context {
    private static final List<String> EVENTS = ['all', 'started', 'completed', 'finalized']

    private final JobManagement jobManagement

    String event = 'all'
    int timeout = 30000

    NotificationEndpointContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void event(String event) {
        jobManagement.requireMinimumPluginVersion('notification', '1.6')
        checkArgument(EVENTS.contains(event), "event must be one of ${EVENTS.join(', ')}")

        this.event = event
    }

    void timeout(int timeout) {
        jobManagement.requireMinimumPluginVersion('notification', '1.6')

        this.timeout = timeout
    }
}
