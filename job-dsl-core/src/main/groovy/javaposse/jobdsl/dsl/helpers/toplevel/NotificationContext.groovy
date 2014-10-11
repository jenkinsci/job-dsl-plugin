package javaposse.jobdsl.dsl.helpers.toplevel

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.Context

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Strings.isNullOrEmpty
import static javaposse.jobdsl.dsl.helpers.AbstractContextHelper.executeInContext

class NotificationContext implements Context {
    private static final List<String> PROTOCOLS = ['UDP', 'TCP', 'HTTP']
    private static final List<String> FORMATS = ['JSON', 'XML']

    private final JobManagement jobManagement
    final List<Node> endpoints = []

    NotificationContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void endpoint(String url, String protocol = 'HTTP', String format = 'JSON') {
        endpoint(url, protocol, format, null)
    }

    void endpoint(String url, String protocol = 'HTTP', String format = 'JSON', Closure notificationEndpointClosure) {
        checkArgument(!isNullOrEmpty(url), 'url must be specified')
        checkArgument(PROTOCOLS.contains(protocol), "protocol must be one of ${PROTOCOLS.join(', ')}")
        checkArgument(FORMATS.contains(format), "format must be one of ${FORMATS.join(', ')}")

        NotificationEndpointContext notificationEndpointContext = new NotificationEndpointContext(jobManagement)
        executeInContext(notificationEndpointClosure, notificationEndpointContext)

        endpoints << NodeBuilder.newInstance().'com.tikal.hudson.plugins.notification.Endpoint' {
            delegate.url(url)
            delegate.protocol(protocol)
            delegate.format(format)
            if (jobManagement.getPluginVersion('notification')?.isNewerThan(new VersionNumber('1.5'))) {
                event(notificationEndpointContext.event)
                timeout(notificationEndpointContext.timeout)
            }
        }
    }
}
