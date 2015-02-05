package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslContext

import static com.google.common.base.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class ListViewSectionContext implements Context {
    private static final List<String> VALID_WIDTHS = ['FULL', 'HALF', 'THIRD', 'TWO_THIRDS']
    private static final List<String> VALID_ALIGNMENTS = ['CENTER', 'LEFT', 'RIGHT']

    String name
    String width = 'FULL'
    String alignment = 'CENTER'
    JobsContext jobsContext = new JobsContext()
    JobFiltersContext jobFiltersContext = new JobFiltersContext()
    ColumnsContext columnsContext = new ColumnsContext()

    void name(String name) {
        this.name = name
    }

    void width(String width) {
        checkArgument(VALID_WIDTHS.contains(width), "width must be one of ${VALID_WIDTHS.join(', ')}")
        this.width = width
    }

    void alignment(String alignment) {
        checkArgument(VALID_ALIGNMENTS.contains(alignment), "alignment must be one of ${VALID_ALIGNMENTS.join(', ')}")
        this.alignment = alignment
    }

    void jobs(@DslContext(JobsContext) Closure jobsClosure) {
        executeInContext(jobsClosure, jobsContext)
    }

    void jobFilters(@DslContext(JobFiltersContext) Closure jobFiltersClosure) {
        executeInContext(jobFiltersClosure, jobFiltersContext)
    }

    void columns(@DslContext(ColumnsContext) Closure columnsClosure) {
        executeInContext(columnsClosure, columnsContext)
    }
}
