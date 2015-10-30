package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.Context

/**
 * Common DSL for the workspace cleanup plugin.
 *
 * See https://wiki.jenkins-ci.org/display/JENKINS/Workspace+Cleanup+Plugin
 */
abstract class WorkspaceCleanupContext implements Context {
    List<Node> patternNodes = []
    boolean deleteDirectories = false
    String deleteCommand

    /**
     * Makes cleanup more selective by specifying file to be deleted using Ant syntax.
     */
    void includePattern(String pattern) {
        addPattern('INCLUDE', pattern)
    }

    /**
     * Makes cleanup more selective by specifying file to be excluded from deletion using Ant syntax.
     */
    void excludePattern(String pattern) {
        addPattern('EXCLUDE', pattern)
    }

    /**
     * If set, the pattern will also be applied to directories. Defaults to {@code false}.
     */
    void deleteDirectories(boolean deleteDirectories = true) {
        this.deleteDirectories = deleteDirectories
    }

    /**
     * If set, an external program will be used for deletion.
     */
    void deleteCommand(String deleteCommand) {
        this.deleteCommand = deleteCommand
    }

    // this method cannot be private due to http://jira.codehaus.org/browse/GROOVY-6263
    protected void addPattern(String type, String pattern) {
        patternNodes << new NodeBuilder().'hudson.plugins.ws__cleanup.Pattern' {
            delegate.pattern(pattern)
            delegate.type(type)
        }
    }
}
