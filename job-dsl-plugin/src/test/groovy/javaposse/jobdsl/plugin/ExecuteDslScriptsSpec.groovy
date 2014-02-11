package javaposse.jobdsl.plugin

import com.google.common.collect.Lists
import hudson.model.AbstractProject
import hudson.model.Action
import spock.lang.Specification

class ExecuteDslScriptsSpec extends Specification {
    ExecuteDslScripts executeDslScripts = new ExecuteDslScripts()
    AbstractProject project = Mock(AbstractProject)

    def 'getProjectActions'() {
        when:
        List<? extends Action> actions = Lists.newArrayList(executeDslScripts.getProjectActions(project))

        then:
        actions != null
        actions.size() == 2
        actions[0] instanceof GeneratedJobsAction
        actions[1] instanceof GeneratedViewsAction
    }
}
