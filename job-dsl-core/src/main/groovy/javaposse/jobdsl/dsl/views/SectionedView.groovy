package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.View

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class SectionedView extends View {
    SectionedView(JobManagement jobManagement) {
        super(jobManagement)
    }

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

    void sections(@DslContext(SectionsContext) Closure sectionsClosure) {
        SectionsContext context = new SectionsContext()
        executeInContext(sectionsClosure, context)

        execute {
            for (Node sectionNode : context.sectionNodes) {
                it / 'sections' << sectionNode
            }
        }
    }
}
