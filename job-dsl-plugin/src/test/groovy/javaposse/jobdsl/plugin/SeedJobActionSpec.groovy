package javaposse.jobdsl.plugin

import hudson.model.AbstractItem
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class SeedJobActionSpec extends Specification {
    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def 'icon file name'() {
        when:
        String iconFileName = new SeedJobAction('foo', 'bar').iconFileName

        then:
        iconFileName == null
    }

    def 'display name'() {
        when:
        String displayName = new SeedJobAction('foo', 'bar').displayName

        then:
        displayName == 'Seed job:'
    }

    def 'URL name'() {
        when:
        String urlName = new SeedJobAction('foo', 'bar').urlName

        then:
        urlName == 'seedJob'
    }

    def 'no template'() {
        setup:
        AbstractItem seedJob = jenkinsRule.createFreeStyleProject('seed')

        when:
        SeedJobAction action = new SeedJobAction(seedJob.fullName, null)

        then:
        action.seedJob == seedJob
        action.templateJob == null
    }

    def 'with template'() {
        setup:
        AbstractItem seedJob = jenkinsRule.createFreeStyleProject('seed')
        AbstractItem templateJob = jenkinsRule.createFreeStyleProject('template')

        when:
        SeedJobAction action = new SeedJobAction(seedJob.fullName, templateJob.fullName)

        then:
        action.seedJob == seedJob
        action.templateJob == templateJob
    }
}
