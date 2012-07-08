package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.WithXmlAction

/**
 triggers {
   scm(String cronString)
   cron(String cronString)
 }
*/
class TriggerHelper extends AbstractHelper<TriggerContext> {

    TriggerHelper(List<WithXmlAction> withXmlActions) {
        this.withXmlActions = withXmlActions
    }

    static class TriggerContext implements Context {
        // Orphan <scm> Node that can be attached to project in the withXmlAction
        List<Node> triggerNodes = []

        TriggerContext() {
        }

        TriggerContext(List<Node> triggerNodes) {
            this.triggerNodes = triggerNodes
        }

        def cron(String cronString) {
            Preconditions.checkNotNull(cronString)
            triggerNodes << new NodeBuilder().'hudson.triggers.TimerTrigger' {
                spec cronString
            }
        }

        /**
         <triggers class="vector">
           <hudson.triggers.SCMTrigger>
             <spec>10 * * * *</spec>
           </hudson.triggers.SCMTrigger>
         </triggers>
        */
        def scm(String cronString) {
            Preconditions.checkNotNull(cronString)
            triggerNodes << new NodeBuilder().'hudson.triggers.SCMTrigger' {
                spec cronString
            }
        }
    }

    def triggers(Closure closure) {
        execute(closure, new TriggerContext())
    }

    Closure generateWithXmlClosure(TriggerContext context) {
        return { Node project ->
            def triggersNode
            if (project.triggers.isEmpty()) {
                triggersNode = project.appendNode('triggers', [class:'vector'])
            } else {
                triggersNode = project.triggers[0]
            }
            context.triggerNodes.each {
                triggersNode << it
            }
        }
    }
}