package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.Context

class PublishOverSshTransferSetContext implements Context {
    String sourceFiles
    String execCommand
    String removePrefix
    String remoteDirectory
    String excludeFiles
    String patternSeparator = '[, ]+'
    boolean noDefaultExcludes
    boolean makeEmptyDirs
    boolean flattenFiles
    boolean remoteDirIsDateFormat
    long execTimeout = 120000
    boolean execInPty

    /**
     * Sets the files to upload to a server.
     */
    void sourceFiles(String sourceFiles) {
        this.sourceFiles = sourceFiles
    }

    /**
     * Specifies a command to execute on the remote server.
     */
    void execCommand(String execCommand) {
        this.execCommand = execCommand
    }

    /**
     * Sets the first part of the file path that should not be created on the remote server.
     */
    void removePrefix(String prefix) {
        this.removePrefix = prefix
    }

    /**
     * Sets the destination folder.
     */
    void remoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory
    }

    /**
     * Specifies files to exclude from the transfer set.
     */
    void excludeFiles(String excludeFiles) {
        this.excludeFiles = excludeFiles
    }

    /**
     * The regular expression that is used to separate the source files and exclude files patterns. Defaults to
     * {@code '[, ]+'}.
     */
    void patternSeparator(String patternSeparator) {
        this.patternSeparator = patternSeparator
    }

    /**
     * If set, the default exclude patterns are disabled. Defaults to {@code false}.
     */
    void noDefaultExcludes(boolean noDefaultExcludes = true) {
        this.noDefaultExcludes = noDefaultExcludes
    }

    /**
     * If set, this option will create any directories that match the Source files pattern, even if empty.
     * Defaults to {@code false}.
     */
    void makeEmptyDirs(boolean makeEmptyDirs = true) {
        this.makeEmptyDirs = makeEmptyDirs
    }

    /**
     * Only creates files on the server, does not create directories. Defaults to {@code false}.
     */
    void flattenFiles(boolean flattenFiles = true) {
        this.flattenFiles = flattenFiles
    }

    /**
     * Select this to include a timestamp in the remote directory. Defaults to {@code false}.
     */
    void remoteDirIsDateFormat(boolean remoteDirIsDateFormat = true) {
        this.remoteDirIsDateFormat = remoteDirIsDateFormat
    }

    /**
     * Sets a timeout in milliseconds for the command to execute. Defaults to two minutes.
     */
    void execTimeout(long execTimeout) {
        this.execTimeout = execTimeout
    }

    /**
     * Executes the command in a pseudo tty. Defaults to {@code false}.
     */
    void execInPty(boolean execInPty = true) {
        this.execInPty = execInPty
    }
}
