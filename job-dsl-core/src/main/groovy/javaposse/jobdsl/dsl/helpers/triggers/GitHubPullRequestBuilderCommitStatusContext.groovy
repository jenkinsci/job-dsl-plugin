package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class GitHubPullRequestBuilderCommitStatusContext implements Context {
    private static final Set<String> VALID_BUILD_RESULT = ['SUCCESS', 'ERROR', 'FAILURE']

    String context
    String triggeredStatus
    String startedStatus
    String statusUrl
    List<Node> completedStatus = []

    /**
     * A string label to differentiate this status from the status of other systems.
     */
    void context(String context) {
        this.context = context
    }

    /**
     * Use a custom status for when a build is triggered.
     */
    void triggeredStatus(String triggeredStatus) {
        this.triggeredStatus = triggeredStatus
    }

    /**
     * Use a custom status for when a build is started.
     */
    void startedStatus(String startedStatus) {
        this.startedStatus = startedStatus
    }
    
    /**
     * Use a custom url instead of the job default.
     *
     * @since 1.31
     */
    void statusUrl(String statusUrl) {
        this.statusUrl = statusUrl
    }

    /**
     * Use a custom status for when a build is completed. Can be called multiple times to set messages for different
     * build results. Valid build results are {@code 'SUCCESS'}, {@code 'FAILURE'}, and {@code 'ERROR'}.
     */
    void completedStatus(String buildResult, String message) {
        Preconditions.checkArgument(
                VALID_BUILD_RESULT.contains(buildResult),
                "buildResult must be one of ${VALID_BUILD_RESULT.join(', ')}"
        )

        completedStatus << new NodeBuilder().'org.jenkinsci.plugins.ghprb.extensions.comments.GhprbBuildResultMessage' {
            delegate.message(message ?: '')
            delegate.result(buildResult)
        }
    }
}
