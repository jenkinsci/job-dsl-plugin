package javaposse.jobdsl.dsl

import spock.lang.Specification

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertFalse

/**
 * Testing the whitlelisting feature provided
 */
class WhitelistContextHelperSpec extends Specification {
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

    static final String EMPTY_PROJECT_XML = '''<?xml version="1.0" encoding="UTF-8"?>
<project>
</project>
'''

    static final String ONLY_TRIGGERS_ALLOWED = '''<?xml version="1.0" encoding="UTF-8"?>
<project>
     <triggers />
</project>
'''

    final Node node = new XmlParser().parse(new StringReader(XML))
    final Node emptyNode = new XmlParser().parse(new StringReader(EMPTY_PROJECT_XML))
    final Node triggersWhitelistNode = new XmlParser().parse(new StringReader(ONLY_TRIGGERS_ALLOWED))

    def setup() {
        Logger.getLogger('javaposse.jobdsl').setLevel(Level.ALL)

        // Our only choice to allow lower logging is to allow it for everyone since JUL puts the handler in one place
        // and sets a horrible default
        LogManager.logManager.getLogger('').handlers.each { Handler handler ->
            handler.setLevel(Level.ALL)
        }
    }

    def 'verify if whitelist only includes project all nodes returns true'() {
        when:
        def answer = WhitelistContextHelper.verifyNode(node, emptyNode)

        then:
        noExceptionThrown()
        assertTrue(answer)
    }

    def 'verify if whitelist only includes triggers node is not verified'() {
        when:
        def answer = true
        node.children().each {
            if (it instanceof Node) {
                Node childNode = ((Node) it)
                if(!WhitelistContextHelper.verifyNode(childNode, triggersWhitelistNode)) {
                    answer = false
                }
            }
        }

        then:
        noExceptionThrown()
        assertFalse(answer)
    }
}
