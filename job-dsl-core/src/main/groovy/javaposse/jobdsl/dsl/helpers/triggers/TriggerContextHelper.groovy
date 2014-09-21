package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper

/**
 triggers {scm(String cronString)
 cron(String cronString)}*/
class TriggerContextHelper extends AbstractContextHelper<TriggerContext> {

    TriggerContextHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    /**
     * Public method available on job {}* @param closure
     * @return
     */
    def triggers(Closure closure) {
        execute(closure, new TriggerContext(withXmlActions, type))
    }

    Closure generateWithXmlClosure(TriggerContext context) {
        return { Node project ->
            def triggersNode
            if (project.triggers.isEmpty()) {
                triggersNode = project.appendNode('triggers', [class: 'vector'])
            } else {
                triggersNode = project.triggers[0]
            }
            context.triggerNodes.each {
                triggersNode << it
            }
        }
    }
}
