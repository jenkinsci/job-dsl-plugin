package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.View

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class SectionedView extends View {
    SectionedView(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Creates a section containing a list of jobs.
     */
    void sections(@DslContext(SectionsContext) Closure sectionsClosure) {
        SectionsContext context = new SectionsContext(jobManagement)
        executeInContext(sectionsClosure, context)

        configure {
            for (Node sectionNode : context.sectionNodes) {
                it / 'sections' << sectionNode
            }
        }
    }
}
