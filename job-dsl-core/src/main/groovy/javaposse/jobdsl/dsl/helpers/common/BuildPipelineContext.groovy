package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper

class BuildPipelineContext implements Context {
    def createManualDownstreamNode(String projects = '', Closure closure = null) {

        // Set up the downstream manual node context
        DownstreamTriggerContext downstreamTriggerContext = new DownstreamTriggerContext()

        AbstractContextHelper.executeInContext(closure, downstreamTriggerContext)

        // Set up the node
        def nodeBuilder = NodeBuilder.newInstance()
        def nodeName = 'au.com.centrumsystems.hudson.plugin.buildpipeline.trigger.BuildPipelineTrigger'

        return nodeBuilder."${nodeName}" {
            downstreamProjectNames (projects ?: '')

            if (downstreamTriggerContext.hasParameter()) {
                configs(downstreamTriggerContext.createParametersNode().children())
            } else {
                configs('class': 'java.util.Collections$EmptyList')
            }
        }
    }
}
