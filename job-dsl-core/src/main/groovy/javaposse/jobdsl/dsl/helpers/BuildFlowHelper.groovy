package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

import static com.google.common.base.Preconditions.checkState

class BuildFlowHelper extends AbstractHelper implements Context {

    boolean buildFlowAdded = false

    BuildFlowHelper(List<WithXmlAction> withXmlActions, JobType type) {
        super(withXmlActions, type)
    }

    /**
     * Specifies text for the Build Flow Dsl block.
     * @param block of groovy DSL to be applied to the Build Flow block.
     */
    def buildFlow(String buildFlowText) {
        checkState type == JobType.BuildFlow, "Build Flow text can only be applied to Build Flow jobs."
        checkState !buildFlowAdded, "Build Flow text can only be applied once"
        buildFlowAdded = true
        execute { Node node ->
            appendOrReplaceNode node, 'dsl', buildFlowText
        }
    }

}
