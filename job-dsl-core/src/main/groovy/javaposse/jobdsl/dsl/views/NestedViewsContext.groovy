package javaposse.jobdsl.dsl.views

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.View
import javaposse.jobdsl.dsl.ViewFactory
import javaposse.jobdsl.dsl.ViewType

class NestedViewsContext implements Context, ViewFactory {
    private final JobManagement jobManagement

    List<View> views = []

    NestedViewsContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
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

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), 'name must be specified')

        ViewType viewType = arguments['type'] as ViewType ?: ViewType.ListView
        View view = viewType.viewClass.newInstance(jobManagement)
        view.name = name
        view.with(closure)
        views << view
    }

    @Override
    ListView listView(String name, @DslContext(ListView) Closure closure = null) {
        processView(name, ListView, closure)
    }

    @Override
    SectionedView sectionedView(String name, @DslContext(SectionedView) Closure closure = null) {
        processView(name, SectionedView, closure)
    }

    @Override
    NestedView nestedView(String name, @DslContext(NestedView) Closure closure = null) {
        processView(name, NestedView, closure)
    }

    @Override
    DeliveryPipelineView deliveryPipelineView(String name, @DslContext(DeliveryPipelineView) Closure closure = null) {
        processView(name, DeliveryPipelineView, closure)
    }

    @Override
    BuildPipelineView buildPipelineView(String name, @DslContext(BuildPipelineView) Closure closure = null) {
        processView(name, BuildPipelineView, closure)
    }

    @Override
    BuildMonitorView buildMonitorView(String name, @DslContext(BuildMonitorView) Closure closure = null) {
        processView(name, BuildMonitorView, closure)
    }

    @Override
    CategorizedJobsView categorizedJobsView(String name, @DslContext(CategorizedJobsView) Closure closure = null) {
        processView(name, CategorizedJobsView, closure)
    }

    private <T extends View> T processView(String name, Class<T> viewClass, Closure closure) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), 'name must be specified')

        T view = viewClass.newInstance(jobManagement)
        view.name = name
        if (closure) {
            view.with(closure)
        }
        views << view
        view
    }
}
