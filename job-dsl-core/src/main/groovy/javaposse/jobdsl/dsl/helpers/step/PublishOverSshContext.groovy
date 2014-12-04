package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.ContextHelper

class PublishOverSshContext implements Context {

    boolean continueOnError
    boolean failOnError
    boolean alwaysPublishFromMaster
    String parameterName
    boolean parameterizedPublishing
    List<ServerContext> servers = []

    def continueOnError(boolean continueOnError = true) {
        this.continueOnError = continueOnError
    }

    def failOnError(boolean failOnError = true) {
        this.failOnError = failOnError
    }

    def alwaysPublishFromMaster(boolean alwaysPublishFromMaster = true) {
        this.alwaysPublishFromMaster = alwaysPublishFromMaster
    }

    def parameterizedPublishing(String parameterName) {
        this.parameterizedPublishing = true
        this.parameterName = parameterName
    }

    def server(String name, Closure serverClosure) {
        def serverContext = new ServerContext(name)
        ContextHelper.executeInContext(serverClosure, serverContext)
        servers << serverContext
    }

    class ServerContext implements Context {

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
        List<TransferSetContext> transferSets = []

        ServerContext(String name) {
            this.name = name
        }

        def verbose(boolean verbose = true) {
            this.verbose = verbose
        }

        def label(String label) {
            this.label = label
        }

        def retry(int retries = 0, int delay = 10000) {
            this.retry = true
            this.retries = retries
            this.delay = delay
        }

        def credentialsWithPathToKey(String username, String pathToKey) {
            this.credentials = true
            this.username = username
            this.pathToKey = pathToKey
            this.key = ''
        }

        def credentialsWithKey(String username, String key) {
            this.credentials = true
            this.username = username
            this.pathToKey = ''
            this.key = key
        }

        def transferSet(String sourceFiles, String execCommand, Closure transferSetClosure = null) {
            def transferSetContext = new TransferSetContext(sourceFiles, execCommand)
            ContextHelper.executeInContext(transferSetClosure, transferSetContext)
            transferSets << transferSetContext
        }
    }

    class TransferSetContext implements Context {

        String sourceFiles = ''
        String execCommand = ''
        String removePrefix = ''
        String remoteDirectory = ''
        String excludeFiles = ''
        String patternSeparator = '[, ]+'
        boolean noDefaultExcludes
        boolean makeEmptyDirs
        boolean flattenFiles
        boolean remoteDirIsDateFormat
        long execTimeout = 120000
        boolean execInPty

        TransferSetContext(String sourceFiles, String execCommand) {
            this.sourceFiles = sourceFiles
            this.execCommand = execCommand
        }

        def removePrefix(String prefix) {
            this.removePrefix = prefix
        }

        def remoteDirectory(String remoteDirectory) {
            this.remoteDirectory = remoteDirectory
        }

        def excludeFiles(String excludeFiles) {
            this.excludeFiles = excludeFiles
        }

        def patternSeparator(String patternSeparator) {
            this.patternSeparator = patternSeparator
        }

        def noDefaultExcludes(boolean noDefaultExcludes = true) {
            this.noDefaultExcludes = noDefaultExcludes
        }

        def makeEmptyDirs(boolean makeEmptyDirs = true) {
            this.makeEmptyDirs = makeEmptyDirs
        }

        def flattenFiles(boolean flattenFiles = true) {
            this.flattenFiles = flattenFiles
        }

        def remoteDirIsDateFormat(boolean remoteDirIsDateFormat = true) {
            this.remoteDirIsDateFormat = remoteDirIsDateFormat
        }

        def execTimeout(long execTimeout) {
            this.execTimeout = execTimeout
        }

        def execInPty(boolean execInPty = true) {
            this.execInPty = execInPty
        }
    }
}
