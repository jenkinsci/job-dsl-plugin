package javaposse.jobdsl.plugin.actions

import hudson.model.Action
import hudson.model.Job
import hudson.model.Run
import javaposse.jobdsl.dsl.GeneratedConfigFile
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Shared
import spock.lang.Specification

class GeneratedConfigFilesBuildActionSpec extends Specification {
    @Shared
    @ClassRule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def 'interface methods'() {
        setup:
        GeneratedConfigFile one = new GeneratedConfigFile('foo', 'one')
        GeneratedConfigFile two = new GeneratedConfigFile('bar', 'two')

        when:
        GeneratedConfigFilesBuildAction action = new GeneratedConfigFilesBuildAction([one, two])

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
        GeneratedConfigFilesBuildAction action = new GeneratedConfigFilesBuildAction([])
        action.onLoad(run)

        when:
        Collection<? extends Action> actions = action.projectActions

        then:
        actions != null
        actions.size() == 1
        actions.first() instanceof GeneratedConfigFilesAction
        GeneratedConfigFilesAction generatedConfigFilesAction = actions.first() as GeneratedConfigFilesAction
        generatedConfigFilesAction.job == job
    }

    def 'deserialize'() {
        setup:
        String xml = '''<javaposse.jobdsl.plugin.actions.GeneratedConfigFilesBuildAction>
    <modifiedObjects class="linked-hash-set">
        <javaposse.jobdsl.dsl.GeneratedConfigFile>
            <id>foo</id>
            <name>one</name>
        </javaposse.jobdsl.dsl.GeneratedConfigFile>
        <javaposse.jobdsl.dsl.GeneratedConfigFile>
            <id>bar</id>
            <name>two</name>
        </javaposse.jobdsl.dsl.GeneratedConfigFile>
    </modifiedObjects>
</javaposse.jobdsl.plugin.actions.GeneratedConfigFilesBuildAction>'''

        when:
        GeneratedConfigFilesBuildAction result = Run.XSTREAM2.fromXML(xml) as GeneratedConfigFilesBuildAction

        then:
        result != null
        result.modifiedObjects != null
        result.modifiedObjects.size() == 2
        result.modifiedObjects.contains(new GeneratedConfigFile('foo', 'one'))
        result.modifiedObjects.contains(new GeneratedConfigFile('bar', 'two'))
    }

    def 'deserialize with modifiedConfigFiles'() {
        setup:
        String xml = '''<javaposse.jobdsl.plugin.actions.GeneratedConfigFilesBuildAction>
    <modifiedConfigFiles class="linked-hash-set">
        <javaposse.jobdsl.dsl.GeneratedConfigFile>
            <id>foo</id>
            <name>one</name>
        </javaposse.jobdsl.dsl.GeneratedConfigFile>
        <javaposse.jobdsl.dsl.GeneratedConfigFile>
            <id>bar</id>
            <name>two</name>
        </javaposse.jobdsl.dsl.GeneratedConfigFile>
    </modifiedConfigFiles>
</javaposse.jobdsl.plugin.actions.GeneratedConfigFilesBuildAction>'''

        when:
        GeneratedConfigFilesBuildAction result = Run.XSTREAM2.fromXML(xml) as GeneratedConfigFilesBuildAction

        then:
        result != null
        result.modifiedObjects != null
        result.modifiedObjects.size() == 2
        result.modifiedObjects.contains(new GeneratedConfigFile('foo', 'one'))
        result.modifiedObjects.contains(new GeneratedConfigFile('bar', 'two'))
    }
}
