package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

/**
 * @since 1.58
 */
@ContextType('jenkins.scm.api.SCMNavigator')
class ScmNavigatorsContext extends AbstractExtensibleContext {
    final List<Node> scmNavigatorNodes = []

    ScmNavigatorsContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(final Node node) {
        scmNavigatorNodes << node
    }
}
