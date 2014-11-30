package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context

class HttpRequestContext implements Context {
    String url
    String httpMode = null
    String authentication = null
    Boolean returnCodeBuildRelevant = null
    Boolean logResponseBody = null

    def url(String url) {
        this.url = url
    }

    def httpMode(String httpMode) {
        this.httpMode = httpMode
    }

    def authentication(String authentication) {
        this.authentication = authentication
    }

    def returnCodeBuildRelevant(boolean returnCodeBuildRelevant) {
        this.returnCodeBuildRelevant = returnCodeBuildRelevant
    }

    def logResponseBody(boolean logResponseBody) {
        this.logResponseBody = logResponseBody
    }
}
