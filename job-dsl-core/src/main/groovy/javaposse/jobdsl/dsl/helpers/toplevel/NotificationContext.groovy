package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.helpers.Context

import static javaposse.jobdsl.dsl.helpers.AbstractContextHelper.executeInContext

class NotificationContext implements Context {
    List<Node> endpoints = []

    NotificationContext() {
    }

    void endpoint(String url, String protocol = "HTTP", String format = "JSON") {
        endpoint(url, protocol, format) {}
    }

    void endpoint(String url, String protocol = "HTTP", String format = "JSON", Closure notificationEndpointClosure) {
        NotificationEndpointContext notificationEndpointContext = new NotificationEndpointContext()
        executeInContext(notificationEndpointClosure, notificationEndpointContext)

        endpoints << NodeBuilder.newInstance().'com.tikal.hudson.plugins.notification.Endpoint' {
            delegate.url(url)
            delegate.protocol(protocol)
            delegate.format(format)
            event(notificationEndpointContext.event)
            timeout(notificationEndpointContext.timeout)
        }
    }

}
