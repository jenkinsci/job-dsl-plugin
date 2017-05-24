package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class ListViewSectionContext extends SectionContext {
    ColumnsContext columnsContext = new ColumnsContext(jobManagement)

    ListViewSectionContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds columns to the views. The view will have no columns by default.
     */
    void columns(@DslContext(ColumnsContext) Closure columnsClosure) {
        executeInContext(columnsClosure, columnsContext)
    }
}
