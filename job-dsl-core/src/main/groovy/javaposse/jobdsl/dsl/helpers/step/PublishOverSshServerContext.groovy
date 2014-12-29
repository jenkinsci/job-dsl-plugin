package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Strings.isNullOrEmpty

class PublishOverSshServerContext implements Context {
    final String name
    final List<PublishOverSshTransferSetContext> transferSets = []
    boolean verbose
    String label
    boolean retry
    long retries
    long delay
    PublishOverSshCredentialsContext credentials

    PublishOverSshServerContext(String name) {
        this.name = name
    }

    void verbose(boolean verbose = true) {
        this.verbose = verbose
    }

    void label(String label) {
        this.label = label
    }

    void retry(int retries = 0, int delay = 10000) {
        this.retry = true
        this.retries = retries
        this.delay = delay
    }

    void credentials(String username, @DslContext(PublishOverSshCredentialsContext) Closure credentialsClosure) {
        PublishOverSshCredentialsContext credentialsContext = new PublishOverSshCredentialsContext(username)
        ContextHelper.executeInContext(credentialsClosure, credentialsContext)

        credentials = credentialsContext
    }

    void transferSet(@DslContext(PublishOverSshTransferSetContext) Closure transferSetClosure) {
        PublishOverSshTransferSetContext transferSetContext = new PublishOverSshTransferSetContext()
        ContextHelper.executeInContext(transferSetClosure, transferSetContext)

        checkArgument(
                !isNullOrEmpty(transferSetContext.sourceFiles) || !isNullOrEmpty(transferSetContext.execCommand),
                'sourceFiles or execCommand must be specified'
        )

        transferSets << transferSetContext
    }
}
