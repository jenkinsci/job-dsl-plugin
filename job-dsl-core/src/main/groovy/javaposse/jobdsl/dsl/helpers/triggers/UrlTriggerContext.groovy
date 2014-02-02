package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context


/**
 * Top level context for configuring the URL trigger functionality.
 */
class UrlTriggerContext implements Context {
    Closure configureClosure
    def label
    def entries = []
    String crontab = 'H/5 * * * *'

    UrlTriggerContext(String cron = null) {
        if (cron) this.crontab = cron
    }

    /** Adds configure closure for overriding the generated XML */
    def configure(Closure configureClosure) {
        this.configureClosure = configureClosure
    }

    /** restrict execution to label */
    def restrictToLabel(String label) {
        this.label = label
    }

    /** Sets the cron schedule */
    def cron(String cron) {
        this.crontab = cron
    }

    /** adds a monitored URL to the trigger. */
    def url(String url, Closure entryClosure = null) {
        UrlTriggerEntryContext entryContext = new UrlTriggerEntryContext(url)
        AbstractContextHelper.executeInContext(entryClosure, entryContext)
        entries << entryContext
    }

}
