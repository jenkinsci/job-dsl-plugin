package javaposse.jobdsl.plugin.actions

import hudson.model.Action
import hudson.model.Job
import hudson.model.Run
import javaposse.jobdsl.dsl.GeneratedUserContent
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Shared
import spock.lang.Specification

class GeneratedUserContentsBuildActionSpec extends Specification {
    @Shared
    @ClassRule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def 'interface methods'() {
        setup:
        GeneratedUserContent one = new GeneratedUserContent('one')
        GeneratedUserContent two = new GeneratedUserContent('two')

        when:
        GeneratedUserContentsBuildAction action = new GeneratedUserContentsBuildAction([one, two])

        then:
        action.iconFileName == null
        action.displayName == null
        action.urlName == null
        action.modifiedObjects != null
        action.modifiedObjects.size() == 2
        action.modifiedObjects.contains(one)
        action.modifiedObjects.contains(two)
    }

    def 'project actions'() {
        setup:
        Job job = Mock(Job)
        Run run = Mock(Run)
        run.parent >> job
        GeneratedUserContentsBuildAction action = new GeneratedUserContentsBuildAction([])
        action.onLoad(run)

        when:
        Collection<? extends Action> actions = action.projectActions

        then:
        actions != null
        actions.size() == 1
        actions.first() instanceof GeneratedUserContentsAction
        GeneratedUserContentsAction generatedUserContentsAction = actions.first() as GeneratedUserContentsAction
        generatedUserContentsAction.job == job
    }

    def 'deserialize'() {
        setup:
        String xml = '''<javaposse.jobdsl.plugin.actions.GeneratedUserContentsBuildAction>
    <modifiedObjects class="linked-hash-set">
        <javaposse.jobdsl.dsl.GeneratedUserContent>
            <path>one</path>
        </javaposse.jobdsl.dsl.GeneratedUserContent>
        <javaposse.jobdsl.dsl.GeneratedUserContent>
            <path>two</path>
        </javaposse.jobdsl.dsl.GeneratedUserContent>
    </modifiedObjects>
</javaposse.jobdsl.plugin.actions.GeneratedUserContentsBuildAction>'''

        when:
        GeneratedUserContentsBuildAction result = Run.XSTREAM2.fromXML(xml) as GeneratedUserContentsBuildAction

        then:
        result != null
        result.modifiedObjects != null
        result.modifiedObjects.size() == 2
        result.modifiedObjects.contains(new GeneratedUserContent('one'))
        result.modifiedObjects.contains(new GeneratedUserContent('two'))
    }
}
