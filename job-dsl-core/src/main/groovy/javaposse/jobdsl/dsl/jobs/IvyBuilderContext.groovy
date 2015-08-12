package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.AbstractExtensibleContext
import javaposse.jobdsl.dsl.helpers.step.AntContext

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class IvyBuilderContext extends AbstractExtensibleContext {

    final List<Node> ivyBuilderNodes = []

    IvyBuilderContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        ivyBuilderNodes << node
    }

    @RequiresPlugin(id = 'ant')
    void ant(@DslContext(AntContext) Closure antClosure = null) {
        AntContext antContext = new AntContext()
        executeInContext(antClosure, antContext)

        ivyBuilderNodes << new NodeBuilder().ivyBuilderType(class: 'hudson.ivy.builder.AntIvyBuilderType') {
            targets antContext.targets.join(' ')
            antName antContext.antName ?: '(Default)'
            if (antContext.antOpts) {
                antOpts antContext.antOpts.join('\n')
            }
            if (antContext.buildFile) {
                buildFile antContext.buildFile
            }
            if (antContext.props) {
                antProperties antContext.props.join('\n')
            }
        }
    }
}
