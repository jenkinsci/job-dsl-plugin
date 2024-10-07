package javaposse.jobdsl.plugin

import jenkins.model.GlobalConfigurationCategory
import net.sf.json.JSONObject
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import org.kohsuke.stapler.StaplerRequest2
import spock.lang.Shared
import spock.lang.Specification

class GlobalJobDslSecurityConfigurationSpec extends Specification {
    @Shared
    @ClassRule
    @SuppressWarnings('JUnitPublicField')
    public JenkinsRule jenkinsRule = new JenkinsRule()

    private GlobalJobDslSecurityConfiguration config

    def setup() {
        config = jenkinsRule.instance.getDescriptorByType(GlobalJobDslSecurityConfiguration)
    }

    def 'security category'() {
        expect:
        config.category instanceof GlobalConfigurationCategory.Security
    }

    def 'enable security'() {
        setup:
        StaplerRequest2 req = Mock(StaplerRequest2)
        JSONObject json = new JSONObject()
        json.put('useScriptSecurity', '')
        config.useScriptSecurity = false

        when:
        boolean result = config.configure(req, json)

        then:
        result
        config.useScriptSecurity

        when:
        jenkinsRule.submit(jenkinsRule.createWebClient().goTo('configureSecurity').getFormByName('config'))

        then:
        config.useScriptSecurity
    }

    def 'disable security'() {
        setup:
        StaplerRequest2 req = Mock(StaplerRequest2)
        JSONObject json = new JSONObject()
        config.useScriptSecurity = true

        when:
        boolean result = config.configure(req, json)

        then:
        result
        !config.useScriptSecurity

        when:
        jenkinsRule.submit(jenkinsRule.createWebClient().goTo('configureSecurity').getFormByName('config'))

        then:
        !config.useScriptSecurity
    }
}
