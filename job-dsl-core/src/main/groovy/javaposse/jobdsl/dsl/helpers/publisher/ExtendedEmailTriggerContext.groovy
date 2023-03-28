package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Preconditions

class ExtendedEmailTriggerContext implements Context {
    private static final Set<String> VALID_CONTENT_TYPES = ['project', 'text/plain', 'text/html', 'both']

    final List<String> recipientList = []
    String contentType = 'project'
    String subject = '$PROJECT_DEFAULT_SUBJECT'
    String content = '$PROJECT_DEFAULT_CONTENT'
    final ExtendedEmailSendToContext sendToContext = new ExtendedEmailSendToContext()
    final List<String> attachmentPatterns = []
    boolean attachBuildLog
    boolean compressBuildLog
    final List<String> replyToList = []

    /**
     * Specifies the email recipients.
     */
    void sendTo(@DslContext(ExtendedEmailSendToContext) Closure closure) {
        ContextHelper.executeInContext(closure, sendToContext)
    }

    /**
     * Adds email addresses that should receive emails for this trigger. Can be called multiple times to add more
     * recipients.
     */
    void recipientList(String... recipients) {
        this.recipientList.addAll(recipients)
    }

    /**
     * Sets the content type of the emails sent after a build. Valid values are {@code 'default'}, {@code 'text/plain'},
     * {@code 'text/html'} and {@code 'both'}. Defaults to {@code 'default'}.
     */
    void contentType(String contentType) {
        Preconditions.checkArgument(
                VALID_CONTENT_TYPES.contains(contentType),
                "contentType must be one of ${VALID_CONTENT_TYPES.join(', ')}"
        )
        this.contentType = contentType
    }

    /**
     * Sets the email subject that will be used for this email trigger. Defaults to {@code '$PROJECT_DEFAULT_SUBJECT'}.
     */
    void subject(String subject) {
        this.subject = subject
    }

    /**
     * Sets the email content that will be used for this email trigger. Defaults to {@code '$PROJECT_DEFAULT_CONTENT'}.
     */
    void content(String content) {
        this.content = content
    }

    /**
     * Adds Ant-style patterns for attachments that will be used for the email. Can be called multiple times to add
     * more attachments.
     */
    void attachmentPatterns(String... attachmentPatterns) {
        this.attachmentPatterns.addAll(attachmentPatterns)
    }

    /**
     * If set, attaches the log from the build to the email. Defaults to {@code false}.
     */
    void attachBuildLog(boolean attachBuildLog = true) {
        this.attachBuildLog = attachBuildLog
    }

    /**
     * If set, attaches a compressed log from the build to the email. Defaults to {@code false}.
     */
    void compressBuildLog(boolean compressBuildLog = true) {
        this.compressBuildLog = compressBuildLog
    }

    /**
     * Adds e-mail addresses to use in the Reply-To header of the email. Defaults to {@code '$PROJECT_DEFAULT_REPLYTO'}.
     * Can be called multiple times to add more recipients.
     */
    void replyToList(String... replyToList) {
        this.replyToList.addAll(replyToList)
    }
}
