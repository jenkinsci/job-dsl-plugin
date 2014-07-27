package javaposse.jobdsl.dsl.helpers.promotions

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.additional.AdditionalXmlConfig
import javaposse.jobdsl.dsl.additional.Promotion
import javaposse.jobdsl.dsl.helpers.AbstractAdditionalContextHelper

class PromotionsContextHelper extends AbstractAdditionalContextHelper<PromotionsContext> {

    PromotionsContextHelper(List<WithXmlAction> withXmlActions, List<AdditionalXmlConfig> additionalConfigs,
                            JobType jobType) {
        super(withXmlActions, additionalConfigs, jobType)
    }

    def promotions(Closure closure) {
        execute(closure, new PromotionsContext())
    }

    @Override
    Closure generateWithXmlClosure(PromotionsContext context) {
        return { Node project ->
            def promotions = project / 'properties' /
                    'hudson.plugins.promoted__builds.JobPropertyImpl' (plugin: 'promoted-builds@2.15') /
                    'activeProcessNames'
            context.promotionNodes.values().each { promotions << it }
        }
    }

    @Override
    Map<String, Closure> generateAdditionalWithXmlClosures(PromotionsContext context) {
        // special XML closure generator for the promotions config.xml
        Map<String, Closure> closures = [:]
        context.subPromotionNodes.keySet().each { k ->
            closures.put(k) { Node project ->
                def promotion = project
                context.subPromotionNodes.get(k).children().each {
                    def name = it.name()
                    appendOrReplaceNode(promotion, name, it)
                }
            }
        }
        closures
    }

    @Override
    AdditionalXmlConfig createXmlConfig(String name, Closure closure) {
        AdditionalXmlConfig xmlConfig = new Promotion(name)
        xmlConfig.withXmlActions << new WithXmlAction(closure)
        xmlConfig
    }

    private void appendOrReplaceNode(Node node, String name, Node replace) {
        node.children().removeAll { it instanceof Node && it.name() == name }
        node.append replace
    }

}

