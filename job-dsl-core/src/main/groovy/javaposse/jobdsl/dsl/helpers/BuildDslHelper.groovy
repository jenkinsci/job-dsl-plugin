package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.common.BuildDslContext
import javaposse.jobdsl.dsl.helpers.step.AbstractStepContext

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkState

class BuildDslHelper extends AbstractHelper implements BuildDslContext {

    boolean buildDslAdded = false

    BuildDslHelper(List<WithXmlAction> withXmlActions, JobType type) {
        super(withXmlActions, type)
    }

    /**
     * Specifies Dsl text for the buildDsl Dsl block.
     * @param block of groovy DSL to be applied to the DSL block.
     */
    def buildDslBlock(String buildDslBlock) {
        checkState type == JobType.BuildDsl, "Build Dsl text can only be applied to BuildDsl jobs"
        checkState !buildDslAdded, "Build Dsl text can only be applied once"
        buildDslAdded = true
        execute { Node node ->
            appendOrReplaceNode node, 'dsl', buildDslBlock
        }
    }

    private static void appendOrReplaceNode(Node node, String name, Object value) {
        node.children().removeAll { it instanceof Node && it.name() == name }
        node.appendNode name, value
    }
}
