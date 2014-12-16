package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

import static com.google.common.base.Preconditions.checkArgument

class EmailContext implements Context {
    Set<String> emailTriggerNames = [
            'PreBuild', 'StillUnstable', 'Fixed', 'Success', 'StillFailing', 'Improvement',
            'Failure', 'Regression', 'Aborted', 'NotBuilt', 'FirstFailure', 'Unstable',
            'Always', 'SecondFailure', 'FirstUnstable', 'FixedUnhealthy', 'StatusChanged',

    ]
    List<EmailTrigger> emailTriggers = []
    Closure configureClosure

    void trigger(Map args) {
        trigger(args.triggerName, args.subject, args.body, args.recipientList, args.sendToDevelopers,
                args.sendToRequester, args.includeCulprits, args.sendToRecipientList)
    }

    void trigger(String triggerName, String subject = null, String body = null, String recipientList = null,
                Boolean sendToDevelopers = null, Boolean sendToRequester = null, Boolean includeCulprits = null,
                Boolean sendToRecipientList = null) {
        checkArgument(emailTriggerNames.contains(triggerName), "Possible values: ${emailTriggerNames.join(',')}")

        emailTriggers << new EmailTrigger(triggerName, recipientList, subject, body, sendToDevelopers, sendToRequester,
                includeCulprits, sendToRecipientList)
    }

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
