package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class PropertiesContextSpec extends Specification {
    JobManagement mockJobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    PropertiesContext context = new PropertiesContext(mockJobManagement, item)

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
        thrown(IllegalArgumentException)

        where:
        url    | text
        null   | 'test'
        ''     | 'test'
        'test' | null
        'test' | ''
    }
}
