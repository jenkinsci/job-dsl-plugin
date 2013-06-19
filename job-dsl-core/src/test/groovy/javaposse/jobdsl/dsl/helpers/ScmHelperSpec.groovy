package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

public class ScmHelperSpec extends Specification {

    private static final String GIT_REPO_URL = 'git://github.com/Netflix/curator.git'
    private static final String HG_REPO_URL = 'http://selenic.com/repo/hello'

    List<WithXmlAction> mockActions = Mock()
    ScmContextHelper helper = new ScmContextHelper(mockActions, JobType.Freeform)
    ScmContext context = new ScmContext()
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.xml))

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

    def 'call github scm method'() {
        when:
        context.github('jenkinsci/job-dsl-plugin')

        then:
        context.scmNode != null
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin.git'
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == '**'
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNode.browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        context.withXmlActions.size() == 1

        when:
        context.withXmlActions[0].execute(root)

        then:
        root.'properties'[0].'com.coravy.hudson.plugins.github.GithubProjectProperty'[0].projectUrl[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
    }

    def 'call github scm method with branch'() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master')

        then:
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
    }

    def 'call github scm method with ssh protocol'() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master', 'ssh')

        then:
        context.scmNode != null
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() == 'git@github.com:jenkinsci/job-dsl-plugin.git'
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNode.browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        context.withXmlActions.size() == 1

        when:
        context.withXmlActions[0].execute(root)

        then:
        root.'properties'[0].'com.coravy.hudson.plugins.github.GithubProjectProperty'[0].projectUrl[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
    }

    def 'call github scm method with git protocol'() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master', 'git')

        then:
        context.scmNode != null
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() == 'git://github.com/jenkinsci/job-dsl-plugin.git'
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNode.browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        context.withXmlActions.size() == 1

        when:
        context.withXmlActions[0].execute(root)

        then:
        root.'properties'[0].'com.coravy.hudson.plugins.github.GithubProjectProperty'[0].projectUrl[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
    }

    def 'call github scm method with unknown protocol'() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master', 'unknown')

        then:
        thrown(IllegalArgumentException)
    }

    def 'call github scm method with protocol and host'() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master', 'ssh', 'github.acme.com')

        then:
        context.scmNode != null
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() == 'git@github.acme.com:jenkinsci/job-dsl-plugin.git'
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNode.browser[0].url[0].value() == 'https://github.acme.com/jenkinsci/job-dsl-plugin/'
        context.withXmlActions.size() == 1

        when:
        context.withXmlActions[0].execute(root)

        then:
        root.'properties'[0].'com.coravy.hudson.plugins.github.GithubProjectProperty'[0].projectUrl[0].value() == 'https://github.acme.com/jenkinsci/job-dsl-plugin/'
    }

    def 'call github scm with closure'() {
        when:
        context.github('jenkinsci/job-dsl-plugin') { gitNode ->
            gitNode << gitConfigName('john') // Always append
        }

        then:
        context.scmNode.gitConfigName.size() == 1
        context.scmNode.gitConfigName[0].text() == 'john'
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNode.browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
    }

    def 'call github scm with branch and closure'() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master') { gitNode ->
            gitNode << gitConfigName('john') // Always append
        }

        then:
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNode.browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        context.scmNode.gitConfigName.size() == 1
        context.scmNode.gitConfigName[0].text() == 'john'
    }

    def 'call github scm with branch, protocol and closure'() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master', 'ssh') { gitNode ->
            gitNode << gitConfigName('john') // Always append
        }

        then:
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() == 'git@github.com:jenkinsci/job-dsl-plugin.git'
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNode.browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        context.scmNode.gitConfigName.size() == 1
        context.scmNode.gitConfigName[0].text() == 'john'
    }

    def 'call github scm method with protocol, host and closure '() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master', 'ssh', 'github.acme.com') { gitNode ->
            gitNode << gitConfigName('john') // Always append
        }

        then:
        context.scmNode != null
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() == 'git@github.acme.com:jenkinsci/job-dsl-plugin.git'
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNode.browser[0].url[0].value() == 'https://github.acme.com/jenkinsci/job-dsl-plugin/'
        context.scmNode.gitConfigName.size() == 1
        context.scmNode.gitConfigName[0].text() == 'john'
        context.withXmlActions.size() == 1

        when:
        context.withXmlActions[0].execute(root)

        then:
        root.'properties'[0].'com.coravy.hudson.plugins.github.GithubProjectProperty'[0].projectUrl[0].value() == 'https://github.acme.com/jenkinsci/job-dsl-plugin/'
    }

    def void hasValidSvnScmNode(scmNode) {
        assert scmNode != null
        assert scmNode.attributes()['class'] == 'hudson.scm.SubversionSCM'
        assert scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() > 0
    }
        
    def 'call subversion with no locations'() {
        when:
        context.subversion {}
        
        then:
        thrown(IllegalStateException)
    }
    
    def 'call subversion with one location'() {
        when:
        context.subversion {
            location("url", "dir")
        }

        then:
        hasValidSvnScmNode(context.scmNode)
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() == 1
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'url'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == 'dir'
    }
    
    def 'call subversion with multiple locations'() {
        when:
        context.subversion {
            location("url1", "dir1")
            location("url2", "dir2")
            location('url3', 'dir3')
        }

        then:
        hasValidSvnScmNode(context.scmNode)
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() == 3
        
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'url1'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == 'dir1'
        
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[1].remote[0].value() == 'url2'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[1].local[0].value() == 'dir2'
        
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[2].remote[0].value() == 'url3'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[2].local[0].value() == 'dir3'
    }

    def 'call subversion with checkout strategy'() {
        when:
        context.subversion {
            location 'url'
            checkoutStrategy strategy
        }

        then:
        hasValidSvnScmNode(context.scmNode)
        context.scmNode.workspaceUpdater[0].attributes()['class'] == wuClass

        where:
        strategy                          | wuClass
        CheckoutStrategy.Update           | 'hudson.scm.subversion.UpdateUpdater'
        CheckoutStrategy.Checkout         | 'hudson.scm.subversion.CheckoutUpdater'
        CheckoutStrategy.EmulateCheckout  | 'hudson.scm.subversion.UpdateWithCleanUpdater'
        CheckoutStrategy.UpdateWithRevert | 'hudson.scm.subversion.UpdateWithRevertUpdater'
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

    def 'call svn with remote and local'() {
        when:
        context.svn('http://svn.apache.org/repos/asf/xml/crimson/trunk/', '/mydir/mycode')

        then:
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == '/mydir/mycode'
    }

    def 'call svn with browser - Fisheye example'() {
        when:
        context.svn('http://svn.apache.org/repos/asf/xml/crimson/trunk/') { svnNode ->
            svnNode / browser(class:'hudson.scm.browsers.FishEyeSVN') {
                url 'http://mycompany.com/fisheye/repo_name'
                rootModule 'my_root_module'
            }
        }

        then:
        context.scmNode != null
        context.scmNode.browser[0].attributes()['class'] == 'hudson.scm.browsers.FishEyeSVN'
        context.scmNode.browser[0].url[0].value() == 'http://mycompany.com/fisheye/repo_name'
        context.scmNode.browser[0].rootModule[0].value() == 'my_root_module'
    }

    def 'call svn with browser - ViewSVN example'() {
        when:
        context.svn('http://svn.apache.org/repos/asf/xml/crimson/trunk/') { svnNode ->
            svnNode / browser(class:'hudson.scm.browsers.ViewSVN') / url << 'http://mycompany.com/viewsvn/repo_name'
        }

        then:
        context.scmNode != null
        context.scmNode.browser[0].attributes()['class'] == 'hudson.scm.browsers.ViewSVN'
        context.scmNode.browser[0].url[0].value() == 'http://mycompany.com/viewsvn/repo_name'
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
        context.scmNode.p4Passwd[0].value() == '0f0kqlwajkEPwz8Yp+A=' // Using PerforcePasswordEncryptor
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
