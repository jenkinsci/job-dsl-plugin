package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

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

    /**
     * Enables logging of detailed information to the Jenkins console.
     */
    void verbose(boolean verbose = true) {
        this.verbose = verbose
    }

    /**
     * Set the label for this Server instance for use with parametrized publishing.
     */
    void label(String label) {
        this.label = label
    }

    /**
     * Tries again if publishing to the server or command execution fails.
     */
    void retry(int retries = 0, int delay = 10000) {
        this.retry = true
        this.retries = retries
        this.delay = delay
    }

    /**
     * Sets the credentials to use with this connection.
     */
    void credentials(String username, @DslContext(PublishOverSshCredentialsContext) Closure credentialsClosure) {
        PublishOverSshCredentialsContext credentialsContext = new PublishOverSshCredentialsContext(username)
        ContextHelper.executeInContext(credentialsClosure, credentialsContext)

        credentials = credentialsContext
    }

    /**
     * Adds a transfer set. Can be called multiple times to add more transfer sets.
     */
    void transferSet(@DslContext(PublishOverSshTransferSetContext) Closure transferSetClosure) {
        PublishOverSshTransferSetContext transferSetContext = new PublishOverSshTransferSetContext()
        ContextHelper.executeInContext(transferSetClosure, transferSetContext)

        checkArgument(
                (transferSetContext.sourceFiles as boolean) || (transferSetContext.execCommand as boolean),
                'sourceFiles or execCommand must be specified'
        )

        transferSets << transferSetContext
    }
}
