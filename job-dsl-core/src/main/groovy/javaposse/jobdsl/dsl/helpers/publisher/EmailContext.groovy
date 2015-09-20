package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class EmailContext implements Context {
    Set<String> emailTriggerNames = [
            'PreBuild', 'StillUnstable', 'Fixed', 'Success', 'StillFailing', 'Improvement',
            'Failure', 'Regression', 'Aborted', 'NotBuilt', 'FirstFailure', 'Unstable',
            'Always', 'SecondFailure', 'FirstUnstable', 'FixedUnhealthy', 'StatusChanged',

    ]
    List<EmailTrigger> emailTriggers = []
    Closure configureClosure

    /**
     * Specifies the condition that should cause an email notification to be sent. Can be called multiple times to add
     * more triggers.
     *
     * The map can contain one or more of the following keys: {@code triggerName}, {@code subject}, {@code body},
     * {@code recipientList}, {@code sendToDevelopers}, {@code sendToRequester}, {@code includeCulprits} and
     * {@code sendToRecipientList}.
     *
     * The {@code triggerName} must be one of {@code `PreBuild`}, {@code `StillUnstable`}, {@code `Fixed`},
     * {@code `Success`}, {@code `StillFailing`}, {@code `Improvement`}, {@code `Failure`}, {@code `Regression`},
     * {@code `Aborted`}, {@code `NotBuilt`}, {@code `FirstFailure`}, {@code `Unstable`}, {@code `Always`},
     * {@code `SecondFailure`}, {@code `FirstUnstable`}, {@code `FixedUnhealthy`} or {@code `StatusChanged`}.
     * Older versions of the Email-ext plugin do not support all triggers.
     */
    void trigger(Map args) {
        trigger(args.triggerName, args.subject, args.body, args.recipientList, args.sendToDevelopers,
                args.sendToRequester, args.includeCulprits, args.sendToRecipientList)
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
                 Boolean sendToRecipientList = null) {
        checkArgument(emailTriggerNames.contains(triggerName), "Possible values: ${emailTriggerNames.join(',')}")

        emailTriggers << new EmailTrigger(triggerName, recipientList, subject, body, sendToDevelopers, sendToRequester,
                includeCulprits, sendToRecipientList)
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

    static class EmailTrigger {
        String triggerShortName
        String recipientList
        String subject
        String body
        boolean sendToDevelopers
        boolean sendToRequester
        boolean includeCulprits
        boolean sendToRecipientList

        EmailTrigger(String triggerShortName, String recipientList = null, String subject = null, String body = null,
                     Boolean sendToDevelopers = null, Boolean sendToRequester = null,
                     Boolean includeCulprits = null, Boolean sendToRecipientList = null) {
            // Use elvis operator to assign default values if needed
            this.triggerShortName = triggerShortName
            this.recipientList = recipientList ?: ''
            this.subject = subject ?: '$PROJECT_DEFAULT_SUBJECT'
            this.body = body ?: '$PROJECT_DEFAULT_CONTENT'
            this.sendToDevelopers = (sendToDevelopers == null) ? false : sendToDevelopers
            this.sendToRequester = (sendToRequester == null) ? false : sendToRequester
            this.includeCulprits = (includeCulprits == null) ? false : includeCulprits
            this.sendToRecipientList = (sendToRecipientList == null) ? true : sendToRecipientList
        }
    }
}
