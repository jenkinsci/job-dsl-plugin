package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.views.BuildMonitorView
import javaposse.jobdsl.dsl.views.BuildPipelineView
import javaposse.jobdsl.dsl.views.CategorizedJobsView
import javaposse.jobdsl.dsl.views.DashboardView
import javaposse.jobdsl.dsl.views.DeliveryPipelineView
import javaposse.jobdsl.dsl.views.ListView
import javaposse.jobdsl.dsl.views.NestedView
import javaposse.jobdsl.dsl.views.SectionedView

/**
 * Creates or updates views.
 *
 * @since 1.31
 */
interface ViewFactory extends Context {
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
    @RequiresPlugin(id = 'sectioned-view', failIfMissing = true)
    SectionedView sectionedView(String name)

    /**
     * Creates or updates a view that can be divided into sections.
     */
    @RequiresPlugin(id = 'sectioned-view', failIfMissing = true)
    SectionedView sectionedView(String name, @DslContext(SectionedView) Closure closure)

    /**
     * Creates or updates a view that allows grouping views into multiple levels.
     *
     * @see #nestedView(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'nested-view', failIfMissing = true)
    NestedView nestedView(String name)

    /**
     * Creates or updates a view that allows grouping views into multiple levels.
     */
    @RequiresPlugin(id = 'nested-view', failIfMissing = true)
    NestedView nestedView(String name, @DslContext(NestedView) Closure closure)

    /**
     * Creates or updates a view that renders pipelines based on upstream/downstream jobs.
     *
     * @see #deliveryPipelineView(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'delivery-pipeline-plugin', failIfMissing = true)
    DeliveryPipelineView deliveryPipelineView(String name)

    /**
     * Creates or updates a view that renders pipelines based on upstream/downstream jobs.
     */
    @RequiresPlugin(id = 'delivery-pipeline-plugin', failIfMissing = true)
    DeliveryPipelineView deliveryPipelineView(String name, @DslContext(DeliveryPipelineView) Closure closure)

    /**
     * Create or updates a view of upstream and downstream connected jobs.
     *
     * @see #buildPipelineView(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'build-pipeline-plugin', failIfMissing = true)
    BuildPipelineView buildPipelineView(String name)

    /**
     * Create or updates a view of upstream and downstream connected jobs.
     */
    @RequiresPlugin(id = 'build-pipeline-plugin', failIfMissing = true)
    BuildPipelineView buildPipelineView(String name, @DslContext(BuildPipelineView) Closure closure)

    /**
     * Creates or updates a view that provides a highly visible view of the status of selected Jenkins jobs.
     *
     * @see #buildMonitorView(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'build-monitor-plugin', failIfMissing = true)
    BuildMonitorView buildMonitorView(String name)

    /**
     * Creates or updates a view that provides a highly visible view of the status of selected Jenkins jobs.
     */
    @RequiresPlugin(id = 'build-monitor-plugin', failIfMissing = true)
    BuildMonitorView buildMonitorView(String name, @DslContext(BuildMonitorView) Closure closure)

    /**
     * Creates or updates a new view that is very similar to the standard Jenkins List Views, but where you can group
     * jobs and categorize them according to regular expressions.
     *
     * @see #categorizedJobsView(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'categorized-view', minimumVersion = '1.8', failIfMissing = true)
    CategorizedJobsView categorizedJobsView(String name)

    /**
     * Creates or updates a new view that is very similar to the standard Jenkins List Views, but where you can group
     * jobs and categorize them according to regular expressions.
     */
    @RequiresPlugin(id = 'categorized-view', minimumVersion = '1.8', failIfMissing = true)
    CategorizedJobsView categorizedJobsView(String name, @DslContext(CategorizedJobsView) Closure closure)

    /**
     * Creates or updates a dashboard / portal-like view.
     *
     * @see #dashboardView(java.lang.String, groovy.lang.Closure)
     * @since 1.42
     */
    @RequiresPlugin(id = 'dashboard-view', minimumVersion = '2.9.7', failIfMissing = true)
    DashboardView dashboardView(String name)

    /**
     * Creates or updates a dashboard / portal-like view.

     * @since 1.42
     */
    @RequiresPlugin(id = 'dashboard-view', minimumVersion = '2.9.7', failIfMissing = true)
    DashboardView dashboardView(String name, @DslContext(DashboardView) Closure closure)
}
