package javaposse.jobdsl.dsl.helpers

import java.util.List
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.AdditionalXmlConfig

/**
 * Each helper has essentially two parts. First they run a closure in executeWithContext right away to build a context
 * object. Once we have the actually root, we run again via the generateWithXmlClosure.
 * @param < T >
 */
abstract class AbstractAdditionalContextHelper<T extends Context> extends AbstractContextHelper<T> {
    
    List<AdditionalXmlConfig> additionalConfigs

    AbstractAdditionalContextHelper(List<WithXmlAction> withXmlActions, List<AdditionalXmlConfig> additionalConfigs, JobType jobType) {
        super(withXmlActions, jobType)
        this.additionalConfigs = additionalConfigs
    }

    @Override
    def execute(Closure closure, T freshContext) {
        super.execute(closure, freshContext)
        
        // Add promotions actions for each promotion in the context
        generateAdditionalXmlConfigs(freshContext).each {
            additionalConfigs << it
        }
        
        return freshContext
    }
    
    List<AdditionalXmlConfig> generateAdditionalXmlConfigs(T context) {
        // Closure to be run later, in this context we're given the root node with the WithXmlAction magic
        Map<String, Closure> withXmlClosures = generateAdditionalWithXmlClosures(context)
        List<AdditionalXmlConfig> xmlConfigs = []
        withXmlClosures.each { name, closure ->
            xmlConfigs << createXmlConfig(name, closure)
        }
        return xmlConfigs
    }

    abstract AdditionalXmlConfig createXmlConfig(String name, Closure closure)
    
    abstract Map<String, Closure> generateAdditionalWithXmlClosures(T context)

}

