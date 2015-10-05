package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class PullRequestPublisherContext extends AbstractContext {
    String mergeComment
    boolean onlyTriggerPhrase
    boolean onlyAdminsMerge
    boolean disallowOwnCode
    boolean failOnNonMerge
    boolean deleteOnMerge

    PullRequestPublisherContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets a comment that should show up when the merge command is sent to GitHub.
     */
    void mergeComment(String mergeComment) {
        this.mergeComment = mergeComment
    }

    /**
     * If set, only commenting the trigger phrase in the pull request will trigger a merge. Defaults to {@code false}.
     */
    void onlyTriggerPhrase(boolean onlyTriggerPhrase = true) {
        this.onlyTriggerPhrase = onlyTriggerPhrase
    }

    /**
     * Allows only admin users to trigger a pull request merge. Defaults to {@code false}.
     */
    void onlyAdminsMerge(boolean onlyAdminsMerge = true) {
        this.onlyAdminsMerge = onlyAdminsMerge
    }

    /**
     * Disallows a user to merge their own code. Defaults to {@code false}.
     */
    void disallowOwnCode(boolean disallowOwnCode = true) {
        this.disallowOwnCode = disallowOwnCode
    }

    /**
     * Fails the build if the pull request can't be merged. Defaults to {@code false}.
     */
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.26')
    void failOnNonMerge(boolean failOnNonMerge = true) {
        this.failOnNonMerge = failOnNonMerge
    }

    /**
     * Deletes the branch after a successful merge. Defaults to {@code false}.
     */
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.26')
    void deleteOnMerge(boolean deleteOnMerge = true) {
        this.deleteOnMerge = deleteOnMerge
    }
}
