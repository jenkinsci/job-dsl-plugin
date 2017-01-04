package javaposse.jobdsl.dsl

import spock.lang.Specification

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

/**
 * Created by G537597 on 1/4/2017.
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

    static final String EMPTY_XML = '''<?xml version="1.0" encoding="UTF-8"?>
<project>
</project>
'''

    final Node node = new XmlParser().parse(new StringReader(XML))
    final Node emptyNode = new XmlParser().parse(new StringReader(EMPTY_XML))

    def setup() {
        Logger.getLogger('javaposse.jobdsl').setLevel(Level.ALL)

        // Our only choice to allow lower logging is to allow it for everyone since JUL puts the handler in one place
        // and sets a horrible default
        LogManager.logManager.getLogger('').handlers.each { Handler handler ->
            handler.setLevel(Level.ALL)
        }
    }

    def 'verify empty parent node returns true'() {
        when:
        WhitelistContextHelper.verifyNode(node, emptyNode)

        then:
        noExceptionThrown()
    }
}
