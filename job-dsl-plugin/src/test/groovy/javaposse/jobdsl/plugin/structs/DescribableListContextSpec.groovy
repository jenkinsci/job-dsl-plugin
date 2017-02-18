package javaposse.jobdsl.plugin.structs

import hudson.triggers.SCMTrigger
import hudson.triggers.TimerTrigger
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.plugin.fixtures.ADescribable
import javaposse.jobdsl.plugin.fixtures.ADuplicateBuilder
import javaposse.jobdsl.plugin.fixtures.BDuplicateBuilder
import javaposse.jobdsl.plugin.fixtures.DeprecatedTrigger
import jenkins.triggers.ReverseBuildTrigger
import org.jenkinsci.plugins.structs.describable.DescribableModel
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Shared
import spock.lang.Specification

class DescribableListContextSpec extends Specification {
    @Shared
    @ClassRule
    JenkinsRule jenkinsRule = new JenkinsRule()

    JobManagement jobManagement = Mock(JobManagement)

    def 'invalid args'() {
        setup:
        DescribableListContext context = new DescribableListContext(
                [new DescribableModel(ReverseBuildTrigger)],
                jobManagement
        )

        when:
        context.reverseBuildTrigger('foo')

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableListContext
        e.method == 'reverseBuildTrigger'
        e.arguments == ['foo']
        !e.static
    }

    def 'unknown name'() {
        setup:
        DescribableListContext context = new DescribableListContext(
                [new DescribableModel(ReverseBuildTrigger)],
                jobManagement
        )

        when:
        context.myTrigger()

        then:
        MissingMethodException e = thrown(MissingMethodException)
        e.type == DescribableListContext
        e.method == 'myTrigger'
        e.arguments.length == 0
        !e.static
    }

    def 'duplicate name'() {
        setup:
        DescribableListContext context = new DescribableListContext(
                [new DescribableModel(ADuplicateBuilder), new DescribableModel(BDuplicateBuilder)],
                jobManagement
        )

        when:
        context.duplicate()

        then:
        Exception e = thrown(DslException)
        e.message.contains(ADuplicateBuilder.name)
        e.message.contains(BDuplicateBuilder.name)
    }

    def 'create instance'() {
        setup:
        DescribableListContext context = new DescribableListContext([new DescribableModel(TimerTrigger)], jobManagement)

        when:
        context.timerTrigger {
            spec('@midnight')
        }

        then:
        context.values != null
        context.values.size() == 1
        context.values[0] instanceof TimerTrigger
        context.values[0].spec  == '@midnight'
    }

    def 'create instance without closure'() {
        setup:
        DescribableListContext context = new DescribableListContext([new DescribableModel(ADescribable)], jobManagement)

        when:
        context.aDescribable()

        then:
        context.values != null
        context.values.size() == 1
        context.values[0] instanceof ADescribable
    }

    def 'create multiple instance'() {
        setup:
        DescribableListContext context = new DescribableListContext(
                [new DescribableModel(SCMTrigger), new DescribableModel(TimerTrigger)],
                jobManagement
        )

        when:
        context.scmTrigger {
            scmpoll_spec('@daily')
            ignorePostCommitHooks(false)
        }
        context.timerTrigger {
            spec('@midnight')
        }

        then:
        context.values != null
        context.values.size() == 2
        context.values[0] instanceof SCMTrigger
        context.values[0].spec == '@daily'
        context.values[1] instanceof TimerTrigger
        context.values[1].spec == '@midnight'
    }

    def 'log deprecation warning'() {
        setup:
        DescribableListContext context = new DescribableListContext(
                [new DescribableModel(DeprecatedTrigger)],
                jobManagement
        )

        when:
        context.old()

        then:
        1 * jobManagement.logDeprecationWarning('old')
    }
}
