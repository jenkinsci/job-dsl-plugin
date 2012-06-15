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
    def xml = """<?xml version='1.0' encoding='UTF-8'?>
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

    def printNode(Node n) {
        def writer = new StringWriter()
        new XmlNodePrinter(new PrintWriter(writer)).print(n)
        println writer.toString()
    }

    def 'lookup with existent nodes'() {
        setup:
        def builders = "build"
        def withXmlAction = new WithXmlAction ({ project ->
            Preconditions.checkNotNull(project)
            Preconditions.checkArgument(project instanceof Node)
             println "About to reference! ${owner} ${delegate}"

            def matrix = project / builders / builder
        })

        when:
        withXmlAction.execute(root)

        then:
        noExceptionThrown()
    }

    def 'lookup with non-existent nodes'() {
        setup:
        def withXmlAction = new WithXmlAction ({ project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
        })

        when:
        withXmlAction.execute(root)

        then:
        noExceptionThrown()
    }

    def 'add single child in left shift closure'() {
        setup:
        def withXmlAction = new WithXmlAction ({ project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix << permission('hudson.model.Item.Delete:jryan')
        })

        when:
        printNode(root)
        withXmlAction.execute(root)
        printNode(root)

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
        setup:
        def withXmlAction = new WithXmlAction ({ project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix << permission('hudson.model.Item.Delete:jryan')
            matrix << permission('hudson.model.Item.Configure:jryan')
        })

        when:
        withXmlAction.execute(root)

        then:
        NodeList permissions = root.builders[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 2
        permissions[0].text() == 'hudson.model.Item.Delete:jryan'
        permissions[1].text() == 'hudson.model.Item.Configure:jryan'
    }

    def 'use chained left shift'() {
        setup:
        def withXmlAction = new WithXmlAction ({ project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix << {
                permission('hudson.model.Item.Configure:jryan')
            } << {
                permission('hudson.model.Item.Configure:jack')
            }
        })

        when:
        withXmlAction.execute(root)

        then:
        NodeList permissions = root.builders[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 2
        permissions[0].text() == 'hudson.model.Item.Configure:jryan'
        permissions[1].text() == 'hudson.model.Item.Configure:jack'
    }

    def 'left shift chained with non-closures'() {
        setup:
        def withXmlAction = new WithXmlAction ({ project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix << permission('hudson.model.Item.Configure:jryan') << permission('hudson.model.Item.Configure:jack')
        })

        when:
        withXmlAction.execute(root)

        then:
        NodeList permissions = root.builders[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 2
        permissions[0].text() == 'hudson.model.Item.Configure:jryan'
        permissions[1].text() == 'hudson.model.Item.Configure:jack'
    }

    def 'left shift with multiple children in closure'() {
        setup:
        def withXmlAction = new WithXmlAction ({ project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix << {
                permission('hudson.model.Item.Configure:jill')
                permission('hudson.model.Item.Configure:jack')
            }
        })

        when:
        withXmlAction.execute(root)

        then:
        NodeList permissions = root.builders[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 2
        permissions[0].text() == 'hudson.model.Item.Configure:jill'
        permissions[1].text() == 'hudson.model.Item.Configure:jack'
    }

    def 'left shift with children having children'() {
        setup:
        def withXmlAction = new WithXmlAction ({ project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix << {
                permission('hudson.model.Item.Configure:jill') {
                    header('My Perm')
                }
                permission('hudson.model.Item.Configure:jack')
            }
        })

        when:
        withXmlAction.execute(root)

        then:
        NodeList permissions = root.builders[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 2
        permissions[0].text() == 'hudson.model.Item.Configure:jill'
        permissions[0].header.size() == 1
        permissions[0].header[0].text() == 'My Perm'
        permissions[1].text() == 'hudson.model.Item.Configure:jack'
    }

    def 'children with children without closure'() {
        setup:
        def withXmlAction = new WithXmlAction ({ project ->
            def matrix = project / builders / 'hudson.security.AuthorizationMatrixProperty'
            matrix <<
                    permissions {
                        header('My document')
                    }
        })

        when:
        withXmlAction.execute(root)

        then:
        NodeList permissions = root.builders[0].'hudson.security.AuthorizationMatrixProperty'[0].permissions
        permissions.size() == 1
        permissions[0].header.size() == 1
        permissions[0].header[0].text() == 'My document'
    }
}
