package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class EmailTriggerContext implements Context {
    String triggerShortName
    String triggerRecipientList
    String triggerSubject
    String triggerBody
    boolean triggerSendToDevelopers
    boolean triggerSendToRequester
    boolean triggerIncludeCulprits
    boolean triggerSendToRecipientList
    boolean triggerAttachBuildLog
    String triggerReplyTo

    /**
     * Creates the email trigger context.
     *
     * By default, {@code recipientList} is an empty list, {@code subject} is $PROJECT_DEFAULT_SUBJECT,
     * {@code body} is $PROJECT_DEFAULT_CONTENT, {@code sendToDevelopers} is false, {@code sendToRequester} is false,
     * {@code includeCulprits} is false, {@code sendToRecipientList} is true, {@code attachBuildLog} is false,
     * and {@code replyTo} is $PROJECT_DEFAULT_REPLYTO
     */
    EmailTriggerContext(String shortName, String recipientList = null, String subject = null, String body = null,
                        Boolean sendToDevelopers = null, Boolean sendToRequester = null,
                        Boolean includeCulprits = null, Boolean sendToRecipientList = null,
                        Boolean attachBuildLog = null, String replyTo = null) {
        triggerShortName = shortName
        triggerRecipientList = recipientList ?: ''
        triggerSubject = subject ?: '$PROJECT_DEFAULT_SUBJECT'
        triggerBody = body ?: '$PROJECT_DEFAULT_CONTENT'
        triggerSendToDevelopers = sendToDevelopers == null ? false : sendToDevelopers
        triggerSendToRequester = sendToRequester == null ? false : sendToRequester
        triggerIncludeCulprits = includeCulprits == null ? false : includeCulprits
        triggerSendToRecipientList = sendToRecipientList == null ? true : sendToRecipientList
        triggerAttachBuildLog = attachBuildLog == null ? false : attachBuildLog
        triggerReplyTo = replyTo ?: '$PROJECT_DEFAULT_REPLYTO'
    }

    /**
     * Specifies the recipient list for the email.
     *
     * The {@code recipients} parameter must be a comma-separated list of recipients.
     */
    void recipientList(String recipients) {
        triggerRecipientList = recipients
    }

    /**
     * Specifies the recipient list for the email.
     */
    void recipientList(List<String> recipients) {
        recipientList(recipients.join(','))
    }

    /**
     * Specifies the subject for the email.
     */
    void subject(String subject) {
        triggerSubject = subject
    }

    /**
     * Specifies the body for the email.
     */
    void body(String body) {
        triggerBody = body
    }

    /**
     * Specifies whether or not the email should be sent to developers.
     */
    void sendToDevelopers(boolean sendToDevelopers = true) {
        triggerSendToDevelopers = sendToDevelopers
    }

    /**
     * Specifies whether or not the email should be sent to requester.
     */
    void sendToRequester(boolean sendToRequester = true) {
        triggerSendToRequester = sendToRequester
    }

    /**
     * Specifies whether or not the email should be sent to culprits.
     */
    void includeCulprits(boolean includeCulprits = true) {
        triggerIncludeCulprits = includeCulprits
    }

    /**
     * Specifies whether or not the email should be sent to recipient list.
     */
    void sendToRecipientList(boolean sendToRecipientList = true) {
        triggerSendToRecipientList = sendToRecipientList
    }

    /**
     * Specifies whether or not to attach the build log to the email.
     */
    void attachBuildLog(boolean attachBuildLog = true) {
        triggerAttachBuildLog = attachBuildLog
    }

    /**
     * Specifies the To list for the email.
     *
     * The {@code replyToList} parameter must be a comma-separated list of recipients.
     */
    void replyTo(String replyToList) {
        triggerReplyTo = replyToList
    }

    /**
     * Specifies the To list for the email.
     */
    void replyTo(List<String> replyToList) {
        replyTo(replyToList.join(','))
    }
}
