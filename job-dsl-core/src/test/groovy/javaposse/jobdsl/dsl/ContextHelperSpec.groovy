package javaposse.jobdsl.dsl

import spock.lang.Specification

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

/**
 * Work through the additional functionality we're offer over node
 */
class ContextHelperSpec extends Specification {
    static final String XML = '''<?xml version="1.0" encoding="UTF-8"?>
<project>
  <actions/>
  <description></description>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <scm class="hudson.scm.NullSCM"/>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers class="vector"/>
  <concurrentBuild>false</concurrentBuild>
  <builders>
      <builder/>
  </builders>
  <publishers/>
  <buildWrappers/>
</project>
'''

    final Node root = new XmlParser().parse(new StringReader(XML))

    def setup() {
        Logger.getLogger('javaposse.jobdsl').setLevel(Level.ALL)

        // Our only choice to allow lower logging is to allow it for everyone since JUL puts the handler in one place
        // and sets a horrible default
        LogManager.logManager.getLogger('').handlers.each { Handler handler ->
            handler.setLevel(Level.ALL)
        }
    }

    def 'lookup with existent nodes'() {
        when:
        def builders = 'build'
        ContextHelper.executeConfigureBlock(root) { project ->
            assertNotNull(project)
            assertTrue(project instanceof Node)

            project / builders / builder
        }

        then:
        noExceptionThrown()
    }

    def 'lookup with non-existent nodes'() {
        when:
        ContextHelper.executeConfigureBlock(root) { project ->
            project / builders / 'hudson.security.AuthorizationMatrixProperty'
        }

        then:
        noExceptionThrown()
    }

    def 'add single child in left shift closure'() {
        when:
        ContextHelper.executeConfigureBlock(root) { project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix << permission('hudson.model.Item.Delete:jryan')
        }

        then:
        def builders = root.builders[0]
        builders.children().size() == 2
        NodeList matrixList = builders.'hudson.security.AuthorizationMatrixProperty'
        matrixList.size() == 1
        Node matrix = matrixList[0]
        NodeList permissions = matrix.permission
        permissions.size() == 1
        permissions[0].text() == 'hudson.model.Item.Delete:jryan'
    }

