package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class PullRequestPublisherContext implements Context {
    String mergeComment
    boolean onlyTriggerPhrase
    boolean onlyAdminsMerge
    boolean disallowOwnCode

    void mergeComment(String mergeComment) {
        this.mergeComment = mergeComment
    }

    void onlyTriggerPhrase(boolean onlyTriggerPhrase = true) {
        this.onlyTriggerPhrase = onlyTriggerPhrase
    }

    void onlyAdminsMerge(boolean onlyAdminsMerge = true) {
        this.onlyAdminsMerge = onlyAdminsMerge
    }

    void disallowOwnCode(boolean disallowOwnCode = true) {
        this.disallowOwnCode = disallowOwnCode
    }
}
