package javaposse.jobdsl.dsl.helpers.promotions

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.additional.AdditionalXmlConfig
import javaposse.jobdsl.dsl.additional.Promotion
import javaposse.jobdsl.dsl.helpers.Context

class PromotionsContextHelper {

    private PromotionsContextHelper() {
    }

    Closure generateWithXmlClosure(Context context) {
        return { Node project ->
            void promotions = project / 'properties' /
                    'hudson.plugins.promoted__builds.JobPropertyImpl' (plugin: 'promoted-builds@2.15') /
                    'activeProcessNames'
            context.promotionNodes.values().each { promotions << it }
        }
    }

    static List<AdditionalXmlConfig> generateAdditionalXmlConfigs(Context context) {
        // Closure to be run later, in this context we're given the root node with the WithXmlAction magic
        Map<String, Closure> withXmlClosures = generateAdditionalWithXmlClosures(context)
        List<AdditionalXmlConfig> xmlConfigs = []
        withXmlClosures.each { name, closure ->
            xmlConfigs << createXmlConfig(name, closure)
        }
        xmlConfigs
    }

    private static Map<String, Closure> generateAdditionalWithXmlClosures(Context context) {
        // special XML closure generator for the promotions config.xml
        Map<String, Closure> closures = [:]
        context.subPromotionNodes.keySet().each { k ->
            closures.put(k) { Node project ->
                Node promotion = project
                context.subPromotionNodes.get(k).children().each {
                    String name = it.name()
                    appendOrReplaceNode(promotion, name, it)
                }
            }
        }
        closures
    }

    private static AdditionalXmlConfig createXmlConfig(String name, Closure closure) {
        AdditionalXmlConfig xmlConfig = new Promotion(name)
        xmlConfig.withXmlActions << new WithXmlAction(closure)
        xmlConfig
    }

    private static boolean appendOrReplaceNode(Node node, String name, Node replace) {
        node.children().removeAll { it instanceof Node && it.name() == name }
        node.append replace
    }
}

