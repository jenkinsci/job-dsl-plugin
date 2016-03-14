package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

/**
 * Top level context for configuring the URL trigger functionality.
 */
class UrlTriggerContext implements Context {
    Closure configureBlock
    String label
    List<UrlTriggerEntryContext> entries = []
    String crontab = 'H/5 * * * *'

    UrlTriggerContext(String cron = null) {
        if (cron) {
            this.crontab = cron
        }
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code URLTrigger} node is passed into the configure block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        this.configureBlock = configureBlock
    }

    /**
     * Restricts execution to label.
     */
    void restrictToLabel(String label) {
        this.label = label
    }

    /**
     * Sets the cron schedule. Defaults to {@code 'H/5 * * * *'}.
     */
    void cron(String cron) {
        this.crontab = cron
    }

    /**
     * Adds a monitored URL to the trigger.
     */
    void url(String url, @DslContext(UrlTriggerEntryContext) Closure entryClosure = null) {
        UrlTriggerEntryContext entryContext = new UrlTriggerEntryContext(url)
        ContextHelper.executeInContext(entryClosure, entryContext)
        entries << entryContext
    }
}
