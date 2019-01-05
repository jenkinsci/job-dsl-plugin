package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ComputedFolder
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.workflow.MultiBranchProjectFactoryContext
import javaposse.jobdsl.dsl.helpers.workflow.ScmNavigatorsContext

/**
 * @since 1.58
 */
class OrganizationFolderJob extends ComputedFolder {
    protected OrganizationFolderJob(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Sets the organizations in this folder.
     */
    void organizations(@DslContext(ScmNavigatorsContext) Closure closure) {
        ScmNavigatorsContext context = new ScmNavigatorsContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            Node navigators = project / navigators
            context.scmNavigatorNodes.each {
                navigators << it
            }
        }
    }

    /**
     * Sets the project factories for this folder.
     *
     * @since 1.66
     */
    void projectFactories(@DslContext(MultiBranchProjectFactoryContext) Closure closure) {
        MultiBranchProjectFactoryContext context = new MultiBranchProjectFactoryContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            Node factories = project / projectFactories
            factories.children().clear()
            context.projectFactoryNodes.each {
                factories << it
            }
        }
    }

    @Override
    protected Node getNodeTemplate() {
        new XmlParser().parse(this.class.getResourceAsStream("${this.class.simpleName}-template.xml"))
    }
}
