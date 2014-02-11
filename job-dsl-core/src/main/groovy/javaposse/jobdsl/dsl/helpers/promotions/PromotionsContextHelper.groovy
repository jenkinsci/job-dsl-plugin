package javaposse.jobdsl.dsl.helpers.promotions

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

class PromotionsContextHelper extends AbstractContextHelper<PromotionsContext> {

	Map<String, List<WithXmlAction>> withXmlActionsPromotions

	PromotionsContextHelper(List<WithXmlAction> withXmlActions, Map<String, List<WithXmlAction>> withXmlActionsPromotions, JobType jobType) {
		super(withXmlActions, jobType)
		this.withXmlActionsPromotions = withXmlActionsPromotions
	}

	def promotions(Closure closure) {
		execute(closure, new PromotionsContext())
	}

	Closure generateWithXmlClosure(PromotionsContext context) {
		return { Node project ->
			def promotions = project / 'properties' / 'hudson.plugins.promoted__builds.JobPropertyImpl' (plugin:"promoted-builds@2.15") / 'activeProcessNames'
			context.promotionNodes.values().each { promotions << it }
		}
	}

	Closure generateWithXmlClosurePromotions(PromotionsContext context, String promotionName) {
		return { Node project ->
			def promotion = project
			context.subPromotionNodes.get(promotionName).children().each {
				def name = it.name()
				appendOrReplaceNode(promotion, name, it)
			}
		}
	}

	private void appendOrReplaceNode(Node node, String name, Node replace) {
		node.children().removeAll { it instanceof Node && it.name() == name }
		node.append replace
	}

	@Override
	def execute(Closure closure, PromotionsContext promotionsContext) {
		// Execute context, which we expect will just establish some state
		executeInContext(closure, promotionsContext)

		// Queue up our action, using the concrete classes logic
		withXmlActions << generateWithXmlAction(promotionsContext)

		// Add promotions actions
		promotionsContext.subPromotionNodes.each { name, node ->
			def xmlActions = withXmlActionsPromotions.get(name)
			if (!xmlActions) {
				xmlActions = []
				withXmlActionsPromotions.put(name, xmlActions)
			}
			xmlActions << generateWithXmlActionPromotions(promotionsContext, name)
		}

		return promotionsContext
	}

	WithXmlAction generateWithXmlActionPromotions(PromotionsContext context, String promotionName) {
		// Closure to be run later, in this context we're given the root node with the WithXmlAction magic
		Closure withXmlClosure = generateWithXmlClosurePromotions(context, promotionName)
		//withXmlClosure.resolveStrategy = Closure.DELEGATE_FIRST

		return new WithXmlAction(withXmlClosure)
	}

}

