package javaposse.jobdsl.dsl.views

import groovy.lang.Closure;

import java.util.Map;

import javaposse.jobdsl.dsl.View
import javaposse.jobdsl.dsl.ViewType;
import static com.google.common.base.Preconditions.checkNotNull
import static java.lang.String.CASE_INSENSITIVE_ORDER
import static javaposse.jobdsl.dsl.helpers.AbstractContextHelper.executeInContext

class NestedView extends View {

    void columns(Closure columnsClosure) {
        ColumnsContext context = new ColumnsContext()
        executeInContext(columnsClosure, context)

        execute {
            for (Node columnNode : context.columnNodes) {
                it / 'columns' / 'columns' << columnNode
            }
        }
    }

    public View listView(String name,Closure closure){
        addView(new ListView(),name,closure)
    }

    public View nestedView(String name,Closure closure){
        addView(new NestedView(),name,closure)
    }

    public View pipelineView(String name,Closure closure){
        addView(new BuildPipelineView(),name,closure)
    }

    private View addView(View view,String name,Closure closure) {
        view.name(name)
        view.with(closure)
        execute{
            def n=view.getNode()
            (n / 'owner').attributes=['class':'hudson.plugins.nested_view.NestedView','reference':'../../..']
            it / 'views' << n
        }
    }


    @Override
    protected String getTemplate() {
        return '''<hudson.plugins.nested__view.NestedView plugin="nested-view@1.14" >
    <name>nest1</name>
    <properties class="hudson.model.View$PropertyList"/>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <views/>
    <columns/>
</hudson.plugins.nested__view.NestedView>
'''
    }
}
