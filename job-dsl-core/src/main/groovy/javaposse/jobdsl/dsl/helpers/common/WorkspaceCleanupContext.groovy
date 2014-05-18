package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.helpers.Context

/**
 * Common DSL for the workspace cleanup plugin.
 *
 * See https://wiki.jenkins-ci.org/display/JENKINS/Workspace+Cleanup+Plugin
 */
class WorkspaceCleanupContext implements Context {

    private enum PatternType {
        INCLUDE ("INCLUDE"),
        EXCLUDE ("EXCLUDE")

        private PatternType(String type) {
            this.type = type
        }
        private String type

        private String value() {
            return type;
        }
    }

    List<Node> patternNodes = []
    boolean deleteDirectories = false
    boolean cleanWhenSuccess = true
    boolean cleanWhenUnstable = true
    boolean cleanWhenFailure = true
    boolean cleanWhenNotBuilt = true
    boolean cleanWhenAborted = true
    boolean failBuild = true
    String deleteCommand

    void includePattern(String pattern) {
        addPattern(PatternType.INCLUDE, pattern)
    }

    void excludePattern(String pattern) {
        addPattern(PatternType.EXCLUDE, pattern)
    }

    void deleteDirectories(boolean deleteDirectories = true) {
        this.deleteDirectories = deleteDirectories
    }

    void cleanWhenSuccess(boolean cleanWhenSuccess = true) {
        this.cleanWhenSuccess = cleanWhenSuccess
    }

    void cleanWhenUnstable(boolean cleanWhenUnstable = true) {
        this.cleanWhenUnstable = cleanWhenUnstable
    }

    void cleanWhenFailure(boolean cleanWhenFailure = true) {
        this.cleanWhenFailure = cleanWhenFailure
    }

    void cleanWhenNotBuilt(boolean cleanWhenNotBuilt = true) {
        this.cleanWhenNotBuilt = cleanWhenNotBuilt
    }

    void cleanWhenAborted(boolean cleanWhenAborted = true) {
        this.cleanWhenAborted = cleanWhenAborted
    }

    void failBuildWhenCleanupFails(boolean failBuild = true) {
        this.failBuild = failBuild
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
    private void addPattern(PatternType type, String pattern) {
        patternNodes << new NodeBuilder().'hudson.plugins.ws__cleanup.Pattern' {
            delegate.pattern(pattern)
            delegate.type(type.value())
        }
    }
}
