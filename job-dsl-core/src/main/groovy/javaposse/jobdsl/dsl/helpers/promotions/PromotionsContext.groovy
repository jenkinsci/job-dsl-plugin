package javaposse.jobdsl.dsl.helpers.promotions

import groovy.lang.Closure;
import groovy.util.Node;

import java.util.List;

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.Context;
import javaposse.jobdsl.dsl.helpers.common.DownstreamTriggerContext;
import javaposse.jobdsl.dsl.helpers.step.AbstractStepContext;

import com.google.common.base.Preconditions

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper

class PromotionsContext implements Context {

    Map<String,Node> promotionNodes = [:]

    Map<String,Node> subPromotionNodes = [:]

    /**
     * <project>
     *     <properties>
     *         <hudson.plugins.promoted__builds.JobPropertyImpl>
     *             <activeProcessNames>
     *                 <string>dev</string>
     *
     * @param promotionName
     * @return
     */	
    def promotion(String promotionName, Closure promotionClosure = null) {
        Preconditions.checkArgument(!promotionNodes.containsKey(promotionName), 'promotion $promotionName already defined')
        Preconditions.checkNotNull(promotionName, 'promotionName cannot be null')
        Preconditions.checkArgument(promotionName.length() > 0)
        Node promotionNode = new Node(null, 'string', promotionName)
        promotionNodes[promotionName] = promotionNode

        PromotionContext promotionContext = new PromotionContext()
        AbstractContextHelper.executeInContext(promotionClosure, promotionContext)

        subPromotionNodes[promotionName] = new NodeBuilder().'project' {
            // Conditions
            if (promotionContext.conditions) {
                promotionContext.conditions.each {ConditionsContext condition ->
                    conditions(condition.createConditionNode().children())
                }
            }

            // Icon
            if (promotionContext.icon) {
                icon(promotionContext.icon)
            }

            // Restrict label
            if (promotionContext.restrict) {
                assignedLabel(promotionContext.restrict)
            }
        }

        // Actions

        def steps = new NodeBuilder().'buildSteps'()
        if (promotionContext.actions) {
            promotionContext.actions.each { steps.append(it) }
        }
        subPromotionNodes[promotionName].append(steps)

        // ...
    }

}
