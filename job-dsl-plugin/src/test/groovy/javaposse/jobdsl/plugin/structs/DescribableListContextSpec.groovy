package javaposse.jobdsl.plugin.structs

import hudson.triggers.SCMTrigger
import hudson.triggers.TimerTrigger
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.plugin.fixtures.ADescribable
import javaposse.jobdsl.plugin.fixtures.ADuplicateBuilder
import javaposse.jobdsl.plugin.fixtures.BDuplicateBuilder
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

    def 'invalid args'() {
        setup:
        DescribableListContext context = new DescribableListContext([new DescribableModel(ReverseBuildTrigger)])

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
        DescribableListContext context = new DescribableListContext([new DescribableModel(ReverseBuildTrigger)])

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
        DescribableListContext context = new DescribableListContext([
                new DescribableModel(ADuplicateBuilder),
                new DescribableModel(BDuplicateBuilder),
        ])

        when:
        context.duplicate()

        then:
        Exception e = thrown(DslException)
        e.message.contains(ADuplicateBuilder.name)
        e.message.contains(BDuplicateBuilder.name)
    }

    def 'create instance'() {
        setup:
        DescribableListContext context = new DescribableListContext([new DescribableModel(TimerTrigger)])

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
        DescribableListContext context = new DescribableListContext([new DescribableModel(ADescribable)])

        when:
        context.aDescribable()

        then:
        context.values != null
        context.values.size() == 1
        context.values[0] instanceof ADescribable
    }

    def 'create multiple instance'() {
        setup:
        DescribableListContext context = new DescribableListContext([
                new DescribableModel(SCMTrigger),
                new DescribableModel(TimerTrigger),
        ])

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
}
