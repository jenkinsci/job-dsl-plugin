package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

public class MultiScmHelperSpec extends Specification {

    private static final String GIT_REPO_URL = 'git://github.com/Netflix/curator.git'

    List<WithXmlAction> mockActions = Mock()
    JobManagement mockJobManagement = Mock(JobManagement)
    MultiScmContextHelper helper = new MultiScmContextHelper(mockActions, JobType.Freeform, mockJobManagement)
    ScmContext context = new ScmContext(true)

    // Most tests are in ScmHelperSpec

    def 'duplicate scm calls allowed'() {
        when:
        context.git(GIT_REPO_URL)
        context.git(GIT_REPO_URL)

        then:
        noExceptionThrown()
        context.scmNodes.size() == 2
    }


    def 'call scm via helper'() {
        Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.XML))

        when:
        ScmContext createdContext = helper.multiscm {
            git(GIT_REPO_URL, 'branch1')
            git(GIT_REPO_URL, 'branch2')
        }

        then:
        createdContext.multiEnabled == true
        createdContext.scmNodes.size() == 2
        1 * mockActions.add(_)

        when:
        WithXmlAction withXmlAction = helper.generateWithXmlAction(createdContext)
        withXmlAction.execute(root)

        then:
        root.scm.size() == 1 // MultiSCM
        root.scm[0].scms[0].scm.size() == 2 // Both gits
        root.scm[0].scms[0].scm[0].branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'branch1'
        root.scm[0].scms[0].scm[1].branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'branch2'
    }
}