    def 'call Node multiple times to append'() {
        when:
        ContextHelper.executeConfigureBlock(root) { project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix << permission('hudson.model.Item.Delete:jryan')
            matrix << permission('hudson.model.Item.Configure:jryan')
        }

        then:
        NodeList permissions = root.builders[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 2
        permissions[0].text() == 'hudson.model.Item.Delete:jryan'
        permissions[1].text() == 'hudson.model.Item.Configure:jryan'
    }

    def 'use chained left shift'() {
        when:
        ContextHelper.executeConfigureBlock(root) { project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix << {
                permission('hudson.model.Item.Configure:jryan')
            } << {
                permission('hudson.model.Item.Configure:jack')
            }
        }

        then:
        NodeList permissions = root.builders[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 2
        permissions[0].text() == 'hudson.model.Item.Configure:jryan'
        permissions[1].text() == 'hudson.model.Item.Configure:jack'
    }

    def 'left shift chained with non-closures'() {
        when:
        ContextHelper.executeConfigureBlock(root) { project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix << permission('hudson.model.Item.Configure:jryan') << permission('hudson.model.Item.Configure:jack')
        }

        then:
        NodeList permissions = root.builders[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 2
        permissions[0].text() == 'hudson.model.Item.Configure:jryan'
        permissions[1].text() == 'hudson.model.Item.Configure:jack'
    }

    def 'left shift with multiple children in closure'() {
        when:
        ContextHelper.executeConfigureBlock(root) { project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix << {
                permission('hudson.model.Item.Configure:jill')
                permission('hudson.model.Item.Configure:jack')
            }
        }

        then:
        NodeList permissions = root.builders[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 2
        permissions[0].text() == 'hudson.model.Item.Configure:jill'
        permissions[1].text() == 'hudson.model.Item.Configure:jack'
    }

    def 'left shift with children having children'() {
        when:
        ContextHelper.executeConfigureBlock(root) { project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix << {
                permission('hudson.model.Item.Configure:jill') {
                    header('My Perm')
                }
                permission('hudson.model.Item.Configure:jack')
            }
        }

        then:
        NodeList permissions = root.builders[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 2
        permissions[0].children().size() == 2
        permissions[0].children()[0] == 'hudson.model.Item.Configure:jill'
        permissions[0].header.size() == 1
        permissions[0].header[0].text() == 'My Perm'
        permissions[1].text() == 'hudson.model.Item.Configure:jack'
    }

    def 'children with children without closure'() {
        when:
        ContextHelper.executeConfigureBlock(root) { project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix <<
                    permissions {
                        header('My document')
                    }
        }

        then:
        NodeList permissions = root.builders[0].'hudson.security.AuthorizationMatrixProperty'[0].permissions
        permissions.size() == 1
        permissions[0].header.size() == 1
        permissions[0].header[0].text() == 'My document'
    }

    void confirmBrowserUrl(Node scmNode) {
        assert scmNode.browser.size() == 1
        assert scmNode.browser[0].attributes()['class'] == 'hudson.plugins.git.browser.GithubWeb'
        assert scmNode.browser[0].url.size() == 1
        assert scmNode.browser[0].url[0].value() == 'https://github.com/foo/bar'
    }

    def 'Setup git browser node'() {
        when:
        ContextHelper.executeConfigureBlock(root) { project ->
            // Find irrelevant of existing attributes
            def browserNode = project / scm / browser

            // Overwrite old value
            browserNode.attributes()['class'] = 'hudson.plugins.git.browser.GithubWeb'

            browserNode / url << 'https://github.com/foo/bar'
        }

        then:
        confirmBrowserUrl(root.scm[0])
    }

    def 'configuring nodes with attributes purely nested'() {
        when:
        ContextHelper.executeConfigureBlock(root) {
            it / scm / browser(class: 'hudson.plugins.git.browser.GithubWeb') / url('https://github.com/foo/bar')
        }

        then:
        confirmBrowserUrl(root.scm[0])

        when: // Do it again with the same values, but hopefully the same thing
        ContextHelper.executeConfigureBlock(root) {
            it / scm / browser(class: 'hudson.plugins.git.browser.GithubWeb') / url << 'https://github.com/foo/bar'
        }

        then:
        confirmBrowserUrl(root.scm[0])

        when: // Do it again with different values, but hopefully the same thing
        ContextHelper.executeConfigureBlock(root) {
            it / scm / browser(class: 'hudson.plugins.git.browser.GitoriusWeb') / url << 'https://github.com/foo/baz'
        }

        then:
        def scmNode = root.scm[0]
        assert scmNode.browser.size() == 2
        assert scmNode.browser[0].attributes()['class'] == 'hudson.plugins.git.browser.GithubWeb'
        assert scmNode.browser[0].url.size() == 1
        assert scmNode.browser[0].url[0].value() == 'https://github.com/foo/bar'
        assert scmNode.browser[1].attributes()['class'] == 'hudson.plugins.git.browser.GitoriusWeb'
        assert scmNode.browser[1].url.size() == 1
        assert scmNode.browser[1].url[0].value() == 'https://github.com/foo/baz'
    }

    def 'conversion to named node'() {
        Node node = new Node(null, 'org.example.CustomType', [foo: 'bar'])
        node.appendNode('test', 'value')

        when:
        Node namedNode = ContextHelper.toNamedNode('example', node)

        then:
        with(namedNode) {
            name() == 'example'
            attributes().size() == 2
            attribute('class') == 'org.example.CustomType'
            attribute('foo') == 'bar'
            children().size() == 1
            test[0].text() == 'value'
        }
    }
}
