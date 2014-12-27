package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

/**
 * Top level context for configuring the URL trigger functionality.
 */
class UrlTriggerContext implements Context {
    Closure configureClosure
    String label
    List<UrlTriggerEntryContext> entries = []
    String crontab = 'H/5 * * * *'

    UrlTriggerContext(String cron = null) {
        if (cron) {
            this.crontab = cron
        }
    }

    /** Adds configure closure for overriding the generated XML */
    void configure(Closure configureClosure) {
        this.configureClosure = configureClosure
    }

    /** restrict execution to label */
    void restrictToLabel(String label) {
        this.label = label
    }

    /** Sets the cron schedule */
    void cron(String cron) {
        this.crontab = cron
    }

    /** adds a monitored URL to the trigger. */
    void url(String url, @DslContext(UrlTriggerEntryContext) Closure entryClosure = null) {
        UrlTriggerEntryContext entryContext = new UrlTriggerEntryContext(url)
        ContextHelper.executeInContext(entryClosure, entryContext)
        entries << entryContext
    }

}
