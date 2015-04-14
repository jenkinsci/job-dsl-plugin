package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class PullRequestPublisherContext implements Context {
    private final JobManagement jobManagement

    String mergeComment
    boolean onlyTriggerPhrase
    boolean onlyAdminsMerge
    boolean disallowMerginOwnCode

    PullRequestPublisherContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    String mergeComment() {
        mergeComment != null ? mergeComment : ''
    }

    /**
     * @since 1.33
     */
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.14')
    void mergeComment(String mergeComment) {
        this.mergeComment = mergeComment
    }

    /**
     * @since 1.33
     */
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.14')
    void onlyTriggerPhrase(boolean onlyTriggerPhrase) {
        this.onlyTriggerPhrase = onlyTriggerPhrase
    }

    /**
     * @since 1.33
     */
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.14')
    void onlyAdminsMerge(boolean onlyAdminsMerge) {
        this.onlyAdminsMerge = onlyAdminsMerge
    }

    /**
     * @since 1.33
     */
    @RequiresPlugin(id = 'ghprb', minimumVersion = '1.14')
    void disallowMerginOwnCode(boolean disallowMerginOwnCode) {
        this.disallowMerginOwnCode = disallowMerginOwnCode
    }
}
