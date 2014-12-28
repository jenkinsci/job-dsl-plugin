package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.helpers.step.publishoverssh.ServerContext

class PublishOverSshContext implements Context {

    boolean continueOnError
    boolean failOnError
    boolean alwaysPublishFromMaster
    String parameterName
    boolean parameterizedPublishing
    List<ServerContext> servers = []

    void continueOnError(boolean continueOnError = true) {
        this.continueOnError = continueOnError
    }

    void failOnError(boolean failOnError = true) {
        this.failOnError = failOnError
    }

    void alwaysPublishFromMaster(boolean alwaysPublishFromMaster = true) {
        this.alwaysPublishFromMaster = alwaysPublishFromMaster
    }

    void parameterizedPublishing(String parameterName) {
        this.parameterizedPublishing = true
        this.parameterName = parameterName
    }

    void server(String name, Closure serverClosure) {
        ServerContext serverContext = new ServerContext(name)
        ContextHelper.executeInContext(serverClosure, serverContext)
        servers << serverContext
    }
}
