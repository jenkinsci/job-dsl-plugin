package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.views.BuildMonitorView
import javaposse.jobdsl.dsl.views.BuildPipelineView
import javaposse.jobdsl.dsl.views.CategorizedJobsView
import javaposse.jobdsl.dsl.views.DeliveryPipelineView
import javaposse.jobdsl.dsl.views.ListView
import javaposse.jobdsl.dsl.views.NestedView
import javaposse.jobdsl.dsl.views.SectionedView

/**
 * Creates or updates views.
 *
 * @since 1.31
 */
interface ViewFactory {
    @Deprecated
    View view(@DslContext(View) Closure closure)

    @Deprecated
    View view(Map<String, Object> arguments, @DslContext(View) Closure closure)

    /**
     * Creates or updates a view that shows items in a simple list format.
     *
     * @see #listView(java.lang.String, groovy.lang.Closure)
     */
    ListView listView(String name)

    /**
     * Creates or updates a view that shows items in a simple list format.
     */
    ListView listView(String name, @DslContext(ListView) Closure closure)

    /**
     * Creates or updates a view that can be divided into sections.
     *
     * @see #sectionedView(java.lang.String)
     */
    SectionedView sectionedView(String name)

    /**
     * Creates or updates a view that can be divided into sections.
     */
    SectionedView sectionedView(String name, @DslContext(SectionedView) Closure closure)

    /**
     * Creates or updates a view that allows grouping views into multiple levels.
     *
     * @see #nestedView(java.lang.String, groovy.lang.Closure)
     */
    NestedView nestedView(String name)

    /**
     * Creates or updates a view that allows grouping views into multiple levels.
     */
    NestedView nestedView(String name, @DslContext(NestedView) Closure closure)

    /**
     * Creates or updates a view that renders pipelines based on upstream/downstream jobs.
     *
     * @see #deliveryPipelineView(java.lang.String, groovy.lang.Closure)
     */
    DeliveryPipelineView deliveryPipelineView(String name)

    /**
     * Creates or updates a view that renders pipelines based on upstream/downstream jobs.
     */
    DeliveryPipelineView deliveryPipelineView(String name, @DslContext(DeliveryPipelineView) Closure closure)

    /**
     * Create or updates a view of upstream and downstream connected jobs.
     *
     * @see #buildPipelineView(java.lang.String, groovy.lang.Closure)
     */
    BuildPipelineView buildPipelineView(String name)

    /**
     * Create or updates a view of upstream and downstream connected jobs.
     */
    BuildPipelineView buildPipelineView(String name, @DslContext(BuildPipelineView) Closure closure)

    /**
     * Creates or updates a view that provides a highly visible view of the status of selected Jenkins jobs.
     *
     * @see #buildMonitorView(java.lang.String, groovy.lang.Closure)
     */
    BuildMonitorView buildMonitorView(String name)

    /**
     * Creates or updates a view that provides a highly visible view of the status of selected Jenkins jobs.
     */
    BuildMonitorView buildMonitorView(String name, @DslContext(BuildMonitorView) Closure closure)

    /**
     * Creates or updates a new view that is very similar to the standard Jenkins List Views, but where you can group
     * jobs and categorize them according to regular expressions.
     *
     * @see #categorizedJobsView(java.lang.String, groovy.lang.Closure)
     */
    CategorizedJobsView categorizedJobsView(String name)

    /**
     * Creates or updates a new view that is very similar to the standard Jenkins List Views, but where you can group
     * jobs and categorize them according to regular expressions.
     */
    CategorizedJobsView categorizedJobsView(String name, @DslContext(CategorizedJobsView) Closure closure)
}
