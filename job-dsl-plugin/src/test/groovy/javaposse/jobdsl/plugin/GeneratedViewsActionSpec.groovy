package javaposse.jobdsl.plugin

import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import javaposse.jobdsl.dsl.GeneratedView
import spock.lang.Specification

import static hudson.util.RunList.fromRuns

class GeneratedViewsActionSpec extends Specification {
    GeneratedViewsBuildAction buildAction = Mock(GeneratedViewsBuildAction)
    AbstractBuild build1 = Mock(AbstractBuild)
    AbstractBuild build2 = Mock(AbstractBuild)
    AbstractProject project = Mock(AbstractProject)
    Collection<GeneratedView> modifiedViews = [Mock(GeneratedView), Mock(GeneratedView)]

    def 'interface methods'() {
        when:
        GeneratedViewsAction action = new GeneratedViewsAction(project)

        then:
        action.iconFileName == null
        action.displayName == null
        action.urlName == "generatedViews"
    }

    def 'findLastGeneratedViews no build'() {
        when:
        Set<GeneratedView> views = new GeneratedViewsAction(project).findLastGeneratedViews()

        then:
        views.empty
    }

    def 'findLastGeneratedViews no build action'() {
        setup:
        build1.getAction(GeneratedViewsBuildAction) >> null
        project.getLastBuild() >> build1

        when:
        Set<GeneratedView> views = new GeneratedViewsAction(project).findLastGeneratedViews()

        then:
        views.empty
    }

    def 'findLastGeneratedViews from last build'() {
        setup:
        buildAction.getModifiedViews() >> modifiedViews
        build1.getAction(GeneratedViewsBuildAction) >> buildAction
        project.getLastBuild() >> build1

        when:
        Set<GeneratedView> views = new GeneratedViewsAction(project).findLastGeneratedViews()

        then:
        views.size() == modifiedViews.size()
        views.containsAll(modifiedViews)
    }

    def 'findLastGeneratedViews from last but one build'() {
        setup:
        buildAction.getModifiedViews() >> modifiedViews
        build1.getAction(GeneratedViewsBuildAction) >> buildAction
        build2.getAction(GeneratedViewsBuildAction) >> null
        build2.getPreviousBuild() >> build1
        project.getLastBuild() >> build2

        when:
        Set<GeneratedView> views = new GeneratedViewsAction(project).findLastGeneratedViews()

        then:
        views.size() == modifiedViews.size()
        views.containsAll(modifiedViews)
    }

    def 'findAllGeneratedViews no builds'() {
        setup:
        project.getBuilds() >> fromRuns([])

        when:
        Set<GeneratedView> views = new GeneratedViewsAction(project).findAllGeneratedViews()

        then:
        views != null
        views.isEmpty()
    }

    def 'findAllGeneratedViews no build action'() {
        setup:
        build1.getAction(GeneratedViewsBuildAction) >> null
        project.getBuilds() >> fromRuns([build1])

        when:
        Set<GeneratedView> views = new GeneratedViewsAction(project).findAllGeneratedViews()

        then:
        views != null
        views.isEmpty()
    }

    def 'findAllGeneratedViews'() {
        setup:
        buildAction.getModifiedViews() >> modifiedViews
        build1.getAction(GeneratedViewsBuildAction) >> buildAction
        build2.getAction(GeneratedViewsBuildAction) >> buildAction
        project.getBuilds() >> fromRuns([build1, build2])

        when:
        Set<GeneratedView> views = new GeneratedViewsAction(project).findAllGeneratedViews()

        then:
        views != null
        views.size() == modifiedViews.size()
        views.containsAll(modifiedViews)
    }
}
