package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.View

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class NestedView extends View {
    NestedView(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Adds nested views.
     */
    void views(@DslContext(NestedViewsContext) Closure viewsClosure) {
        NestedViewsContext context = new NestedViewsContext(jobManagement)
        executeInContext(viewsClosure, context)

        configure {
            for (View view : context.views) {
                Node viewNode = view.node
                viewNode.appendNode('name', view.name)
                viewNode.appendNode('owner', [class: 'hudson.plugins.nested_view.NestedView', reference: '../../..'])
                it / 'views' << viewNode
            }
        }
    }

    /**
     * Adds columns to the views. The view will have no columns by default.
     */
    void columns(@DslContext(NestedViewColumnsContext) Closure columnsClosure) {
        NestedViewColumnsContext context = new NestedViewColumnsContext()
        executeInContext(columnsClosure, context)

        configure {
            for (Node columnNode : context.columnNodes) {
                it / 'columns' / 'columns' << columnNode
            }
        }
    }
}
