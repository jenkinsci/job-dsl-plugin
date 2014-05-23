package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.common.WorkspaceCleanupContext

/**
 * DSL supporting the Workspace Cleanup Plugin post build action.
 *
 * See https://wiki.jenkins-ci.org/display/JENKINS/Workspace+Cleanup+Plugin
 */
class PostBuildCleanupContext extends WorkspaceCleanupContext {
    boolean cleanWhenSuccess = true
    boolean cleanWhenUnstable = true
    boolean cleanWhenFailure = true
    boolean cleanWhenNotBuilt = true
    boolean cleanWhenAborted = true
    boolean failBuild = true

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
}
