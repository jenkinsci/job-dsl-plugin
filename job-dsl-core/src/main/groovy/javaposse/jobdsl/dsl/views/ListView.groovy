package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.View

import static java.lang.String.CASE_INSENSITIVE_ORDER
import static javaposse.jobdsl.dsl.ContextHelper.executeInContext
import static javaposse.jobdsl.dsl.Preconditions.checkNotNull

class ListView extends View {
    private final JobsContext jobsContext = new JobsContext()

    ListView(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Filter the job list by enabled/disabled status. Defaults to {@code StatusFilter.ALL}.
     */
    void statusFilter(StatusFilter filter) {
        checkNotNull(filter, 'filter must not be null')

        configure {
            if (filter == StatusFilter.ALL) {
                it.children().removeAll { it instanceof Node && it.name() == 'statusFilter' }
            } else {
                it / methodMissing('statusFilter', filter == StatusFilter.ENABLED)
            }
        }
    }

    /**
     * Adds jobs to the view.
     */
    void jobs(@DslContext(JobsContext) Closure jobsClosure) {
        executeInContext(jobsClosure, jobsContext)

        List<String> jobs = jobsContext.jobNames.sort(true, CASE_INSENSITIVE_ORDER) // see GROOVY-6900
        String regex = jobsContext.regex

        configure {
            it / 'jobNames' {
                comparator(class: 'hudson.util.CaseInsensitiveComparator')
                for (String job : jobs) {
                    string(job)
                }
            }
            if (regex) {
                it / includeRegex(regex)
            }
        }
    }

    /**
     * Adds columns to the views. The view will have no columns by default.
     */
    void columns(@DslContext(ColumnsContext) Closure columnsClosure) {
        ColumnsContext context = new ColumnsContext(jobManagement)
        executeInContext(columnsClosure, context)

        configure {
            for (Node columnNode : context.columnNodes) {
                it / 'columns' << columnNode
            }
        }
    }

    /**
     * Adds or removes jobs from the view by specifying filters.
     *
     * @since 1.29
     */
    void jobFilters(@DslContext(JobFiltersContext) Closure jobFiltersClosure) {
        JobFiltersContext context = new JobFiltersContext(jobManagement)
        executeInContext(jobFiltersClosure, context)

        configure {
            context.filterNodes.each { filterNode ->
                it / 'jobFilters' << filterNode
            }
        }
    }

    /**
     * If set to {@code true}, jobs from sub-folders will be shown.
     * Defaults to {@code false}.
     *
     * @since 1.31
     */
    void recurse(boolean shouldRecurse = true) {
        configure {
            it / 'recurse' << shouldRecurse
        }
    }

    static enum StatusFilter {
        ALL, ENABLED, DISABLED
    }
}
