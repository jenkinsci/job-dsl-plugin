package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

import static com.google.common.base.Preconditions.checkArgument

class EmailContext implements Context {
    def emailTriggerNames = ['PreBuild', 'StillUnstable', 'Fixed', 'Success', 'StillFailing', 'Improvement',
            'Failure', 'Regression', 'Aborted', 'NotBuilt', 'FirstFailure', 'Unstable']
    def emailTriggers = []

    // Not sure why a map syntax wouldn't call method below, so creating this one
    def trigger(Map args) {
        trigger(args.triggerName, args.subject, args.body, args.recipientList, args.sendToDevelopers,
                args.sendToRequester, args.includeCulprits, args.sendToRecipientList)
    }

    def trigger(String triggerName, String subject = null, String body = null, String recipientList = null,
                Boolean sendToDevelopers = null, Boolean sendToRequester = null, Boolean includeCulprits = null,
                Boolean sendToRecipientList = null) {
        checkArgument(emailTriggerNames.contains(triggerName), "Possible values: ${emailTriggerNames.join(',')}")

        emailTriggers << new EmailTrigger(triggerName, recipientList, subject, body, sendToDevelopers, sendToRequester,
                includeCulprits, sendToRecipientList)
    }

    Closure configureClosure

    def configure(Closure configureClosure) {
        // save for later
        this.configureClosure = configureClosure
    }

    static class EmailTrigger {
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

        String triggerShortName, recipientList, subject, body
        boolean sendToDevelopers, sendToRequester, includeCulprits, sendToRecipientList

    }
}
