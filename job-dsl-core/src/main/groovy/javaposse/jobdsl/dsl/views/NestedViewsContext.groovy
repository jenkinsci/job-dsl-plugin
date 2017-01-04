package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.View
import javaposse.jobdsl.dsl.ViewFactory

class NestedViewsContext extends AbstractContext implements ViewFactory {
    List<View> views = []

    NestedViewsContext(JobManagement jobManagement) {
        super(jobManagement)
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
    DeliveryPipelineView deliveryPipelineView(String name, @DslContext(DeliveryPipelineView) Closure closure = null) {
        jobManagement.logPluginDeprecationWarning('delivery-pipeline-plugin', '0.10.0')
        processView(name, DeliveryPipelineView, closure)
    }

    /**
     * @since 1.31
     */
    @Override
    BuildPipelineView buildPipelineView(String name, @DslContext(BuildPipelineView) Closure closure = null) {
        processView(name, BuildPipelineView, closure)
    }

    /**
     * @since 1.31
     */
    @Override
    BuildMonitorView buildMonitorView(String name, @DslContext(BuildMonitorView) Closure closure = null) {
        processView(name, BuildMonitorView, closure)
    }

    /**
     * @since 1.31
     */
    @Override
    CategorizedJobsView categorizedJobsView(String name, @DslContext(CategorizedJobsView) Closure closure = null) {
        processView(name, CategorizedJobsView, closure)
    }

    /**
     * @since 1.42
     */
    @Override
    DashboardView dashboardView(String name, @DslContext(DashboardView) Closure closure = null) {
        processView(name, DashboardView, closure)
    }

    private <T extends View> T processView(String name, Class<T> viewClass, Closure closure) {
        Preconditions.checkNotNullOrEmpty(name, 'name must be specified')

        T view = viewClass.newInstance(jobManagement, name)
        if (closure) {
            view.with(closure)
        }
        views << view
        view
    }
}
