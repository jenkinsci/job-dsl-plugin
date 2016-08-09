package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext
import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

class NotificationContext extends AbstractContext {
    private static final List<String> PROTOCOLS = ['UDP', 'TCP', 'HTTP']
    private static final List<String> FORMATS = ['JSON', 'XML']

    final List<Node> endpoints = []

    NotificationContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds an endpoint which will receive notifications about the job status.
     *
     * @see #endpoint(java.lang.String, java.lang.String, java.lang.String, groovy.lang.Closure)
     */
    void endpoint(String url, String protocol = 'HTTP', String format = 'JSON') {
        endpoint(url, protocol, format, null)
    }

    /**
     * Adds an endpoint which will receive notifications about the job status.
     *
     * Possible values for the protocol argument are {@code 'HTTP'}, {@code 'TCP'}, and {@code 'UDP'}.
     * Possible values for the format argument are {@code 'JSON'} and {@code 'XML'}.
     */
    void endpoint(String url, String protocol = 'HTTP', String format = 'JSON',
                  @DslContext(NotificationEndpointContext) Closure notificationEndpointClosure) {
        checkNotNullOrEmpty(url, 'url must be specified')
        checkArgument(PROTOCOLS.contains(protocol), "protocol must be one of ${PROTOCOLS.join(', ')}")
        checkArgument(FORMATS.contains(format), "format must be one of ${FORMATS.join(', ')}")

        NotificationEndpointContext notificationEndpointContext = new NotificationEndpointContext(jobManagement)
        executeInContext(notificationEndpointClosure, notificationEndpointContext)

        endpoints << NodeBuilder.newInstance().'com.tikal.hudson.plugins.notification.Endpoint' {
            delegate.url(url)
            delegate.protocol(protocol)
            delegate.format(format)
            event(notificationEndpointContext.event)
            timeout(notificationEndpointContext.timeout)
            loglines(notificationEndpointContext.logLines)
        }
    }
}
