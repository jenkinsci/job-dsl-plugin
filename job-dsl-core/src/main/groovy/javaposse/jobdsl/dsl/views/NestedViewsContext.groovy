package javaposse.jobdsl.dsl.views

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.View
import javaposse.jobdsl.dsl.ViewType

class NestedViewsContext implements Context {
    private final JobManagement jobManagement

    List<View> views = []

    NestedViewsContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    @Deprecated
    void view(Map<String, Object> arguments = [:], @DslContext(View) Closure closure) {
        jobManagement.logDeprecationWarning()

        ViewType viewType = arguments['type'] as ViewType ?: ViewType.ListView
        View view = viewType.viewClass.newInstance(jobManagement)
        view.with(closure)
        views << view
    }

    void view(Map<String, Object> arguments = [:], String name, @DslContext(View) Closure closure) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), 'name must be specified')

        ViewType viewType = arguments['type'] as ViewType ?: ViewType.ListView
        View view = viewType.viewClass.newInstance(jobManagement)
        view.name = name
        view.with(closure)
        views << view
    }
}
