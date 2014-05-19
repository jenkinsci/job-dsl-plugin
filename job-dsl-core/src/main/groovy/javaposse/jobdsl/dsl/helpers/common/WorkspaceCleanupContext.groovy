package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.helpers.Context

/**
 * Common DSL for the workspace cleanup plugin.
 *
 * See https://wiki.jenkins-ci.org/display/JENKINS/Workspace+Cleanup+Plugin
 */
class WorkspaceCleanupContext implements Context {

    List<Node> patternNodes = []
    boolean deleteDirectories = false
    String deleteCommand

    void includePattern(String pattern) {
        addPattern('INCLUDE', pattern)
    }

    void excludePattern(String pattern) {
        addPattern('EXCLUDE', pattern)
    }

    void deleteDirectories(boolean deleteDirectories = true) {
        this.deleteDirectories = deleteDirectories
    }

    void deleteCommand(String deleteCommand) {
        this.deleteCommand = deleteCommand
    }

    /**
     * <hudson.plugins.ws__cleanup.Pattern>
     *     <pattern>*.class</pattern>
     *     <type>INCLUDE</type>
     * </hudson.plugins.ws__cleanup.Pattern>
     */
    private void addPattern(String type, String pattern) {
        patternNodes << new NodeBuilder().'hudson.plugins.ws__cleanup.Pattern' {
            delegate.pattern(pattern)
            delegate.type(type)
        }
    }
}
