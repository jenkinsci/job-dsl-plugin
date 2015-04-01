package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.views.BuildMonitorView
import javaposse.jobdsl.dsl.views.BuildPipelineView
import javaposse.jobdsl.dsl.views.CategorizedJobsView
import javaposse.jobdsl.dsl.views.DeliveryPipelineView
import javaposse.jobdsl.dsl.views.ListView
import javaposse.jobdsl.dsl.views.NestedView
import javaposse.jobdsl.dsl.views.SectionedView

interface ViewFactory {
    @Deprecated
    View view(@DslContext(View) Closure closure)

    @Deprecated
    View view(Map<String, Object> arguments, @DslContext(View) Closure closure)

    ListView listView(String name)

    ListView listView(String name, @DslContext(ListView) Closure closure)

    SectionedView sectionedView(String name)

    SectionedView sectionedView(String name, @DslContext(SectionedView) Closure closure)

    NestedView nestedView(String name)

    NestedView nestedView(String name, @DslContext(NestedView) Closure closure)

    DeliveryPipelineView deliveryPipelineView(String name)

    DeliveryPipelineView deliveryPipelineView(String name, @DslContext(DeliveryPipelineView) Closure closure)

    BuildPipelineView buildPipelineView(String name)

    BuildPipelineView buildPipelineView(String name, @DslContext(BuildPipelineView) Closure closure)

    BuildMonitorView buildMonitorView(String name)

    BuildMonitorView buildMonitorView(String name, @DslContext(BuildMonitorView) Closure closure)

    CategorizedJobsView categorizedJobsView(String name)

    CategorizedJobsView categorizedJobsView(String name, @DslContext(CategorizedJobsView) Closure closure)
}
