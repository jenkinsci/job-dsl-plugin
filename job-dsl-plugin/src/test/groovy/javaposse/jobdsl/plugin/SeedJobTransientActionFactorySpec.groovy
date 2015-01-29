package javaposse.jobdsl.plugin

import hudson.model.Action
import hudson.model.Item
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class SeedJobTransientActionFactorySpec extends Specification {
    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def 'handles Items'() {
        when:
        Class<?> type = new SeedJobTransientActionFactory().type()

        then:
        type == Item
    }

    def 'no SeedReference'() {
        setup:
        Item target = jenkinsRule.createFreeStyleProject('target')

        when:
        Collection<? extends Action> actions = new SeedJobTransientActionFactory().createFor(target)

        then:
        actions.empty
    }

    def 'with SeedReference'() {
        setup:
        Item targetJob = jenkinsRule.createFreeStyleProject('target')
        Item seedJob = jenkinsRule.createFreeStyleProject('seed')
        Item templateJob = jenkinsRule.createFreeStyleProject('template')
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
