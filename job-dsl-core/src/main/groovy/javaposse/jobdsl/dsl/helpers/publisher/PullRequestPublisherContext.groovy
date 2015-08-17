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

    /**
     * @since 1.38
     */
    @RequiresPlugin(id='ghprb', minimumVersion='1.26')
    void failOnNonMerge(boolean failOnNonMerge = true) {
        this.failOnNonMerge = failOnNonMerge
    }

    /**
     * @since 1.38
     */
    @RequiresPlugin(id='ghprb', minimumVersion='1.26')
    void deleteOnMerge(boolean deleteOnMerge = true) {
        this.deleteOnMerge = deleteOnMerge
    }
}
