package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class PropertiesContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    PropertiesContext context = new PropertiesContext(jobManagement, item)

    def 'sideBarLinks with no options'() {
        when:
        context.sidebarLinks {
        }

        then:
        with(context.propertiesNodes[0]) {
            name() == 'hudson.plugins.sidebar__link.ProjectLinks'
            children().size() == 1
            links[0].value().empty
        }
    }

    def 'sideBarLinks with all options'() {
        when:
        context.sidebarLinks {
            link('http://foo.org', 'Foo')
            link('http://bar.org', 'Bar', 'bar.png')
        }

        then:
        with(context.propertiesNodes[0]) {
            name() == 'hudson.plugins.sidebar__link.ProjectLinks'
            children().size() == 1
            with(links[0]) {
                children().size() == 2
                with(children()[0]) {
                    name() == 'hudson.plugins.sidebar__link.LinkAction'
                    children().size() == 3
                    url[0].value() == 'http://foo.org'
                    text[0].value() == 'Foo'
                    icon[0].value() == ''
                }
                with(children()[1]) {
                    name() == 'hudson.plugins.sidebar__link.LinkAction'
                    children().size() == 3
                    url[0].value() == 'http://bar.org'
                    text[0].value() == 'Bar'
                    icon[0].value() == 'bar.png'
                }
            }
        }
    }

    def 'sideBarLinks with invalid options'(String url, String text) {
        when:
        context.sidebarLinks {
            link(url, text)
        }

        then:
        thrown(DslScriptException)

        where:
        url    | text
        null   | 'test'
        ''     | 'test'
        'test' | null
        'test' | ''
    }

    def 'set custom icon'() {
        when:
        context.customIcon('myfancyicon.png')

        then:
        with(context.propertiesNodes[0]) {
            name() == 'jenkins.plugins.jobicon.CustomIconProperty'
            children().size() == 1
            iconfile[0].value() == 'myfancyicon.png'
        }
        1 * jobManagement.requireMinimumPluginVersion('custom-job-icon', '0.2')
    }

    def 'set custom icon with invalid options'(String fileName) {
        when:
        context.customIcon(fileName)

        then:
        thrown(DslScriptException)

        where:
        fileName << [null, '']
    }

    def 'zenTimestamp'() {
        when:
        context.zenTimestamp('some-pattern')

        then:
        with(context.propertiesNodes[0]) {
            name() == 'hudson.plugins.zentimestamp.ZenTimestampJobProperty'
            children().size() == 2
            changeBUILDID[0].value() == true
            pattern[0].value() == 'some-pattern'
        }
    }

    def 'rebuild with no options'() {
        when:
        context.rebuild {}

        then:
        with(context.propertiesNodes[0]) {
            name() == 'com.sonyericsson.rebuild.RebuildSettings'
            children().size() == 2
            autoRebuild[0].value() == false
            rebuildDisabled[0].value() == false
        }
    }

    def 'rebuild with all options'() {
        when:
        context.rebuild {
            autoRebuild()
            rebuildDisabled()
        }

        then:
        with(context.propertiesNodes[0]) {
            name() == 'com.sonyericsson.rebuild.RebuildSettings'
            children().size() == 2
            autoRebuild[0].value() == true
            rebuildDisabled[0].value() == true
        }
    }

    def 'github project URL with value'() {
        when:
        context.githubProjectUrl(value)

        then:
        with(context.propertiesNodes[0]) {
            name() == 'com.coravy.hudson.plugins.github.GithubProjectProperty'
            children().size() == 1
            projectUrl[0].value() == expected
        }
        1 * jobManagement.requireMinimumPluginVersion('github', '1.12.0')

        where:
        value                                         || expected
        'https://github.com/jenkinsci/job-dsl-plugin' || 'https://github.com/jenkinsci/job-dsl-plugin'
        ''                                            || ''
        null                                          || ''
    }

    def 'buildFailureAnalyzer'() {
        when:
        context.buildFailureAnalyzer()

        then:
        with(context.propertiesNodes[0]) {
            name() == 'com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty'
            children().size() == 1
            doNotScan[0].value() == false
        }
    }

    def 'buildFailureAnalyzer with value'() {
        when:
        context.buildFailureAnalyzer(value)

        then:
        with(context.propertiesNodes[0]) {
            name() == 'com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty'
            children().size() == 1
            doNotScan[0].value() == expected
        }

        where:
        value || expected
        true  || false
        false || true
    }
}
