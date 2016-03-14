package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Preconditions

class ExtendedEmailContext implements Context {
    private static final Set<String> VALID_CONTENT_TYPES = ['default', 'text/plain', 'text/html', 'both']

    final List<String> recipientList = []
    final ExtendedEmailTriggersContext triggersContext = new ExtendedEmailTriggersContext()
    String contentType = 'default'
    String defaultSubject = '$DEFAULT_SUBJECT'
    String defaultContent = '$DEFAULT_CONTENT'
    final List<String> attachmentPatterns = []
    String preSendScript = '$DEFAULT_PRESEND_SCRIPT'
    final List<String> additionalGroovyClasspath = []
    boolean attachBuildLog
    boolean compressBuildLog
    final List<String> replyToList = []
    boolean saveToWorkspace
    boolean disabled
    Closure configureBlock

    /**
     * Adds email addresses that should receive emails. Defaults to {@code '$DEFAULT_RECIPIENTS'}. Can be called
     * multiple times to add more recipients.
     */
    void recipientList(String... recipients) {
        this.recipientList.addAll(recipients)
    }

    /**
     * Adds an email trigger. Can be called multiple times to add more recipients. Defaults to a {@code failure}
     * trigger.
     */
    void triggers(@DslContext(ExtendedEmailTriggersContext) Closure closure) {
        ContextHelper.executeInContext(closure, triggersContext)
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
     * Sets the default email subject that will be used for each email that is sent. Defaults to
     * {@code '$DEFAULT_SUBJECT'}.
     */
    void defaultSubject(String defaultSubject) {
        this.defaultSubject = defaultSubject
    }

    /**
     * Sets the default email content that will be used for each email that is sent. Defaults to
     * {@code '$DEFAULT_CONTENT'}.
     */
    void defaultContent(String defaultContent) {
        this.defaultContent = defaultContent
    }

    /**
     * Adds Ant-style patterns for attachments that will be used for the email. Can be called multiple times to add
     * more attachments.
     */
    void attachmentPatterns(String... attachmentPatterns) {
        this.attachmentPatterns.addAll(attachmentPatterns)
    }

    /**
     * Sets a script that will be run prior to sending the email to allow modifying the email before sending. Defaults
     * to {@code '$DEFAULT_PRESEND_SCRIPT'}.
     */
    void preSendScript(String preSendScript) {
        this.preSendScript = preSendScript
    }

    /**
     * Adds entries to the classpath used for running Groovy scripts. Can be called multiple times to add more classpath
     * entries.
     */
    void additionalGroovyClasspath(String... additionalGroovyClasspath) {
        this.additionalGroovyClasspath.addAll(additionalGroovyClasspath)
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
     * Adds e-mail addresses to use in the Reply-To header of the email. Defaults to {@code '$DEFAULT_REPLYTO'}. Can be
     * called multiple times to add more recipients.
     */
    void replyToList(String... replyToList) {
        this.replyToList.addAll(replyToList)
    }

    /**
     * If set, saves the generated email content to a file in the workspace. Defaults to {@code false}.
     */
    void saveToWorkspace(boolean saveToWorkspace = true) {
        this.saveToWorkspace = saveToWorkspace
    }

    /**
     * Disables the publisher. Defaults to {@code false}.
     */
    void disabled(boolean disabled = true) {
        this.disabled = disabled
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code hudson.plugins.emailext.ExtendedEmailPublisher} node
     * is passed into the configure block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        this.configureBlock = configureBlock
    }
}
