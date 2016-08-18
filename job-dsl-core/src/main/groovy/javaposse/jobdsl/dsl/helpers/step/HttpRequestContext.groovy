package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class HttpRequestContext extends AbstractContext {
    private static final Set<String> VALID_MODES = ['POST', 'GET', 'DELETE', 'PUT']

    String httpMode
    String authentication
    Boolean returnCodeBuildRelevant
    Boolean logResponseBody
    Boolean passBuildParameters

    HttpRequestContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets the HTTP method to use. Must be one of {@code 'GET'}, {@code 'POST'}, {@code 'PUT'} or {@code 'DELETE'}.
     */
    void httpMode(String httpMode) {
        checkArgument(VALID_MODES.contains(httpMode), "HTTP mode must be one of ${VALID_MODES.join(', ')}")
        this.httpMode = httpMode
    }

    /**
     * Sets the key of the authentication to be used. Authentications are created in global configuration.
     */
    void authentication(String authentication) {
        this.authentication = authentication
    }

    /**
     * Fails the build if the response contains an error. Defaults to {@code false}.
     */
    void returnCodeBuildRelevant(boolean returnCodeBuildRelevant = true) {
        this.returnCodeBuildRelevant = returnCodeBuildRelevant
    }

    /**
     * Allows to write the response body to the log. Defaults to {@code false}.
     */
    void logResponseBody(boolean logResponseBody = true) {
        this.logResponseBody = logResponseBody
    }

    /**
     * Allows to pass build parameters to the request URL. Defaults to {@code false}.
     *
     * @since 1.49
     */
    @RequiresPlugin(id = 'http_request', minimumVersion = '1.8.7')
    void passBuildParameters(boolean passBuildParameters = true) {
        this.passBuildParameters = passBuildParameters
    }
}
