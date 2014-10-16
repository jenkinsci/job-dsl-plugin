package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.View

import static javaposse.jobdsl.dsl.helpers.ContextHelper.executeInContext

class NestedView extends View {
    void views(Closure viewsClosure) {
        NestedViewsContext context = new NestedViewsContext()
        executeInContext(viewsClosure, context)

        execute {
            for (View view : context.views) {
                Node viewNode = view.node
                viewNode.appendNode('name', view.name)
                viewNode.appendNode('owner', [class: 'hudson.plugins.nested_view.NestedView', reference: '../../..'])
                it / 'views' << viewNode
            }
        }
    }

    void columns(Closure columnsClosure) {
        NestedViewColumnsContext context = new NestedViewColumnsContext()
        executeInContext(columnsClosure, context)

        execute {
            for (Node columnNode : context.columnNodes) {
                it / 'columns' / 'columns' << columnNode
            }
        }
    }

    @Override
    protected String getTemplate() {
        '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.nested__view.NestedView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <views/>
</hudson.plugins.nested__view.NestedView>'''
    }
}
