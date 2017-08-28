package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

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
     * Adds an endpoint with a URL sourced from the Jenkins credential store
     *
     * @see #secretEndpoint(java.lang.String, java.lang.String, java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'notification', minimumVersion = '1.12')
    void secretEndpoint(String credentialsId, String protocol = 'HTTP', String format = 'JSON') {
        secretEndpoint(credentialsId, protocol, format, null)
    }

    /**
     * Adds an endpoint with a URL sourced from the Jenkins credential store
     *
     * Possible values for the protocol argument are {@code 'HTTP'}, {@code 'TCP'}, and {@code 'UDP'}.
     * Possible values for the format argument are {@code 'JSON'} and {@code 'XML'}.
     */
    @RequiresPlugin(id = 'notification', minimumVersion = '1.12')
    void secretEndpoint(String credentialsId, String protocol = 'HTTP', String format = 'JSON',
                  @DslContext(NotificationEndpointContext) Closure notificationEndpointClosure) {
        checkNotNullOrEmpty(credentialsId, 'credentials id must be specified')
        checkArgument(PROTOCOLS.contains(protocol), "protocol must be one of ${PROTOCOLS.join(', ')}")
        checkArgument(FORMATS.contains(format), "format must be one of ${FORMATS.join(', ')}")

        NotificationEndpointContext notificationEndpointContext = new NotificationEndpointContext(jobManagement)
        executeInContext(notificationEndpointClosure, notificationEndpointContext)

        endpoints << NodeBuilder.newInstance().'com.tikal.hudson.plugins.notification.Endpoint' {
            urlInfo {
                urlOrId(credentialsId)
                urlType('SECRET')
            }
            delegate.protocol(protocol)
            delegate.format(format)
            event(notificationEndpointContext.event)
            timeout(notificationEndpointContext.timeout)
            loglines(notificationEndpointContext.logLines)
            retries(notificationEndpointContext.retries)
        }
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
            // Newer versions of this plugin use a different format for the URL
            if (jobManagement.isMinimumPluginVersionInstalled('notification', '1.12')) {
                urlInfo {
                    urlOrId(url)
                    urlType('PUBLIC')
                }
            }
            else {
                delegate.url(url)
            }
            delegate.protocol(protocol)
            delegate.format(format)
            event(notificationEndpointContext.event)
            timeout(notificationEndpointContext.timeout)
            loglines(notificationEndpointContext.logLines)
            if (jobManagement.isMinimumPluginVersionInstalled('notification', '1.12')) {
                retries(notificationEndpointContext.retries)
            }
        }
    }
}
