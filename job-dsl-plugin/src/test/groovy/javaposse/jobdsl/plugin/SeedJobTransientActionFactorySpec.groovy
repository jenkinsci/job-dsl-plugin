package javaposse.jobdsl.plugin

import hudson.model.AbstractItem
import hudson.model.Action
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class SeedJobTransientActionFactorySpec extends Specification {
    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def 'handles AbstractItems'() {
        when:
        Class<?> type = new SeedJobTransientActionFactory().type()

        then:
        type == AbstractItem
    }

    def 'no SeedReference'() {
        setup:
        AbstractItem target = jenkinsRule.createFreeStyleProject('target')

        when:
        Collection<? extends Action> actions = new SeedJobTransientActionFactory().createFor(target)

        then:
        actions.empty
    }

    def 'with SeedReference'() {
        setup:
        AbstractItem targetJob = jenkinsRule.createFreeStyleProject('target')
        AbstractItem seedJob = jenkinsRule.createFreeStyleProject('seed')
        AbstractItem templateJob = jenkinsRule.createFreeStyleProject('template')
        jenkinsRule.jenkins.getDescriptorByType(DescriptorImpl).generatedJobMap[targetJob.fullName] =
                new SeedReference(templateJob.fullName, seedJob.fullName, 'digest')

        when:
        Collection<? extends Action> actions = new SeedJobTransientActionFactory().createFor(targetJob)

        then:
        actions.size() == 1
        SeedJobAction seedJobAction = actions.grep(SeedJobAction).first() as SeedJobAction
        seedJobAction.seedJob == seedJob
        seedJobAction.templateJob == templateJob
        seedJobAction.digest == 'digest'
        seedJobAction.item == targetJob
    }
}
