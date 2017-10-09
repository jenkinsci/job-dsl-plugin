package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.JobManagement

@ContextType('au.com.centrumsystems.hudson.plugin.buildpipeline.extension.PipelineHeaderExtension')
class BuildPipelineHeadersContext extends AbstractExtensibleContext {
    Node headersNode

    BuildPipelineHeadersContext(JobManagement jobManagement) {
        super(jobManagement, null)
    }

    @Override
    protected void addExtensionNode(Node node) {
        headersNode = node
    }
}
