package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.views.BuildPipelineView
import javaposse.jobdsl.dsl.views.ListView
import spock.lang.Specification

class JobParentSpec extends Specification {
    JobParent parent = Spy(JobParent)

    def 'default view type'() {
        when:
        View view = parent.view {
            name 'test'
        }

        then:
        view.name == 'test'
        view instanceof ListView
        parent.referencedViews.contains(view)
    }

    def 'list view'() {
        when:
        View view = parent.view(type: ViewType.ListView) {
            name 'test'
        }

        then:
        view.name == 'test'
        view instanceof ListView
        parent.referencedViews.contains(view)
    }

    def 'build pipeline view'() {
        when:
        View view = parent.view(type: ViewType.BuildPipelineView) {
            name 'test'
        }

        then:
        view.name == 'test'
        view instanceof BuildPipelineView
        parent.referencedViews.contains(view)
    }
}
