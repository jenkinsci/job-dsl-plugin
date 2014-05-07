package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

/**
 * DSL supporting the Workspace Cleanup Plugin post build action.
 *
 * See https://wiki.jenkins-ci.org/display/JENKINS/Workspace+Cleanup+Plugin
 */
class WsCleanupContext implements Context {

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
    String externalDeleteCommand

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

    void externalDeleteCommand(String externalDeleteCommand) {
        this.externalDeleteCommand = externalDeleteCommand
    }

    private void addPattern(PatternType type, String pattern) {
        patternNodes << new NodeBuilder().'hudson.plugins.ws__cleanup.Pattern' {
            delegate.pattern(pattern)
            delegate.type(type.value())
        }
    }
}
