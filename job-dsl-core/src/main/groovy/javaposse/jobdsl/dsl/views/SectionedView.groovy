package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.View

import static javaposse.jobdsl.dsl.helpers.AbstractContextHelper.executeInContext

class SectionedView extends View {
    @Override
    protected String getTemplate() {
        '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.sectioned__view.SectionedView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <sections/>
</hudson.plugins.sectioned__view.SectionedView>'''
    }

    void sections(Closure sectionsClosure) {
        SectionsContext context = new SectionsContext()
        executeInContext(sectionsClosure, context)

        execute {
            for (Node sectionNode : context.sectionNodes) {
                it / 'sections' << sectionNode
            }
        }
    }
}
