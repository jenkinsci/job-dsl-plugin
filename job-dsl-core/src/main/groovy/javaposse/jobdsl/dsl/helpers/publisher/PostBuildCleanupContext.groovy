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

    /**
     * Deletes files when the build was successful. Defaults to {@code true}.
     */
    void cleanWhenSuccess(boolean cleanWhenSuccess = true) {
        this.cleanWhenSuccess = cleanWhenSuccess
    }

    /**
     * Deletes files when the build is unstable. Defaults to {@code true}.
     */
    void cleanWhenUnstable(boolean cleanWhenUnstable = true) {
        this.cleanWhenUnstable = cleanWhenUnstable
    }

    /**
     * Deletes files when the build failed. Defaults to {@code true}.
     */
    void cleanWhenFailure(boolean cleanWhenFailure = true) {
        this.cleanWhenFailure = cleanWhenFailure
    }

    /**
     * Deletes files when the build was not run. Defaults to {@code true}.
     */
    void cleanWhenNotBuilt(boolean cleanWhenNotBuilt = true) {
        this.cleanWhenNotBuilt = cleanWhenNotBuilt
    }

    /**
     * Deletes files when the build has been aborted. Defaults to {@code true}.
     */
    void cleanWhenAborted(boolean cleanWhenAborted = true) {
        this.cleanWhenAborted = cleanWhenAborted
    }

    /**
     * If set, does not fail the build when the cleanup fails. Defaults to {@code true}.
     */
    void failBuildWhenCleanupFails(boolean failBuild = true) {
        this.failBuild = failBuild
    }
}
