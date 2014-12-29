package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

import static com.google.common.base.Preconditions.checkArgument

class PublishOverSshContext implements Context {
    final List<PublishOverSshServerContext> servers = []
    boolean continueOnError
    boolean failOnError
    boolean alwaysPublishFromMaster
    String parameterName

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
        this.parameterName = parameterName
    }

    void server(String name, @DslContext(PublishOverSshServerContext) Closure serverClosure) {
        PublishOverSshServerContext serverContext = new PublishOverSshServerContext(name)
        ContextHelper.executeInContext(serverClosure, serverContext)

        checkArgument(!serverContext.transferSets.empty, "At least 1 transferSet must be configured for ${name}")

        servers << serverContext
    }
}
