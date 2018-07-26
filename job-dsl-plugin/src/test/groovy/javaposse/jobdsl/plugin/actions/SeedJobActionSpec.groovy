package javaposse.jobdsl.plugin.actions

import hudson.Util
import hudson.model.AbstractItem
import hudson.model.Item
import javaposse.jobdsl.plugin.SeedReference
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import org.jvnet.hudson.test.WithoutJenkins
import spock.lang.Shared
import spock.lang.Specification

class SeedJobActionSpec extends Specification {
    @Shared
    @ClassRule
    @SuppressWarnings('JUnitPublicField')
    public JenkinsRule jenkinsRule = new JenkinsRule()

    @Shared
    private AbstractItem item

    @Shared
    private Item seedJob

    @Shared
    private Item templateJob

    private final SeedReference seedReference = Mock(SeedReference)

    def setupSpec() {
        item = jenkinsRule.createFreeStyleProject('test')
        seedJob = jenkinsRule.createFreeStyleProject('seed')
        templateJob = jenkinsRule.createFreeStyleProject('template')
    }

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
        displayName == null
    }

    @WithoutJenkins
    def 'URL name'() {
        when:
        String urlName = new SeedJobAction(item, seedReference).urlName

        then:
        urlName == null
    }

    def 'no template'() {
        setup:
        SeedReference seedReference = new SeedReference('unknown-template', seedJob.fullName, 'digest')

        when:
        SeedJobAction action = new SeedJobAction(item, seedReference)

        then:
        action.seedJob == seedJob
        action.templateJob == null
    }

    def 'with template'() {
        setup:
        SeedReference seedReference = new SeedReference(templateJob.fullName, seedJob.fullName, 'digest')

        when:
        SeedJobAction action = new SeedJobAction(item, seedReference)

        then:
        action.seedJob == seedJob
        action.templateJob == templateJob
    }

    def 'Config is not changed when digest is the same'() {
        setup:
        SeedReference seedReference = new SeedReference(
                templateJob.fullName,
                seedJob.fullName,
                Util.getDigestOf(item.configFile.file)
        )

        when:
        def action = new SeedJobAction(item, seedReference)

        then:
        !action.configChanged
    }

    def 'Config is changed when digest is not the same'() {
        setup:
        SeedReference seedReference = new SeedReference(templateJob.fullName, seedJob.fullName, 'changed')

        when:
        def action = new SeedJobAction(item, seedReference)

        then:
        action.configChanged
    }
}
