package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper

class PublishOverSshServerContext implements Context {

    String name
    boolean verbose
    String label
    boolean retry
    long retries
    long delay
    boolean credentials
    String username
    String pathToKey
    String key
    List<PublishOverSshTransferSetContext> transferSets = []

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

    void credentialsWithPathToKey(String username, String pathToKey) {
        this.credentials = true
        this.username = username
        this.pathToKey = pathToKey
        this.key = ''
    }

    void credentialsWithKey(String username, String key) {
        this.credentials = true
        this.username = username
        this.pathToKey = ''
        this.key = key
    }

    void transferSet(String sourceFiles, String execCommand, Closure transferSetClosure = null) {
        PublishOverSshTransferSetContext transferSetContext = new PublishOverSshTransferSetContext(sourceFiles, execCommand)
        ContextHelper.executeInContext(transferSetClosure, transferSetContext)
        transferSets << transferSetContext
    }
}
