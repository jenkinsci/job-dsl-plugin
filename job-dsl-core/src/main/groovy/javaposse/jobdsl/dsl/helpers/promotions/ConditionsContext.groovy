package javaposse.jobdsl.dsl.helpers.promotions

import javaposse.jobdsl.dsl.helpers.BuildParametersContext
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper

class ConditionsContext implements Context {

    // Self Promotion Condition
    boolean isSelfPromotion = false
    boolean evenIfUnstable = false

    // Parametzerized Self Promotion Condition
    boolean isParameterizedSelfPromotion = false
    boolean evenIfUnstableParameterized = false
    String parameterName = null
    String parameterValue = null

    // Manual Promotion Condition
    boolean isManual = false
    String users = null
    List<Node> params = []

    // Release Build Condition
    boolean isReleaseBuild = false

    // Downstream Build Condition
    boolean isDownstreamPass = false
    boolean evenIfUnstableDownstream = false
    String jobs = null

    // Upstream Build Condition
    boolean isUpstreamPromotion = false
    String promotionNames = null

    void selfPromotion(Boolean evenIfUnstable) {
        isSelfPromotion = true
        if (evenIfUnstable) {
            this.evenIfUnstable = evenIfUnstable
        }
    }

    void parameterizedSelfPromotion(Boolean evenIfUnstable, String parameterName, String parameterValue) {
        parameterizedSelfPromotion = true
        if (evenIfUnstable) {
            this.parameterizedSelfPromotion = evenIfUnstable
        }
        this.parameterName = parameterName
        this.parameterValue = parameterValue
    }

    void manual(String users, Closure parametersClosure = null) {
        isManual = true
        this.users = users
        parameters(parametersClosure)
    }

    void releaseBuild() {
        isReleaseBuild = true
    }

    void parameters(Closure parametersClosure) {
        // delegate to main BuildParametersContext
        BuildParametersContext parametersContext = new BuildParametersContext()
        ContextHelper.executeInContext(parametersClosure, parametersContext)
        parametersContext.buildParameterNodes.values().each { params << it }
    }

    void downstream(Boolean evenIfUnstable, String jobs) {
        isDownstreamPass = true
        if (evenIfUnstable) {
            this.evenIfUnstableDownstream = evenIfUnstable
        }
        this.jobs = jobs
    }

    void upstream(String promotionNames) {
        isUpstreamPromotion = true
        this.promotionNames = promotionNames
    }

    Node createConditionNode() {
        NodeBuilder nodeBuilder = NodeBuilder.newInstance()
        nodeBuilder.'configs' {
            if (isSelfPromotion) {
                'hudson.plugins.promoted__builds.conditions.SelfPromotionCondition' {
                    delegate.createNode('evenIfUnstable', evenIfUnstable)
                }
            }
            if (isParameterizedSelfPromotion) {
                'hudson.plugins.promoted__builds.conditions.ParameterizedSelfPromotionCondition' {
                    delegate.createNode('evenIfUnstable', evenIfUnstableParameterized)
                    delegate.createNode('parameterName', parameterName)
                    delegate.createNode('parameterValue', parameterValue)
                }
            }
            if (isManual) {
                'hudson.plugins.promoted__builds.conditions.ManualCondition' {
                    delegate.createNode('users', users)
                    delegate.createNode('parameterDefinitions', params)
                }
            }
            if (isReleaseBuild) {
                'hudson.plugins.promoted__builds.conditions.ReleaseCondition'()
            }
            if (isDownstreamPass) {
                'hudson.plugins.promoted__builds.conditions.DownstreamPassCondition' {
                    delegate.createNode('evenIfUnstable', evenIfUnstableDownstream)
                    delegate.createNode('jobs', jobs)
                }
            }
            if (isUpstreamPromotion) {
                'hudson.plugins.promoted__builds.conditions.UpstreamPromotionCondition' {
                    delegate.createNode('requiredPromotionNames', promotionNames)
                }
            }
        }
    }
}
