package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class ExtendedEmailSendToContext implements Context {
    final List<Node> recipientProviders = []

    /**
     * Sends an email to the list of users who committed a change since the last non-broken build till now.
     */
    void culprits() {
        addRecipientProvider('Culprits')
    }

    /**
     * Sends an email to all the people who caused a change in the change set.
     */
    void developers() {
        addRecipientProvider('Developers')
    }

    /**
     * Sends an email to the list of recipients defined in the "Project Recipient List."
     */
    void recipientList() {
        addRecipientProvider('List')
    }

    /**
     * Sends an email to the user who initiated the build.
     */
    void requester() {
        addRecipientProvider('Requester')
    }

    /**
     * Sends an email to the list of users suspected of causing a unit test to begin failing.
     */
    void failingTestSuspects() {
        addRecipientProvider('FailingTestSuspects')
    }

    /**
     * Sends an email to the list of users suspected of causing the build to begin failing.
     */
    void firstFailingBuildSuspects() {
        addRecipientProvider('FirstFailingBuildSuspects')
    }

    /**
     * Sends an email to the list of users who committed changes in upstream builds that triggered this build.
     */
    void upstreamCommitter() {
        addRecipientProvider('UpstreamComitter')
    }

    protected void addRecipientProvider(String name) {
        recipientProviders << new Node(null, "hudson.plugins.emailext.plugins.recipients.${name}RecipientProvider")
    }
}
