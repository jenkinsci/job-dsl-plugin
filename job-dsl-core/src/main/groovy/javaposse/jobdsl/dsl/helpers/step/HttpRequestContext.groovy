package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context

import static com.google.common.base.Preconditions.checkArgument

class HttpRequestContext implements Context {
    private static final Set<String> VALID_MODES = ['POST', 'GET', 'DELETE', 'PUT']

    String httpMode
    String authentication
    Boolean returnCodeBuildRelevant
    Boolean logResponseBody

    def httpMode(String httpMode) {
        checkArgument(VALID_MODES.contains(httpMode), "HTTP mode must be one of ${VALID_MODES.join(', ')}")
        this.httpMode = httpMode
    }

    def authentication(String authentication) {
        this.authentication = authentication
    }

    def returnCodeBuildRelevant(boolean returnCodeBuildRelevant = true) {
        this.returnCodeBuildRelevant = returnCodeBuildRelevant
    }

    def logResponseBody(boolean logResponseBody = true) {
        this.logResponseBody = logResponseBody
    }
}
