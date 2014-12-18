package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.View
import javaposse.jobdsl.dsl.ViewType

class NestedViewsContext implements Context {
    List<View> views = []

    void view(Map<String, Object> arguments = [:], Closure closure) {
        ViewType viewType = arguments['type'] as ViewType ?: ViewType.ListView
        View view = viewType.viewClass.newInstance()
        view.with(closure)
        views << view
    }
}
