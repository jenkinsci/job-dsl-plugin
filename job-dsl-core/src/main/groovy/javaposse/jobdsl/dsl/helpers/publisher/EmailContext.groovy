package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class EmailContext implements Context {
    Set<String> emailTriggerNames = [
            'PreBuild', 'StillUnstable', 'Fixed', 'Success', 'StillFailing', 'Improvement',
            'Failure', 'Regression', 'Aborted', 'NotBuilt', 'FirstFailure', 'Unstable',
            'Always', 'SecondFailure', 'FirstUnstable', 'FixedUnhealthy', 'StatusChanged',

    ]
    List<EmailTriggerContext> emailTriggers = []
    String emailRecipients
    String emailSubject
    String emailContent
    boolean emailAttachBuildLog = false
    String emailReplyTo
    Closure configureClosure

    /**
     * Specifies the default recipient list for an email.
     *
     * The {@code recipients} parameter must be a comma-separated list of recipients.
     */
    void recipientList(String recipients) {
        emailRecipients = recipients
    }

    /**
     * Specifies the default recipient list for an email.
     */
    void recipientList(List<String> recipients) {
        recipientList(recipients.join(','))
    }

    /**
     * Specifies the default subject for an email.
     */
    void defaultSubject(String subject) {
        emailSubject = subject
    }

    /**
     * Specifies the default content for an email.
     */
    void defaultContent(String content) {
        emailContent = content
    }

    /**
     * Specifies whether or not to attach the build log to an email.
     */
    void attachBuildLog(boolean attach = true) {
        emailAttachBuildLog = attach
    }

    /**
     * Specifies the default To list for an email.
     *
     * The {@code recipients} parameter must be a comma-separated list of recipients.
     */
    void replyTo(String recipients) {
        emailReplyTo = recipients
    }

    /**
     * Specifies the default To list for an email.
     */
    void replyTo(List<String> recipients) {
        replyTo(recipients.join(','))
    }

    /**
     * Specifies the condition that should cause an email notification to be sent. Can be called multiple times to add
     * more triggers.
     *
     * The map can contain one or more of the following keys: {@code triggerName}, {@code subject}, {@code body},
     * {@code recipientList}, {@code sendToDevelopers}, {@code sendToRequester}, {@code includeCulprits},
     * {@code sendToRecipientList} and {@code replyTo}.
     *
     * The {@code triggerName} must be one of {@code `PreBuild`}, {@code `StillUnstable`}, {@code `Fixed`},
     * {@code `Success`}, {@code `StillFailing`}, {@code `Improvement`}, {@code `Failure`}, {@code `Regression`},
     * {@code `Aborted`}, {@code `NotBuilt`}, {@code `FirstFailure`}, {@code `Unstable`}, {@code `Always`},
     * {@code `SecondFailure`}, {@code `FirstUnstable`}, {@code `FixedUnhealthy`} or {@code `StatusChanged`}.
     * Older versions of the Email-ext plugin do not support all triggers.
     */
    void trigger(Map args) {
        trigger(args.triggerName, args.subject, args.body, args.recipientList, args.sendToDevelopers,
                args.sendToRequester, args.includeCulprits, args.sendToRecipientList, args.replyTo)
    }

    /**
     * Specifies the condition that should cause an email notification to be sent. Can be called multiple times to add
     * more triggers.
     *
     * The {@code triggerName} must be one of {@code `PreBuild`}, {@code `StillUnstable`}, {@code `Fixed`},
     * {@code `Success`}, {@code `StillFailing`}, {@code `Improvement`}, {@code `Failure`}, {@code `Regression`},
     * {@code `Aborted`}, {@code `NotBuilt`}, {@code `FirstFailure`}, {@code `Unstable`}, {@code `Always`},
     * {@code `SecondFailure`}, {@code `FirstUnstable`}, {@code `FixedUnhealthy`} or {@code `StatusChanged`}.
     * Older versions of the Email-ext plugin do not support all triggers.
     */
    void trigger(String triggerName, String subject = null, String body = null, String recipientList = null,
                 Boolean sendToDevelopers = null, Boolean sendToRequester = null, Boolean includeCulprits = null,
                 Boolean sendToRecipientList = null, String replyTo = null) {
        checkArgument(emailTriggerNames.contains(triggerName), "Possible values: ${emailTriggerNames.join(',')}")

        emailTriggers << new EmailTriggerContext(triggerName, recipientList, subject, body,
                sendToDevelopers, sendToRequester, includeCulprits, sendToRecipientList, replyTo)
    }

    /**
     * Specifies the condition that should cause an email notification to be sent. Can be called multiple times to add
     * more triggers.
     *
     * The {@code triggerName} must be one of {@code `PreBuild`}, {@code `StillUnstable`}, {@code `Fixed`},
     * {@code `Success`}, {@code `StillFailing`}, {@code `Improvement`}, {@code `Failure`}, {@code `Regression`},
     * {@code `Aborted`}, {@code `NotBuilt`}, {@code `FirstFailure`}, {@code `Unstable`}, {@code `Always`},
     * {@code `SecondFailure`}, {@code `FirstUnstable`}, {@code `FixedUnhealthy`} or {@code `StatusChanged`}.
     * Older versions of the Email-ext plugin do not support all triggers.
     */
    void trigger(String triggerName, @DslContext(EmailTriggerContext) Closure triggerClosure) {
        checkArgument(emailTriggerNames.contains(triggerName), "Possible values: ${emailTriggerNames.join(',')}")

        EmailTriggerContext triggerContext = new EmailTriggerContext(triggerName)
        ContextHelper.executeInContext(triggerClosure, triggerContext)
        emailTriggers << triggerContext
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code hudson.plugins.emailext.ExtendedEmailPublisher} node
     * is passed into the configure block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureClosure) {
        this.configureClosure = configureClosure
    }
}
