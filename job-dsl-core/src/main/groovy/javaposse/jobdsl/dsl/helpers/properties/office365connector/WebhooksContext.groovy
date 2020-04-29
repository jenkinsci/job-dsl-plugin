package javaposse.jobdsl.dsl.helpers.properties.office365connector

import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class WebhooksContext implements Context {
    
    final List<Node> webhookNodes = []

    void webhook(@DslContext(WebhookContext) Closure webhookClosure = null) {
        
        WebhookContext webhookContext = new WebhookContext()
        ContextHelper.executeInContext(webhookClosure, webhookContext)

        checkNotNullOrEmpty(webhookContext.url, 'url must be specified')
        
        webhookNodes << new NodeBuilder().'jenkins.plugins.office365connector.Webhook' {
            
            url(webhookContext.url)
            startNotification(webhookContext.notifyBuildStart)            
            notifySuccess(webhookContext.notifySuccess)
            notifyAborted(webhookContext.notifyAborted)
            notifyNotBuilt(webhookContext.notifyNotBuilt)
            notifyUnstable(webhookContext.notifyUnstable)
            notifyFailure(webhookContext.notifyFailure)
            notifyBackToNormal(webhookContext.notifyBackToNormal)
            notifyRepeatedFailure(webhookContext.notifyRepeatedFailure)
            timeout(webhookContext.timeout)
        }
    }
}
