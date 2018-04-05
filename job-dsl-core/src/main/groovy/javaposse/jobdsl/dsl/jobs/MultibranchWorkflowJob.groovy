package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ComputedFolder
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.workflow.BranchSourcesContext
import javaposse.jobdsl.dsl.helpers.workflow.MultiBranchProjectFactoryContext

class MultibranchWorkflowJob extends ComputedFolder {
    MultibranchWorkflowJob(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Adds branch sources.
     */
    void branchSources(@DslContext(BranchSourcesContext) Closure sourcesClosure) {
        BranchSourcesContext context = new BranchSourcesContext(jobManagement, this)
        ContextHelper.executeInContext(sourcesClosure, context)

        configure { Node project ->
            context.branchSourceNodes.each {
                project / sources / data << it
            }
        }
    }

    /**
     * Sets the project factories for this folder.
     *
     * @since 1.67
     */
    void factory(@DslContext(MultiBranchProjectFactoryContext) Closure closure) {
        MultiBranchProjectFactoryContext context = new MultiBranchProjectFactoryContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            Node factory = project / factory
            if (factory) {
                project.remove(factory)
            }

            project << ContextHelper.toNamedNode('factory', context.projectFactoryNodes[0])
        }
    }

    @Override
    protected Node getNodeTemplate() {
        String version = jobManagement.isMinimumPluginVersionInstalled('workflow-multibranch', '2.10') ? '-2.10' : ''
        new XmlParser().parse(this.class.getResourceAsStream("${this.class.simpleName}${version}-template.xml"))
    }
}
