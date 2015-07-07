package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.View
import javaposse.jobdsl.dsl.ViewFactory
import javaposse.jobdsl.dsl.ViewType

class NestedViewsContext extends AbstractContext implements ViewFactory {
    List<View> views = []

    NestedViewsContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    @Deprecated
    View view(Map<String, Object> arguments = [:], @DslContext(View) Closure closure) {
        jobManagement.logDeprecationWarning()

        ViewType viewType = arguments['type'] as ViewType ?: ViewType.ListView
        View view = viewType.viewClass.newInstance(jobManagement)
        view.with(closure)
        views << view
        view
    }

    @Deprecated
    void view(Map<String, Object> arguments = [:], String name, @DslContext(View) Closure closure) {
        jobManagement.logDeprecationWarning()

        Preconditions.checkNotNullOrEmpty(name, 'name must be specified')

        ViewType viewType = arguments['type'] as ViewType ?: ViewType.ListView
        View view = viewType.viewClass.newInstance(jobManagement)
        view.name = name
        view.with(closure)
        views << view
    }

    /**
     * @since 1.31
     */
    @Override
    ListView listView(String name, @DslContext(ListView) Closure closure = null) {
        processView(name, ListView, closure)
    }

    /**
     * @since 1.31
     */
    @Override
    @RequiresPlugin(id = 'sectioned-view')
    SectionedView sectionedView(String name, @DslContext(SectionedView) Closure closure = null) {
        processView(name, SectionedView, closure)
    }

    /**
     * @since 1.31
     */
    @Override
    NestedView nestedView(String name, @DslContext(NestedView) Closure closure = null) {
        processView(name, NestedView, closure)
    }

    /**
     * @since 1.31
     */
    @Override
    @RequiresPlugin(id = 'delivery-pipeline-plugin')
    DeliveryPipelineView deliveryPipelineView(String name, @DslContext(DeliveryPipelineView) Closure closure = null) {
        processView(name, DeliveryPipelineView, closure)
    }

    /**
     * @since 1.31
     */
    @Override
    @RequiresPlugin(id = 'build-pipeline-plugin')
    BuildPipelineView buildPipelineView(String name, @DslContext(BuildPipelineView) Closure closure = null) {
        processView(name, BuildPipelineView, closure)
    }

    /**
     * @since 1.31
     */
    @Override
    @RequiresPlugin(id = 'build-monitor-plugin')
    BuildMonitorView buildMonitorView(String name, @DslContext(BuildMonitorView) Closure closure = null) {
        processView(name, BuildMonitorView, closure)
    }

    /**
     * @since 1.31
     */
    @Override
    @RequiresPlugin(id = 'categorized-view', minimumVersion = '1.8')
    CategorizedJobsView categorizedJobsView(String name, @DslContext(CategorizedJobsView) Closure closure = null) {
        processView(name, CategorizedJobsView, closure)
    }

    private <T extends View> T processView(String name, Class<T> viewClass, Closure closure) {
        Preconditions.checkNotNullOrEmpty(name, 'name must be specified')

        T view = viewClass.newInstance(jobManagement)
        view.name = name
        if (closure) {
            view.with(closure)
        }
        views << view
        view
    }
}
