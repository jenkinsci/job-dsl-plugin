package javaposse.jobdsl.plugin.actions

import hudson.model.Action
import hudson.model.Job
import hudson.model.ListView
import hudson.model.View
import javaposse.jobdsl.dsl.GeneratedView
import javaposse.jobdsl.plugin.LookupStrategy
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Shared
import spock.lang.Specification
import hudson.model.Run

class GeneratedViewsBuildActionSpec extends Specification {
    @Shared
    @ClassRule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def 'interface methods'() {
        setup:
        GeneratedView one = new GeneratedView('one')
        GeneratedView two = new GeneratedView('two')

        when:
        GeneratedViewsBuildAction action = new GeneratedViewsBuildAction([one, two], LookupStrategy.JENKINS_ROOT)

        then:
        action.iconFileName == null
        action.displayName == null
        action.urlName == null
        action.modifiedObjects != null
        action.modifiedObjects.size() == 2
        action.modifiedObjects.contains(one)
        action.modifiedObjects.contains(two)
    }

    def 'get views not found'() {
        setup:
        GeneratedView one = new GeneratedView('one')
        GeneratedView two = new GeneratedView('foo/two')
        Run run = Mock(Run)
        run.parent >> Mock(Job)
        GeneratedViewsBuildAction action = new GeneratedViewsBuildAction([one, two], LookupStrategy.JENKINS_ROOT)
        action.onLoad(run)

        when:
        Set<View> views = action.views

        then:
        views != null
        views.empty
    }

    def 'get views'() {
        setup:
        View viewOne = new ListView('one')
        View viewTwo = new ListView('two')
        View viewThree = new ListView('aaa')
        jenkinsRule.jenkins.addView(viewOne)
        jenkinsRule.jenkins.addView(viewTwo)
        jenkinsRule.jenkins.addView(viewThree)
        GeneratedView one = new GeneratedView('one')
        GeneratedView two = new GeneratedView('two')
        GeneratedView three = new GeneratedView('aaa')
        Run run = Mock(Run)
        run.parent >> Mock(Job)
        GeneratedViewsBuildAction action = new GeneratedViewsBuildAction([one, two, three], LookupStrategy.JENKINS_ROOT)
        action.onLoad(run)

        when:
        List<View> views = action.views as List

        then:
        views != null
        views.size() == 3
        views[0] == viewThree
        views[1] == viewOne
        views[2] == viewTwo
    }

    def 'project actions'() {
        setup:
        Job job = Mock(Job)
        Run run = Mock(Run)
        run.parent >> job
        GeneratedViewsBuildAction action = new GeneratedViewsBuildAction([], LookupStrategy.JENKINS_ROOT)
        action.onLoad(run)

        when:
        Collection<? extends Action> actions = action.projectActions

        then:
        actions != null
        actions.size() == 1
        actions.first() instanceof GeneratedViewsAction
        GeneratedViewsAction generatedViewsAction = actions.first() as GeneratedViewsAction
        generatedViewsAction.job == job
    }

    def 'deserialize'() {
        setup:
        String xml = '''<javaposse.jobdsl.plugin.actions.GeneratedViewsBuildAction>
    <lookupStrategy>SEED_JOB</lookupStrategy>
    <modifiedObjects class="linked-hash-set">
        <javaposse.jobdsl.dsl.GeneratedView>
            <name>one</name>
        </javaposse.jobdsl.dsl.GeneratedView>
        <javaposse.jobdsl.dsl.GeneratedView>
            <name>two</name>
        </javaposse.jobdsl.dsl.GeneratedView>
    </modifiedObjects>
</javaposse.jobdsl.plugin.actions.GeneratedViewsBuildAction>'''

        when:
        GeneratedViewsBuildAction result = Run.XSTREAM2.fromXML(xml) as GeneratedViewsBuildAction

        then:
        result != null
        result.lookupStrategy == LookupStrategy.SEED_JOB
        result.modifiedObjects != null
        result.modifiedObjects.size() == 2
        result.modifiedObjects.contains(new GeneratedView('one'))
        result.modifiedObjects.contains(new GeneratedView('two'))
    }

    def 'deserialize without lookupStrategy'() {
        setup:
        String xml = '''<javaposse.jobdsl.plugin.actions.GeneratedViewsBuildAction>
    <modifiedObjects class="linked-hash-set">
        <javaposse.jobdsl.dsl.GeneratedView>
            <name>one</name>
        </javaposse.jobdsl.dsl.GeneratedView>
        <javaposse.jobdsl.dsl.GeneratedView>
            <name>two</name>
        </javaposse.jobdsl.dsl.GeneratedView>
    </modifiedObjects>
</javaposse.jobdsl.plugin.actions.GeneratedViewsBuildAction>'''

        when:
        GeneratedViewsBuildAction result = Run.XSTREAM2.fromXML(xml) as GeneratedViewsBuildAction

        then:
        result != null
        result.lookupStrategy == LookupStrategy.JENKINS_ROOT
        result.modifiedObjects != null
        result.modifiedObjects.size() == 2
        result.modifiedObjects.contains(new GeneratedView('one'))
        result.modifiedObjects.contains(new GeneratedView('two'))
    }

    def 'deserialize with modifiedViews'() {
        setup:
        String xml = '''<javaposse.jobdsl.plugin.actions.GeneratedViewsBuildAction>
    <lookupStrategy>SEED_JOB</lookupStrategy>
    <modifiedViews class="linked-hash-set">
        <javaposse.jobdsl.dsl.GeneratedView>
            <name>one</name>
        </javaposse.jobdsl.dsl.GeneratedView>
        <javaposse.jobdsl.dsl.GeneratedView>
            <name>two</name>
        </javaposse.jobdsl.dsl.GeneratedView>
    </modifiedViews>
</javaposse.jobdsl.plugin.actions.GeneratedViewsBuildAction>'''

        when:
        GeneratedViewsBuildAction result = Run.XSTREAM2.fromXML(xml) as GeneratedViewsBuildAction

        then:
        result != null
        result.lookupStrategy == LookupStrategy.SEED_JOB
        result.modifiedObjects != null
        result.modifiedObjects.size() == 2
        result.modifiedObjects.contains(new GeneratedView('one'))
        result.modifiedObjects.contains(new GeneratedView('two'))
    }
}
