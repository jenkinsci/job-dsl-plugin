package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class ExtendedEmailTriggersContext implements Context {
    final List<Node> configuredTriggers = []

    /**
     * Triggers an email if the build status is "Aborted".
     */
    void aborted(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('Aborted', closure)
    }

    /**
     * Always triggers an email after the build, regardless of the status of the build.
     */
    void always(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('Always', closure)
    }

    /**
     * Triggers an email when the build begins, but after SCM polling has completed.
     */
    void beforeBuild(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('PreBuild', closure)
    }

    /**
     * Triggers an email when the build status changes from "Success" to "Failure".
     */
    void firstFailure(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('FirstFailure', closure)
    }

    /**
     * Triggers an email when the build fails twice in a row after a successful build.
     */
    void secondFailure(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('SecondFailure', closure)
    }

    /**
     * Triggers an email any time the build fails.
     */
    void failure(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('Failure', closure)
    }

    /**
     * Triggers an email if the build status is "Failure" for two or more builds in a row.
     */
    void stillFailing(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('StillFailing', closure)
    }

    /**
     * Triggers an email when the build status changes from "Failure" or "Unstable" to "Success".
     */
    void fixed(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('Fixed', closure)
    }

    /**
     * Triggers an email if the build status is "Not Built".
     */
    void notBuilt(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('NotBuilt', closure)
    }

    /**
     * Triggers an email if the build status changes.
     */
    void statusChanged(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('StatusChanged', closure)
    }

    /**
     * Triggers an email if the build status is "Successful".
     */
    void success(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('Success', closure)
    }

    /**
     * Triggers an email any time there is an improvement.
     */
    void improvement(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('Improvement', closure)
    }

    /**
     * Triggers an email any time there is a regression.
     */
    void regression(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('Regression', closure)
    }

    /**
     * Triggers an email any time the build is unstable.
     */
    void unstable(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('Unstable', closure)
    }

    /**
     * Triggers an email when the build status changes from anything to "Unstable".
     */
    void firstUnstable(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('FirstUnstable', closure)
    }

    /**
     * Triggers an email if the build status is "Unstable" for two or more builds in a row.
     */
    void stillUnstable(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('StillUnstable', closure)
    }

    /**
     * Triggers an email when the build status changes from "Failure" or "Unstable" to "Success".
     */
    void fixedUnhealthy(@DslContext(ExtendedEmailTriggerContext) Closure closure = null) {
        addTrigger('FixedUnhealthy', closure)
    }

    protected void addTrigger(String name, Closure closure) {
        ExtendedEmailTriggerContext context = new ExtendedEmailTriggerContext()
        ContextHelper.executeInContext(closure, context)

        configuredTriggers << new NodeBuilder()."hudson.plugins.emailext.plugins.trigger.${name}Trigger" {
            email {
                recipientList(context.recipientList.join(', '))
                subject(context.subject ?: '')
                body(context.content ?: '')
                recipientProviders(context.sendToContext.recipientProviders)
                attachmentsPattern(context.attachmentPatterns.join(', '))
                attachBuildLog(context.attachBuildLog)
                compressBuildLog(context.compressBuildLog)
                replyTo(context.replyToList ? context.replyToList.join(', ') : '$PROJECT_DEFAULT_REPLYTO')
                contentType(context.contentType)
            }
        }
    }
}
