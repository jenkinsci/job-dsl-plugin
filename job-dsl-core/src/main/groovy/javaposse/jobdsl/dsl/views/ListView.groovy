package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.View

import static com.google.common.base.Preconditions.checkNotNull
import static java.lang.String.CASE_INSENSITIVE_ORDER
import static javaposse.jobdsl.dsl.helpers.AbstractContextHelper.executeInContext

class ListView extends View {
    private Set<String> jobNames = []

    void statusFilter(StatusFilter filter) {
        checkNotNull(filter, "filter must not be null")

        execute {
            if (filter == StatusFilter.ALL) {
                it.children().removeAll { it instanceof Node && it.name() == 'statusFilter' }
            } else {
                it / methodMissing('statusFilter', filter == StatusFilter.ENABLED)
            }
        }
    }

    void jobs(Closure jobsClosure) {
        JobsContext context = new JobsContext()
        executeInContext(jobsClosure, context)

        this.jobNames.addAll(context.jobNames)

        execute {
            it / 'jobNames' {
                comparator(class: 'hudson.util.CaseInsensitiveComparator')
                for (String job : this.jobNames.sort(CASE_INSENSITIVE_ORDER)) {
                    string(job)
                }
            }
            if (context.regex) {
                it / includeRegex(context.regex)
            }
        }
    }

    void columns(Closure columnsClosure) {
        ColumnsContext context = new ColumnsContext()
        executeInContext(columnsClosure, context)

        execute {
            for (Node columnNode : context.columnNodes) {
                it / 'columns' << columnNode
            }
        }
    }

    void jobFilters(Closure jobFiltersClosure) {
        JobFiltersContext context = new JobFiltersContext()
        executeInContext(jobFiltersClosure, context)

        execute {
            for (Node filterNode : context.filterNodes) {
                it / 'jobFilters' << filterNode
            }
        }
    }

    @Override
    protected String getTemplate() {
        return '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.model.ListView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <jobNames class="tree-set">
        <comparator class="hudson.util.CaseInsensitiveComparator"/>
    </jobNames>
    <jobFilters/>
    <columns/>
</hudson.model.ListView>'''
    }

    static enum StatusFilter {
        ALL, ENABLED, DISABLED
    }
}
