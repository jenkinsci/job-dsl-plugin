package javaposse.jobdsl.dsl

import spock.lang.Specification
import com.google.common.base.Preconditions
import java.util.logging.LogManager
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.Handler

/**
 * Work through the additional functionality we're offer over node
 */
class WithXmlActionSpec extends Specification {
    public static String xml = """<?xml version='1.0' encoding='UTF-8'?>
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
"""

    final Node root = new XmlParser().parse(new StringReader(xml))

    def setup() {
        Logger.getLogger("javaposse.jobdsl").setLevel(Level.ALL)

        // Our only choice to allow lower logging is to allow it for everyone since JUL puts the handler in one place and sets a horrible default
        LogManager.logManager.getLogger("").handlers.each { Handler handler ->
            handler.setLevel(Level.ALL)
        }
    }

    def static printNode(Node n) {
        def writer = new StringWriter()
        new XmlNodePrinter(new PrintWriter(writer)).print(n)
        println writer.toString()
    }

    def execute(Closure closure) {
        def withXmlAction = new WithXmlAction(closure)
        return withXmlAction.execute(root)
    }

    def 'lookup with existent nodes'() {
        when:
        def builders = "build"
        execute { project ->
            Preconditions.checkNotNull(project)
            Preconditions.checkArgument(project instanceof Node)
            println "About to reference! ${owner} ${delegate}"

            def matrix = project / builders / builder
        }

        then:
        noExceptionThrown()
    }

    def 'lookup with non-existent nodes'() {
        when:
        execute { project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
        }

        then:
        noExceptionThrown()
    }

    def 'add single child in left shift closure'() {
        when:
        execute{ project ->
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
        execute { project ->
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
        execute { project ->
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
        execute { project ->
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
        execute { project ->
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
        execute { project ->
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
        permissions[0].text() == 'hudson.model.Item.Configure:jill'
        permissions[0].header.size() == 1
        permissions[0].header[0].text() == 'My Perm'
        permissions[1].text() == 'hudson.model.Item.Configure:jack'
    }

    def 'children with children without closure'() {
        when:
        execute { project ->
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

    def 'lookup up nodes with attribute without attributes'() {
        when:
        Node trigger = execute { it / triggers }

        then:
        trigger != null
        trigger.attributes()['class'] == 'vector'
        root.triggers.size() == 1
        root.triggers[0] == trigger

    }

    def 'lookup up nodes with attribute with attributes'() {
        when:
        Node trigger = execute { it / triggers(class: 'vector') }

        then:
        trigger != null
        trigger.attributes()['class'] == 'vector'
        root.triggers.size() == 1
        root.triggers[0] == trigger
    }

    def 'lookup up nodes with attribute with different attributes'() {
        when:
        Node trigger = execute { it / triggers(class: 'arraylist') }

        then:
        trigger != null
        trigger.attributes()['class'] == 'arraylist'
        root.triggers.size() == 2
        root.triggers[1] == trigger
        root.triggers[0].attributes()['class'] == 'vector'
    }

    void confirmBrowserUrl(Node scmNode) {
        assert scmNode.browser.size() == 1
        assert scmNode.browser[0].attributes()['class'] == 'hudson.plugins.git.browser.GithubWeb'
        assert scmNode.browser[0].url.size() == 1
        assert scmNode.browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin'
    }

    def 'Setup git browser node'() {
        when:
        execute { project ->
            // Find irrelevant of existing attributes
            def browserNode = project / scm / browser

            // Overwrite old value
            browserNode.attributes()['class'] = 'hudson.plugins.git.browser.GithubWeb'

            browserNode / url << 'https://github.com/jenkinsci/job-dsl-plugin'
        }

        then:
        confirmBrowserUrl(root.scm[0])
    }

    def 'configuring nodes with attributes purely nested'() {
        when:
        execute {
            it / scm / browser(class: 'hudson.plugins.git.browser.GithubWeb') / url('https://github.com/jenkinsci/job-dsl-plugin')
        }

        then:
        confirmBrowserUrl(root.scm[0])

        when: // Do it again with the same values, but hopefully the same thing
        execute {
            it / scm / browser(class: 'hudson.plugins.git.browser.GithubWeb') / url << 'https://github.com/jenkinsci/job-dsl-plugin'
        }

        then:
        confirmBrowserUrl(root.scm[0])

        when: // Do it again with different values, but hopefully the same thing
        execute {
            it / scm / browser(class: 'hudson.plugins.git.browser.GitoriusWeb') / url << 'https://github.com/javaposse/job-dsl-plugin'
        }

        then:
        def scmNode = root.scm[0]
        assert scmNode.browser.size() == 2
        assert scmNode.browser[0].attributes()['class'] == 'hudson.plugins.git.browser.GithubWeb'
        assert scmNode.browser[0].url.size() == 1
        assert scmNode.browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin'
        assert scmNode.browser[1].attributes()['class'] == 'hudson.plugins.git.browser.GitoriusWeb'
        assert scmNode.browser[1].url.size() == 1
        assert scmNode.browser[1].url[0].value() == 'https://github.com/javaposse/job-dsl-plugin'

    }
}
