package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class GitBrowserContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    GitBrowserContext context = new GitBrowserContext(jobManagement, item)

    def 'add extension'() {
        given:
        Node paramNode = new NodeBuilder().'my.custom.Browser' {
            repoUrl('foo')
        }

        when:
        context.addExtensionNode(paramNode)

        then:
        context.browser != null
        context.browser.name() == 'browser'
        context.browser.attributes().size() == 1
        context.browser.attributes()['class'] == 'my.custom.Browser'
        context.browser.children().size() == 1
        context.browser.repoUrl.text() == 'foo'
    }
}
