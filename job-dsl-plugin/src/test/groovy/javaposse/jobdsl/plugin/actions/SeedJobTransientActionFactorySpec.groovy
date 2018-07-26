package javaposse.jobdsl.plugin.actions

import hudson.model.Action
import hudson.model.Item
import javaposse.jobdsl.plugin.DescriptorImpl
import javaposse.jobdsl.plugin.SeedReference
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import org.jvnet.hudson.test.WithoutJenkins
import spock.lang.Shared
import spock.lang.Specification

class SeedJobTransientActionFactorySpec extends Specification {
    @Shared
    @ClassRule
    @SuppressWarnings('JUnitPublicField')
    public JenkinsRule jenkinsRule = new JenkinsRule()

    @WithoutJenkins
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
        Item targetJob = jenkinsRule.createFreeStyleProject('generated-target')
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
