package javaposse.jobdsl.dsl.helpers.promotions

import java.util.List;

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.XmlConfig;
import javaposse.jobdsl.dsl.XmlConfigType;
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.Promotion

class PromotionsContextHelper extends AbstractContextHelper<PromotionsContext> {

    // helds the WithXmlActions for every promotion
    List<XmlConfig> additionalConfigs

    PromotionsContextHelper(List<WithXmlAction> withXmlActions, List<XmlConfig> additionalConfigs, JobType jobType) {
        super(withXmlActions, jobType)
        this.additionalConfigs = additionalConfigs
    }

    def promotions(Closure closure) {
        execute(closure, new PromotionsContext())
        
        // extra execution for the generation of each XML per promotion
        executeAdditional(closure, new PromotionsContext())
    }

    Closure generateWithXmlClosure(PromotionsContext context) {
        return { Node project ->
            def promotions = project / 'properties' / 'hudson.plugins.promoted__builds.JobPropertyImpl' (plugin:"promoted-builds@2.15") / 'activeProcessNames'
            context.promotionNodes.values().each { promotions << it }
        }
    }

    Closure generateAdditionalWithXmlClosure(PromotionsContext context, String promotionName) {
        // special XML closure generator for the promotions config.xml
        return { Node project ->
            def promotion = project
            context.subPromotionNodes.get(promotionName).children().each {
                def name = it.name()
                appendOrReplaceNode(promotion, name, it)
            }
        }
    }

    private void appendOrReplaceNode(Node node, String name, Node replace) {
        node.children().removeAll { it instanceof Node && it.name() == name }
        node.append replace
    }

    def executeAdditional(Closure closure, PromotionsContext promotionsContext) {
        // Execute context, which we expect will just establish some state
        executeInContext(closure, promotionsContext)

        // Add promotions actions for each promotion in the context
        promotionsContext.subPromotionNodes.each { name, node ->
            def xmlConfig = getAdditionalXmlConfig(name)
            if (!xmlConfig) {
                xmlConfig = new Promotion(name)
                additionalConfigs << xmlConfig
            }
            def xmlActions = xmlConfig.withXmlActions
            xmlActions << generateAdditionalWithXmlAction(promotionsContext, name)
        }

        return promotionsContext
    }
    
    private XmlConfig getAdditionalXmlConfig(String name) {
        additionalConfigs.each {
            if (it.configType == XmlConfigType.PROMOTION && it.name.equals(name)) {
                return it
            }
        }
        return null
    }

    WithXmlAction generateAdditionalWithXmlAction(PromotionsContext context, String promotionName) {
        // Closure to be run later, in this context we're given the root node with the WithXmlAction magic
        Closure withXmlClosure = generateAdditionalWithXmlClosure(context, promotionName)
        return new WithXmlAction(withXmlClosure)
    }

}

