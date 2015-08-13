package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class GitHubPullRequestBuilderCommitStatusContext implements Context {
    private static final Set<String> VALID_BUILD_RESULT = ['SUCCESS', 'ERROR', 'FAILURE']

    String context
    String triggeredStatus
    String startedStatus
    List<Node> completedStatus = []

    void context(String context) {
        this.context = context
    }

    void triggeredStatus(String triggeredStatus) {
        this.triggeredStatus = triggeredStatus
    }

    void startedStatus(String startedStatus) {
        this.startedStatus = startedStatus
    }

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
