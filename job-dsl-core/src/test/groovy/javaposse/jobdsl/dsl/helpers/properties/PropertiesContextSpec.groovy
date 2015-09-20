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
}
