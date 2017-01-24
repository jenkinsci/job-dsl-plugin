package javaposse.jobdsl.plugin.actions

import hudson.model.Action
import hudson.model.Project
import javaposse.jobdsl.plugin.ExecuteDslScripts
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class ApiViewerActionFactorySpec extends Specification {
    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def 'type is Project'() {
        expect:
        new ApiViewerActionFactory().type() == Project
    }

    def 'creates no action when no ExecuteDslScripts build step is configures'() {
        setup:
        Project project = jenkinsRule.createFreeStyleProject()

        expect:
        new ApiViewerActionFactory().createFor(project) == []
    }

    def 'creates action when ExecuteDslScripts build step is configures'() {
        setup:
        Project project = jenkinsRule.createFreeStyleProject()
        project.buildersList.add(new ExecuteDslScripts())

        when:
        Collection<? extends Action> actions = new ApiViewerActionFactory().createFor(project)

        then:
        actions.size() == 1
        actions.first() instanceof ApiViewerAction
    }
}
