package javaposse.jobdsl.dsl.helpers.properties.office365connector

import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class Office365ConnectorWebhookJobPropertyContext implements Context {
    
    WebhooksContext webhookContext
    
    void webhooks(@DslContext(WebhooksContext) Closure webhooksClosure = null) {
        
        WebhooksContext webhookContext = new WebhooksContext()
        ContextHelper.executeInContext(webhooksClosure, webhookContext)

        this.webhookContext = webhookContext
    }
}
