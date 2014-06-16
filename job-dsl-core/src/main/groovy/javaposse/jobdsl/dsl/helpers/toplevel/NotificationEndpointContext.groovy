package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.helpers.Context

class NotificationEndpointContext implements Context {
    String event = "all"
    int timeout = 30000

    NotificationEndpointContext() {
    }

    void event(String event) {
        this.event = event
    }

    void timeout(int timeout) {
        this.timeout = timeout
    }
}
