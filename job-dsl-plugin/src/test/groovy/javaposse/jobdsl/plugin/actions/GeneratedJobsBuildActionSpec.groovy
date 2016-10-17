package javaposse.jobdsl.plugin.actions

import hudson.model.Action
import hudson.model.Item
import hudson.model.Run
import hudson.model.Job
import javaposse.jobdsl.dsl.GeneratedJob
import javaposse.jobdsl.plugin.LookupStrategy
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Shared
import spock.lang.Specification
import hudson.model.FreeStyleProject

class GeneratedJobsBuildActionSpec extends Specification {
    @Shared
    @ClassRule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def 'interface methods'() {
        setup:
        GeneratedJob one = new GeneratedJob(null, 'one')
        GeneratedJob two = new GeneratedJob('foo', 'two')

        when:
        GeneratedJobsBuildAction action = new GeneratedJobsBuildAction([one, two], LookupStrategy.JENKINS_ROOT)

        then:
        action.iconFileName == null
        action.displayName == null
        action.urlName == null
        action.modifiedObjects != null
        action.modifiedObjects.size() == 2
        action.modifiedObjects.contains(one)
        action.modifiedObjects.contains(two)
    }

    def 'get items not found'() {
        setup:
        GeneratedJob one = new GeneratedJob(null, 'one')
        GeneratedJob two = new GeneratedJob('bar', 'foo/two')
        Run run = Mock(Run)
        run.parent >> Mock(Job)
        GeneratedJobsBuildAction action = new GeneratedJobsBuildAction([one, two], LookupStrategy.JENKINS_ROOT)
        action.onLoad(run)

        when:
        Set<Item> items = action.items

        then:
        items != null
        items.empty
    }

    def 'get items'() {
        setup:
        Item jobOne = jenkinsRule.jenkins.createProject(FreeStyleProject, 'one')
        Item jobTwo = jenkinsRule.jenkins.createProject(FreeStyleProject, 'two')
        Item jobThree = jenkinsRule.jenkins.createProject(FreeStyleProject, 'aaa')
        GeneratedJob one = new GeneratedJob(null, 'one')
        GeneratedJob two = new GeneratedJob(null, 'two')
        GeneratedJob three = new GeneratedJob(null, 'aaa')
        Run run = Mock(Run)
        run.parent >> Mock(Job)
        GeneratedJobsBuildAction action = new GeneratedJobsBuildAction([one, two, three], LookupStrategy.JENKINS_ROOT)
        action.onLoad(run)

        when:
        List<Item> items = action.items as List

        then:
        items != null
        items.size() == 3
        items[0] == jobThree
        items[1] == jobOne
        items[2] == jobTwo
    }

    def 'project actions'() {
        setup:
        Job job = Mock(Job)
        Run run = Mock(Run)
        run.parent >> job
        GeneratedJobsBuildAction action = new GeneratedJobsBuildAction([], LookupStrategy.JENKINS_ROOT)
        action.onLoad(run)

        when:
        Collection<? extends Action> actions = action.projectActions

        then:
        actions != null
        actions.size() == 1
        actions.first() instanceof GeneratedJobsAction
        GeneratedJobsAction generatedJobsAction = actions.first() as GeneratedJobsAction
        generatedJobsAction.job == job
    }

    def 'deserialize'() {
        setup:
        String xml = '''<javaposse.jobdsl.plugin.actions.GeneratedJobsBuildAction>
    <lookupStrategy>SEED_JOB</lookupStrategy>
    <modifiedObjects class="linked-hash-set">
        <javaposse.jobdsl.dsl.GeneratedJob>
            <jobName>one</jobName>
        </javaposse.jobdsl.dsl.GeneratedJob>
        <javaposse.jobdsl.dsl.GeneratedJob>
            <jobName>two</jobName>
            <templateName>test</templateName>
        </javaposse.jobdsl.dsl.GeneratedJob>
    </modifiedObjects>
</javaposse.jobdsl.plugin.actions.GeneratedJobsBuildAction>'''

        when:
        GeneratedJobsBuildAction result = Run.XSTREAM2.fromXML(xml) as GeneratedJobsBuildAction

        then:
        result != null
        result.lookupStrategy == LookupStrategy.SEED_JOB
        result.modifiedObjects != null
        result.modifiedObjects.size() == 2
        result.modifiedObjects.contains(new GeneratedJob(null, 'one'))
        result.modifiedObjects.contains(new GeneratedJob('test', 'two'))
    }

    def 'deserialize without lookupStrategy'() {
        setup:
        String xml = '''<javaposse.jobdsl.plugin.actions.GeneratedJobsBuildAction>
    <modifiedObjects class="linked-hash-set">
        <javaposse.jobdsl.dsl.GeneratedJob>
            <jobName>one</jobName>
        </javaposse.jobdsl.dsl.GeneratedJob>
        <javaposse.jobdsl.dsl.GeneratedJob>
            <jobName>two</jobName>
            <templateName>test</templateName>
        </javaposse.jobdsl.dsl.GeneratedJob>
    </modifiedObjects>
</javaposse.jobdsl.plugin.actions.GeneratedJobsBuildAction>'''

        when:
        GeneratedJobsBuildAction result = Run.XSTREAM2.fromXML(xml) as GeneratedJobsBuildAction

        then:
        result != null
        result.lookupStrategy == LookupStrategy.JENKINS_ROOT
        result.modifiedObjects != null
        result.modifiedObjects.size() == 2
        result.modifiedObjects.contains(new GeneratedJob(null, 'one'))
        result.modifiedObjects.contains(new GeneratedJob('test', 'two'))
    }

    def 'deserialize with modifiedJobs'() {
        setup:
        String xml = '''<javaposse.jobdsl.plugin.actions.GeneratedJobsBuildAction>
    <lookupStrategy>SEED_JOB</lookupStrategy>
    <modifiedJobs class="linked-hash-set">
        <javaposse.jobdsl.dsl.GeneratedJob>
            <jobName>one</jobName>
        </javaposse.jobdsl.dsl.GeneratedJob>
        <javaposse.jobdsl.dsl.GeneratedJob>
            <jobName>two</jobName>
            <templateName>test</templateName>
        </javaposse.jobdsl.dsl.GeneratedJob>
    </modifiedJobs>
</javaposse.jobdsl.plugin.actions.GeneratedJobsBuildAction>'''

        when:
        GeneratedJobsBuildAction result = Run.XSTREAM2.fromXML(xml) as GeneratedJobsBuildAction

        then:
        result != null
        result.lookupStrategy == LookupStrategy.SEED_JOB
        result.modifiedObjects != null
        result.modifiedObjects.size() == 2
        result.modifiedObjects.contains(new GeneratedJob(null, 'one'))
        result.modifiedObjects.contains(new GeneratedJob('test', 'two'))
    }
}
