package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.common.DownstreamTriggerContext

class BuildPipelineContext implements Context {

    private DownstreamTriggerContext trigger = null

    def parameters(Closure closure) {
        // Set up the downstream manual node context
        DownstreamTriggerContext downstreamTriggerContext = new DownstreamTriggerContext()

        AbstractContextHelper.executeInContext(closure, downstreamTriggerContext)

        trigger = downstreamTriggerContext
    }

    def createManualDownstreamNode(String projects = '') {

        // Set up the node
        def nodeBuilder = NodeBuilder.newInstance()
        def nodeName = 'au.com.centrumsystems.hudson.plugin.buildpipeline.trigger.BuildPipelineTrigger'

        return nodeBuilder."${nodeName}" {
            downstreamProjectNames (projects ?: '')

            if (trigger && trigger.hasParameter()) {
                configs(trigger.createParametersNode().children())
            } else {
                configs('class': 'java.util.Collections$EmptyList')
            }
        }
    }
}
