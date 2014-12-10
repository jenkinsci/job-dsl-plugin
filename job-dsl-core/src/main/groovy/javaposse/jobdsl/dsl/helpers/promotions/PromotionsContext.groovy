package javaposse.jobdsl.dsl.helpers.promotions

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.ContextHelper

class PromotionsContext implements Context {

    Map<String,Node> promotionNodes = [:]

    Map<String,Node> subPromotionNodes = [:]

    /**
     * PromotionNodes:
     * 1. <string>dev</string>
     * 2. <string>test</string>
     *
     * AND
     *
     * Sub PromotionNode for every promotion
     * 1. <project>
     *     <name>dev</name>
     *     .
     *     .
     *     .
     * </project>
     * 2. <project>
     *     <name>test</name>
     *     .
     *     .
     *     .
     * </project>
     *
     * @param promotionName
     * @return
     */
    boolean promotion(String promotionName, Closure promotionClosure = null) {
        Preconditions.checkArgument(!promotionNodes.containsKey(promotionName),
                                    'promotion $promotionName already defined')
        Preconditions.checkNotNull(promotionName, 'promotionName cannot be null')
        Preconditions.checkArgument(promotionName.length() > 0)
        Node promotionNode = new Node(null, 'string', promotionName)
        promotionNodes[promotionName] = promotionNode

        PromotionContext promotionContext = new PromotionContext()
        ContextHelper.executeInContext(promotionClosure, promotionContext)

        subPromotionNodes[promotionName] = new NodeBuilder().'project' {
            // Conditions to proof before promotion
            if (promotionContext.conditions) {
                promotionContext.conditions.each { ConditionsContext condition ->
                    conditions(condition.createConditionNode().children())
                }
            }

            // Icon, i.e. star-green
            if (promotionContext.icon) {
                icon(promotionContext.icon)
            }

            // Restrict label
            if (promotionContext.restrict) {
                assignedLabel(promotionContext.restrict)
            }
        }

        // Actions for promotions ... BuildSteps
        Node steps = new NodeBuilder().'buildSteps'()
        if (promotionContext.actions) {
            promotionContext.actions.each { steps.append(it) }
        }
        subPromotionNodes[promotionName].append(steps)
    }
}
