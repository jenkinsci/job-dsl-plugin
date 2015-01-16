package javaposse.jobdsl.plugin

import hudson.Util
import hudson.model.AbstractItem
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class SeedJobActionSpec extends Specification {
    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule()

    AbstractItem item = Mock(AbstractItem)
    SeedReference seedReference = new SeedReference('template', 'seed', 'digest')

    def 'icon file name'() {
        when:
        String iconFileName = new SeedJobAction(item, seedReference).iconFileName

        then:
        iconFileName == null
    }

    def 'display name'() {
        when:
        String displayName = new SeedJobAction(item, seedReference).displayName

        then:
        displayName == 'Seed job:'
    }

    def 'URL name'() {
        when:
        String urlName = new SeedJobAction(item, seedReference).urlName

        then:
        urlName == 'seedJob'
    }

    def 'no template'() {
        setup:
        AbstractItem seedJob = jenkinsRule.createFreeStyleProject('seed')

        when:
        SeedJobAction action = new SeedJobAction(item, seedReference)

        then:
        action.seedJob == seedJob
        action.templateJob == null
    }

    def 'with template'() {
        setup:
        AbstractItem seedJob = jenkinsRule.createFreeStyleProject('seed')
        AbstractItem templateJob = jenkinsRule.createFreeStyleProject('template')

        when:
        SeedJobAction action = new SeedJobAction(item, seedReference)

        then:
        action.seedJob == seedJob
        action.templateJob == templateJob
    }

    def 'Config is not changed when digest is the same'() {
        setup:
        AbstractItem item = jenkinsRule.createFreeStyleProject('test')
        seedReference.digest = Util.getDigestOf(item.configFile.file)

        when:
        def action = new SeedJobAction(item, seedReference)

        then:
        !action.configChanged
    }

    def 'Config is changed when digest is not the same'() {
        setup:
        AbstractItem item = jenkinsRule.createFreeStyleProject('test')
        seedReference.digest = 'changed'

        when:
        def action = new SeedJobAction(item, seedReference)

        then:
        action.configChanged
    }
}
