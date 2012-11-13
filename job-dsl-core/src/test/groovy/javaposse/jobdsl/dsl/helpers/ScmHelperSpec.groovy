package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

public class ScmHelperSpec extends Specification {

    private static final String GIT_REPO_URL = 'git://github.com/Netflix/curator.git'
    private static final String HG_REPO_URL = 'http://selenic.com/repo/hello'

    List<WithXmlAction> mockActions = Mock()
    ScmHelper helper = new ScmHelper(mockActions)
    ScmContext context = new ScmContext()

    def 'base hg configuration'() {
        when:
        context.hg(HG_REPO_URL)

        then:
        context.scmNode != null
        context.scmNode.source[0].text() == HG_REPO_URL
        context.scmNode.modules[0].text() == ''
    }

    def 'hg with branch'() {
        final String branch = "not-default"

        when:
        context.hg(HG_REPO_URL, branch)

        then:
        context.scmNode.branch[0].text() == branch
    }

    def 'duplicate scm calls disallowed'() {
        when:
        context.git(GIT_REPO_URL)
        context.git(GIT_REPO_URL)

        then:
        thrown(RuntimeException)
    }

    def 'call git scm methods'() {
        when:
        context.git(GIT_REPO_URL)

        then:
        context.scmNode != null
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() == GIT_REPO_URL
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == '**'

    }

    def 'call git scm with branch'() {
        when:
        context.git(GIT_REPO_URL, 'feature-branch')

        then:
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'feature-branch'
    }

    def 'call git scm with configure appending'() {
        when:
        context.git(GIT_REPO_URL, null) { Node gitNode ->
            gitNode << authorOrCommitter('true')
            // gitNode / authorOrCommitter('true') // Always append
            // gitNode.authorOrCommitter('true') // Does not work since there is no method that
            // authorOrCommitter('true') // Does not work since it has no context
            gitNode.appendNode('gitTool', 'NotDefault')
        }

        then:
        context.scmNode.authorOrCommitter.size() == 2
        context.scmNode.authorOrCommitter[0].text() == 'false'
        context.scmNode.authorOrCommitter[1].text() == 'true'
        context.scmNode.gitTool.size() == 2
        context.scmNode.gitTool[0].text() == 'Default'
        context.scmNode.gitTool[1].text() == 'NotDefault'
    }


    def 'call git scm with configure on Node'() {
        when:
        context.git(GIT_REPO_URL, null) { gitNode ->
            gitNode << gitConfigName('john') // Always append
            gitNode.appendNode('gitConfigEmail' ,'john@gmail.com') // Clearly an append
            gitNode / scmName << 'Kittner' // Re-use node and set value
        }

        then:
        context.scmNode.gitConfigName.size() == 1
        context.scmNode.gitConfigName[0].text() == 'john'
        context.scmNode.gitConfigEmail.size() == 1
        context.scmNode.gitConfigEmail[0].text() == 'john@gmail.com'
        context.scmNode.scmName.size() == 1
        context.scmNode.scmName[0].text() == 'Kittner'
    }

    def 'call svn'() {
        when:
        context.svn('http://svn.apache.org/repos/asf/xml/crimson/trunk/') { svnNode ->
            svnNode / excludedRegions << '/trunk/.*'
        }

        then:
        context.scmNode != null
        context.scmNode.attributes()['class'] == 'hudson.scm.SubversionSCM'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'http://svn.apache.org/repos/asf/xml/crimson/trunk/'
        context.scmNode.excludedRegions.size() == 1
        context.scmNode.excludedRegions[0].value() == '/trunk/.*'
    }

    def 'call p4 with all parameters'() {
        setup:
        def viewspec = '//depot/Tools/build/...\n//depot/webapplications/helloworld/...'

        when:
        context.p4(viewspec, 'roleoe', 'secret') { p4Node ->
            p4Node / alwaysForceSync << 'true'
        }

        then:
        context.scmNode != null
        context.scmNode.attributes()['class'] == 'hudson.plugins.perforce.PerforceSCM'
        context.scmNode.p4User[0].value() == 'roleoe'
        context.scmNode.p4Passwd[0].value() == 'secret'
        context.scmNode.p4Port[0].value() == 'perforce:1666'
        context.scmNode.alwaysForceSync.size() == 1 // Double check there's only one
        context.scmNode.alwaysForceSync[0].value() == 'true'
        context.scmNode.projectPath.size() == 1
        context.scmNode.projectPath[0].value().contains('//depot')
    }

    def 'call p4 with few parameters'() {
        setup:
        def viewspec = '//depot/Tools/build/...\n//depot/webapplications/helloworld/...'

        when:
        context.p4(viewspec)

        then:
        context.scmNode != null
        context.scmNode.p4User[0].value() == 'rolem'
        context.scmNode.p4Passwd[0].value() == ''
        context.scmNode.p4Port[0].value() == 'perforce:1666'
        context.scmNode.alwaysForceSync[0].value() == 'false'
        context.scmNode.projectPath.size() == 1
        context.scmNode.projectPath[0].value().contains('//depot')
    }

    def 'call scm via helper'() {
        when:
        helper.scm {
            git(GIT_REPO_URL)
        }

        then:
        1 * mockActions.add(_)
    }

    def 'execute withXml Action'() {
        Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.xml))
        def nodeBuilder = new NodeBuilder()

        Node scmNode = nodeBuilder.scm(class:'hudson.plugins.git.GitSCM') {
            wipeOutWorkspace 'true'
        }

        when:
        def withXmlAction = helper.generateWithXmlAction( new ScmContext(scmNode) )
        withXmlAction.execute(root)

        then:
        root.scm[0].wipeOutWorkspace[0].text() == 'true'
    }
}
