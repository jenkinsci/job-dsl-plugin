package javaposse.jobdsl.plugin

import hudson.Util
import hudson.model.AbstractItem
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class WarnConfigChangeActionSpec extends Specification {
    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def 'icon file name'() {
        when:
        String iconFileName = new WarnConfigChangeAction(Mock(AbstractItem), 'digest').iconFileName

        then:
        iconFileName == null
    }

    def 'display name'() {
        when:
        String displayName = new WarnConfigChangeAction(Mock(AbstractItem), 'digest').displayName

        then:
        displayName == null
    }

    def 'URL name'() {
        when:
        String urlName = new WarnConfigChangeAction(Mock(AbstractItem), 'digest').urlName

        then:
        urlName == 'warnConfigChange'
    }

    def 'Config is not changed when digest is the same'() {
        setup:
        def item = jenkinsRule.createFreeStyleProject('test-job')
        def digest = Util.getDigestOf(item.configFile.file)

        when:
        def warnConfigChangeAction = new WarnConfigChangeAction(item, digest)

        then:
        !warnConfigChangeAction.configChanged
    }

    def 'Config is changed when digest is not the same'() {
        setup:
        def item = jenkinsRule.createFreeStyleProject('test-job')

        when:
        def warnConfigChangeAction = new WarnConfigChangeAction(item, 'changed')

        then:
        warnConfigChangeAction.configChanged
    }
}
