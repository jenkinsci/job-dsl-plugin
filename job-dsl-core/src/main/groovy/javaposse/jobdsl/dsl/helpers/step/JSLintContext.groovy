package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class JSLintContext implements Context {
    String includePattern
    String excludePattern
    String logFile
    String arguments

    /**
     * Specifies the files to include in an Ant-style filter. You can define multiple filesets using comma as a
     * separator.
     */
    void includePattern(String includePattern) {
        this.includePattern = includePattern
    }

    /**
     * Specifies the files to exclude in an Ant-style filter. You can define multiple filesets using comma as a
     * separator.
     */
    void excludePattern(String excludePattern) {
        this.excludePattern = excludePattern
    }

    /**
     * Specifies the file to output to in a Checkstyle XML format.
     */
    void logFile(String logFile) {
        this.logFile = logFile
    }

    /**
     * Specifies the arguments to pass to JSLint. Be sure to prefix each argument with {@code -D} and use comma as a
     * separator.
     */
    void arguments(String arguments) {
        this.arguments = arguments
    }
}
