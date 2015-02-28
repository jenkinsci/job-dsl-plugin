package javaposse.jobdsl.plugin

import hudson.Util
import hudson.model.Item
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import org.jvnet.hudson.test.WithoutJenkins
import spock.lang.Specification

class SeedJobActionSpec extends Specification {
    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule()

    Item item = Mock(Item)
    SeedReference seedReference = new SeedReference('template', 'seed', 'digest')

    @WithoutJenkins
    def 'icon file name'() {
        when:
        String iconFileName = new SeedJobAction(item, seedReference).iconFileName

        then:
        iconFileName == null
    }

    @WithoutJenkins
    def 'display name'() {
        when:
        String displayName = new SeedJobAction(item, seedReference).displayName

        then:
        displayName == 'Seed job:'
    }

    @WithoutJenkins
    def 'URL name'() {
        when:
        String urlName = new SeedJobAction(item, seedReference).urlName

        then:
        urlName == 'seedJob'
    }

    def 'no template'() {
        setup:
        Item seedJob = jenkinsRule.createFreeStyleProject('seed')

        when:
        SeedJobAction action = new SeedJobAction(item, seedReference)

        then:
        action.seedJob == seedJob
        action.templateJob == null
    }

    def 'with template'() {
        setup:
        Item seedJob = jenkinsRule.createFreeStyleProject('seed')
        Item templateJob = jenkinsRule.createFreeStyleProject('template')

        when:
        SeedJobAction action = new SeedJobAction(item, seedReference)

        then:
        action.seedJob == seedJob
        action.templateJob == templateJob
    }

    def 'Config is not changed when digest is the same'() {
        setup:
        Item item = jenkinsRule.createFreeStyleProject('test')
        seedReference.digest = Util.getDigestOf(item.configFile.file)

        when:
        def action = new SeedJobAction(item, seedReference)

        then:
        !action.configChanged
    }

    def 'Config is changed when digest is not the same'() {
        setup:
        Item item = jenkinsRule.createFreeStyleProject('test')
        seedReference.digest = 'changed'

        when:
        def action = new SeedJobAction(item, seedReference)

        then:
        action.configChanged
    }
}
