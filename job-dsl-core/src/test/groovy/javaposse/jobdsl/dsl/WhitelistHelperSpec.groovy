package javaposse.jobdsl.dsl

import spock.lang.Specification

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

/**
 * Testing the whitlelisting feature provided
 */
class WhitelistHelperSpec extends Specification {
    static final String LOTS_OF_CHILDREN = '''<?xml version="1.0" encoding="UTF-8"?>
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
   <triggers class="vector">
        <hudson.triggers.SCMTrigger>
            <spec>H/15 * * * *</spec>
            <ignorePostCommitHooks>false</ignorePostCommitHooks>
        </hudson.triggers.SCMTrigger>
    </triggers>
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

    static final String TRIGGER_AT_WRONG_LEVEL = '''<?xml version="1.0" encoding="UTF-8"?>
<project>
     <hudson.triggers.SCMTrigger>
            <spec>H/15 * * * *</spec>
            <ignorePostCommitHooks>false</ignorePostCommitHooks>
        </hudson.triggers.SCMTrigger>
</project>
'''

    static final String ONLY_TRIGGERS = '''<?xml version="1.0" encoding="UTF-8"?>
<project>
     <triggers />
</project>
'''

    static final Closure CUSTOM_WORKSPACE_CLOSURE = { Node project ->
        Node node = methodMissing('customWorkspace', workspacePath)
        project / node
    }

    final Node lotsOfChildren = new XmlParser().parse(new StringReader(LOTS_OF_CHILDREN))
    final Node noProjectChildren = new XmlParser().parse(new StringReader(EMPTY_PROJECT_XML))
    final Node onlyTriggersChild = new XmlParser().parse(new StringReader(ONLY_TRIGGERS))
    final Node triggerAtWrongLevel = new XmlParser().parse(new StringReader(TRIGGER_AT_WRONG_LEVEL))

    def setup() {
        Logger.getLogger('javaposse.jobdsl').setLevel(Level.ALL)

        // Our only choice to allow lower logging is to allow it for everyone since JUL puts the handler in one place
        // and sets a horrible default
        LogManager.logManager.getLogger('').handlers.each { Handler handler ->
            handler.setLevel(Level.ALL)
        }
    }

    def 'node valid for whitelist with no children'() {
        when:
        WhitelistHelper.verifyNode(lotsOfChildren, noProjectChildren)

        then:
        noExceptionThrown()
    }

    def 'node not valid if whitelist does not include'() {
        when:
        WhitelistHelper.verifyNodeChildren(lotsOfChildren, onlyTriggersChild)

        then:
        thrown(DslScriptException)
    }

    def 'node valid if whitelist includes'() {
        when:
        WhitelistHelper.verifyNodeChildren(onlyTriggersChild, lotsOfChildren)

        then:
        noExceptionThrown()
    }

    def 'node not valid if whitelist includes but at wrong level'() {
        when:
        WhitelistHelper.verifyNodeChildren(triggerAtWrongLevel, lotsOfChildren)

        then:
        thrown(DslScriptException)
    }

    def 'configure block not valid if whitelist does not include'() {
        when:
        WhitelistHelper.verifyRawJobDsl({ Node project ->
            Node node = methodMissing('customWorkspace', workspacePath)
            project / node
        }, lotsOfChildren)

        then:
        thrown(DslScriptException)
    }
}
