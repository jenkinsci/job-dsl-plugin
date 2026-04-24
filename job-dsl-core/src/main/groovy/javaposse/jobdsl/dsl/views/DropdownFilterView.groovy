package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext
import static javaposse.jobdsl.dsl.Preconditions.checkNotNull

class DropdownFilterView extends ListView {
    DropdownFilterView(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Sets the filter bar position. Defaults to {@code 'top'}.
     *
     * <p>Valid values: {@code 'top'} (horizontal bar above job table) or
     * {@code 'sidebar'} (vertical panel on the right).
     */
    void filterPosition(String filterPosition) {
        checkNotNull(filterPosition, 'filterPosition must not be null')

        configure {
            it / methodMissing('filterPosition', filterPosition)
        }
    }

    /**
     * Configures dropdown filter definitions. Multiple dropdowns combine with AND logic.
     *
     * <p>Example:
     * <pre>
     * dropdowns {
     *     jobNameRegex('Project', 'projects/([^/]+)/.*')
     *     buildParameter('Environment', 'env')
     * }
     * </pre>
     */
    @RequiresPlugin(id = 'dynamic-view-filter')
    void dropdowns(@DslContext(DropdownFilterDropdownsContext) Closure dropdownsClosure) {
        DropdownFilterDropdownsContext context = new DropdownFilterDropdownsContext()
        executeInContext(dropdownsClosure, context)

        configure {
            for (Node dropdownNode : context.dropdownNodes) {
                it / 'dropdowns' << dropdownNode
            }
        }
    }
}
