package javaposse.jobdsl.plugin

import hudson.util.ListBoxModel
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class DescriptorImplSpec extends Specification {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    def 'lookup strategy items'() {
        when:
        ListBoxModel listBoxModel = new DescriptorImpl().doFillLookupStrategyItems()

        then:
        listBoxModel.size() == 2
        listBoxModel.get(0).name == 'Jenkins Root'
        listBoxModel.get(0).value == 'JENKINS_ROOT'
        listBoxModel.get(1).name == 'Seed Job'
        listBoxModel.get(1).value == 'SEED_JOB'
    }
}
