package javaposse.jobdsl.dsl.helpers.step.publishoverssh

import javaposse.jobdsl.dsl.Context

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

    void removePrefix(String prefix) {
        this.removePrefix = prefix
    }

    void remoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory
    }

    void excludeFiles(String excludeFiles) {
        this.excludeFiles = excludeFiles
    }

    void patternSeparator(String patternSeparator) {
        this.patternSeparator = patternSeparator
    }

    void noDefaultExcludes(boolean noDefaultExcludes = true) {
        this.noDefaultExcludes = noDefaultExcludes
    }

    void makeEmptyDirs(boolean makeEmptyDirs = true) {
        this.makeEmptyDirs = makeEmptyDirs
    }

    void flattenFiles(boolean flattenFiles = true) {
        this.flattenFiles = flattenFiles
    }

    void remoteDirIsDateFormat(boolean remoteDirIsDateFormat = true) {
        this.remoteDirIsDateFormat = remoteDirIsDateFormat
    }

    void execTimeout(long execTimeout) {
        this.execTimeout = execTimeout
    }

    void execInPty(boolean execInPty = true) {
        this.execInPty = execInPty
    }
}
