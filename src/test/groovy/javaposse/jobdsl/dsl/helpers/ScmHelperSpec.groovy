package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import javaposse.jobdsl.dsl.helpers.ScmHelper.ScmContext
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
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'

    }

    def 'call git scm with branch'() {
        when:
        context.git(GIT_REPO_URL, 'feature-branch')

        then:
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'feature-branch'

    }

    def 'call git scm with configure'() {
        when:
        context.git(GIT_REPO_URL, null) { gitNode ->
            gitNode << gitConfigName('john')
            gitNode.appendNode('gitConfigEmail' ,'john@gmail.com')
        }

        then:
        context.scmNode.gitConfigName[0].text() == 'john'
        context.scmNode.gitConfigEmail[0].text() == 'john@gmail.com'
    }

    def 'call scm via helper'() {
        when:
        helper.scm {
            git(GIT_REPO_URL)
        }

        then:
        1 * mockActions.add(_)

        // TODO Support this notation
//        when:
//        helper.scm.git('git://github.com/Netflix/curator.git')
//
//        then:
//        1 * mockActions.add(_)
    }

    // TODO Test Configure Block

    def 'execute withXml Action'() {
        Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.xml))
        def nodeBuilder = new NodeBuilder()

        Node scmNode = nodeBuilder.scm(class:'hudson.plugins.git.GitSCM') {
            wipeOutWorkspace 'true'
        }

        when:
        def withXmlAction = helper.generateWithXmlAction( new ScmContext(scmNode))
        withXmlAction.execute(root)

        then:
        root.scm[0].wipeOutWorkspace[0].text() == 'true'
    }
}
