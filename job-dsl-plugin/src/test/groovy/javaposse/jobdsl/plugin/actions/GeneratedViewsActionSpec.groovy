package javaposse.jobdsl.plugin.actions

import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.ItemGroup
import hudson.model.View
import hudson.model.ViewGroup
import javaposse.jobdsl.dsl.GeneratedView
import spock.lang.Specification

import static hudson.util.RunList.fromRuns

class GeneratedViewsActionSpec extends Specification {
    private final GeneratedViewsBuildAction buildAction = Mock(GeneratedViewsBuildAction)
    private final AbstractBuild build1 = Mock(AbstractBuild)
    private final AbstractBuild build2 = Mock(AbstractBuild)
    private final AbstractProject project = Mock(AbstractProject)
    private final Collection<GeneratedView> modifiedViews = [Mock(GeneratedView), Mock(GeneratedView)]
    private final List<View> views = [Mock(View), Mock(View)]

    def 'interface methods'() {
        when:
        GeneratedViewsAction action = new GeneratedViewsAction(project)

        then:
        action.iconFileName == null
        action.displayName == null
        action.urlName == null
    }

    def 'findLastGeneratedObjects no build'() {
        when:
        Set<GeneratedView> views = new GeneratedViewsAction(project).findLastGeneratedObjects()

        then:
        views.empty
    }

    def 'findLastGeneratedObjects no build action'() {
        setup:
        build1.getAction(GeneratedViewsBuildAction) >> null
        project.lastBuild >> build1

        when:
        Set<GeneratedView> views = new GeneratedViewsAction(project).findLastGeneratedObjects()

        then:
        views.empty
    }

    def 'findLastGeneratedObjects from last build'() {
        setup:
        buildAction.modifiedObjects >> modifiedViews
        build1.getAction(GeneratedViewsBuildAction) >> buildAction
        project.lastBuild >> build1

        when:
        Set<GeneratedView> views = new GeneratedViewsAction(project).findLastGeneratedObjects()

        then:
        views.size() == modifiedViews.size()
        views.containsAll(modifiedViews)
    }

    def 'findLastGeneratedObjects from last but one build'() {
        setup:
        buildAction.modifiedObjects >> modifiedViews
        build1.getAction(GeneratedViewsBuildAction) >> buildAction
        build2.getAction(GeneratedViewsBuildAction) >> null
        build2.previousBuild >> build1
        project.lastBuild >> build2

        when:
        Set<GeneratedView> views = new GeneratedViewsAction(project).findLastGeneratedObjects()

        then:
        views.size() == modifiedViews.size()
        views.containsAll(modifiedViews)
    }

    def 'getViews no builds'() {
        setup:
        project.builds >> fromRuns([])

        when:
        Iterable<View> views = new GeneratedViewsAction(project).views

        then:
        views != null
        views.size() == 0
    }

    def 'getViews no build action'() {
        setup:
        build1.getAction(GeneratedViewsBuildAction) >> null
        project.builds >> fromRuns([build1])

        when:
        Iterable<View> views = new GeneratedViewsAction(project).views

        then:
        views != null
        views.size() == 0
    }

    def 'getViews'() {
        setup:
        ItemGroup itemGroup = Mock(ItemGroup)
        itemGroup.fullDisplayName >> 'foo'
        ViewGroup owner = Mock(ViewGroup)
        owner.itemGroup >> itemGroup
        views[0].displayName >> 'one'
        views[0].owner >> owner
        views[1].displayName >> 'two'
        views[1].owner >> owner
        buildAction.views >> views
        build1.getAction(GeneratedViewsBuildAction) >> buildAction
        build2.getAction(GeneratedViewsBuildAction) >> buildAction
        project.builds >> fromRuns([build1, build2])

        when:
        Iterable<View> views = new GeneratedViewsAction(project).views

        then:
        views != null
        views.size() == this.views.size()
        views.containsAll(this.views)
    }
}
