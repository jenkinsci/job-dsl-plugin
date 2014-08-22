package javaposse.jobdsl.dsl

enum ViewType {
    ListView(javaposse.jobdsl.dsl.views.ListView),
    SectionedView(javaposse.jobdsl.dsl.views.SectionedView),
    NestedView(javaposse.jobdsl.dsl.views.NestedView),
    BuildPipelineView(javaposse.jobdsl.dsl.views.BuildPipelineView)

    final Class<? extends View> viewClass

    ViewType(Class<? extends View> viewClass) {
        this.viewClass = viewClass
    }
}
