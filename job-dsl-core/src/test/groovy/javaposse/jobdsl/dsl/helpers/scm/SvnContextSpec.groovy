package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.XmlGeneratorSpecification
import javaposse.jobdsl.dsl.helpers.ScmContext

public class SvnContextSpec extends XmlGeneratorSpecification {

    JobManagement mockJobManagement = Mock(JobManagement)
    ScmContext context = new ScmContext(false, [], mockJobManagement)

    void isValidSvnScmNode(scmNode) {
        assert scmNode != null
        assert scmNode.attributes()['class'] == 'hudson.scm.SubversionSCM'
        assert scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() > 0
    }
        
    def 'call svn with no locations'() {
        when: 'svn is called without specifying any locations'
        context.svn {}
        
        then: 'an IllegalStateException should be thrown'
        thrown(IllegalStateException)
    }
    
    def 'call svn with one location'() {
        when: 'svn is called with a single location'
        context.svn {
            location("url", "dir")
        }

        then: 'the svn node should contain that single location'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() == 1
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'url'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == 'dir'
    }
    
    def 'call svn with multiple locations'() {
        when: 'svn is called with multiple locations'
        context.svn {
            location("url1", "dir1")
            location("url2", "dir2")
            location('url3', 'dir3')
        }

        then: 'the svn node should contain those locations'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() == 3
        
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'url1'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == 'dir1'
        
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[1].remote[0].value() == 'url2'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[1].local[0].value() == 'dir2'
        
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[2].remote[0].value() == 'url3'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[2].local[0].value() == 'dir3'
    }

    def 'call svn without specifying a local dir for the location'() {
        when: 'svn is called without a local dir for the location'
        context.svn {
            location('url')
        }

        then: 'the svn node should contain a location with a local dir of .'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() == 1
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'url'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == '.'
    }

    def 'call svn without checkout strategy'() {
        when: 'svn is called without specifying a checkoutStrategy'
        context.svn {
            location 'url'
        }

        then: 'the default strategy of Update is used'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.workspaceUpdater[0].attributes()['class'] == 'hudson.scm.subversion.UpdateUpdater'
    }

    def 'call svn with checkout strategy'() {
        when: 'svn is called with a checkout strategy'
        context.svn {
            location 'url'
            checkoutStrategy strategy
        }

        then: 'then the workspace updater class for that strategy should be used'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.workspaceUpdater[0].attributes()['class'] == wuClass

        where:
        strategy                          | wuClass
        SvnCheckoutStrategy.Update           | 'hudson.scm.subversion.UpdateUpdater'
        SvnCheckoutStrategy.Checkout         | 'hudson.scm.subversion.CheckoutUpdater'
        SvnCheckoutStrategy.UpdateWithClean  | 'hudson.scm.subversion.UpdateWithCleanUpdater'
        SvnCheckoutStrategy.UpdateWithRevert | 'hudson.scm.subversion.UpdateWithRevertUpdater'
    }

    def 'call svn without excluded regions'() {
        when: 'svn is called without specifying any excluded regions'
        context.svn {
            location 'url'
        }

        then: 'an empty excludedRegions node should be created'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedRegions[0].value() == ''
    }

    def 'call svn with single excluded region'() {
        when: 'svn is called with a single excluded region'
        context.svn {
            location 'url'
            excludedRegion 'exreg'
        }

        then: 'the excludedRegions node should contain the specified region'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedRegions[0].value() == 'exreg'
    }

    def 'call svn with multiple excluded regions'() {
        when: 'svn is called with multiple excluded regions'
        context.svn {
            location 'url'
            excludedRegion 'exreg1'
            excludedRegion 'exreg2'
        }

        then: 'the excludedRegions node should contain the specified regions separated by a newline'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedRegions[0].value() == 'exreg1\nexreg2'
    }

    def 'call svn with a list of excluded regions'() {
        when: 'svn is called with a list of excluded regions'
        context.svn {
            location 'url'
            excludedRegions (['exreg1','exreg2']) // In Groovy, parenthesis are required for
                                                  // zero-argument methods or if the first
                                                  // argument is a list or map.
        }

        then: 'the excludedRegions node should contain the specified regions separated by a newline'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedRegions[0].value() == 'exreg1\nexreg2'
    }

    def 'call svn with a mix of excluded region specifications'() {
        when: 'svn is called with an excluded region and a list of excluded regions'
        context.svn {
            location 'url'
            excludedRegions (['exreg1','exreg2']) // In Groovy, parenthesis are required for
                                                  // zero-argument methods or if the first
                                                  // argument is a list or map.
            excludedRegion 'exreg3'
        }

        then: 'the excludedRegions node should contain the specified regions separated by a newline'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedRegions[0].value() == 'exreg1\nexreg2\nexreg3'
    }

    def 'call svn without included regions'() {
        when: 'svn is called without specifying any included regions'
        context.svn {
            location 'url'
        }

        then: 'an empty includedRegions node should be created'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.includedRegions[0].value() == ''
    }

    def 'call svn with single included region'() {
        when: 'svn is called with a single included region'
        context.svn {
            location 'url'
            includedRegion 'increg'
        }

        then: 'the includedRegions node should contain the specified region'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.includedRegions[0].value() == 'increg'
    }

    def 'call svn with multiple included regions'() {
        when: 'svn is called with multiple included regions'
        context.svn {
            location 'url'
            includedRegion 'increg1'
            includedRegion 'increg2'
        }

        then: 'the includedRegions node should contain the specified regions separated by a newline'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.includedRegions[0].value() == 'increg1\nincreg2'
    }

    def 'call svn with a list of included regions'() {
        when: 'svn is called with a list of included regions'
        context.svn {
            location 'url'
            includedRegions (['increg1','increg2']) // In Groovy, parenthesis are required for
                                                    // zero-argument methods or if the first
                                                    // argument is a list or map.
        }

        then: 'the includedRegions node should contain the specified regions separated by a newline'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.includedRegions[0].value() == 'increg1\nincreg2'
    }

    def 'call svn with a mix of included region specifications'() {
        when: 'svn is called with an included region and a list of included regions'
        context.svn {
            location 'url'
            includedRegions (['increg1','increg2']) // In Groovy, parenthesis are required for
                                                    // zero-argument methods or if the first
                                                    // argument is a list or map.
            includedRegion 'increg3'
        }

        then: 'the includedRegions node should contain the specified regions separated by a newline'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.includedRegions[0].value() == 'increg1\nincreg2\nincreg3'
    }

    def 'call svn without excluded users'() {
        when: 'svn is called without specifying any excluded users'
        context.svn {
            location 'url'
        }

        then: 'an empty excludedUsers node should be created'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedUsers[0].value() == ''
    }

    def 'call svn with single excluded user'() {
        when: 'svn is called with a single excluded user'
        context.svn {
            location 'url'
            excludedUser 'user'
        }

        then: 'the excludedUsers node should contain the specified user'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedUsers[0].value() == 'user'
    }

    def 'call svn with multiple excluded users'() {
        when: 'svn is called with multiple excluded users'
        context.svn {
            location 'url'
            excludedUser 'user1'
            excludedUser 'user2'
        }

        then: 'the excludedUsers node should contain the specified users separated by a newline'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedUsers[0].value() == 'user1\nuser2'
    }

    def 'call svn with a list of excluded users'() {
        when: 'svn is called with a list of excluded users'
        context.svn {
            location 'url'
            excludedUsers (['user1','user2']) // In Groovy, parenthesis are required for
                                              // zero-argument methods or if the first
                                              // argument is a list or map.
        }

        then: 'the excludedUsers node should contain the specified users separated by a newline'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedUsers[0].value() == 'user1\nuser2'
    }

    def 'call svn with a mix of excluded user specifications'() {
        when: 'svn is called with an excluded user and a list of excluded users'
        context.svn {
            location 'url'
            excludedUsers (['user1','user2']) // In Groovy, parenthesis are required for
                                              // zero-argument methods or if the first
                                              // argument is a list or map.
            excludedUser 'user3'
        }

        then: 'the excludedUsers node should contain the specified users separated by a newline'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedUsers[0].value() == 'user1\nuser2\nuser3'
    }

    def 'call svn without excluded commit messages'() {
        when: 'svn is called without specifying any excluded commit messages'
        context.svn {
            location 'url'
        }

        then: 'an empty excludedCommitMessages node should be created'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedCommitMessages[0].value() == ''
    }

    def 'call svn with single excluded commit message'() {
        when: 'svn is called with a single excluded commit message'
        context.svn {
            location 'url'
            excludedCommitMsg 'commit'
        }

        then: 'the excludedCommitMessages node should contain the specified commit message'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedCommitMessages[0].value() == 'commit'
    }

    def 'call svn with multiple excluded commit messages'() {
        when: 'svn is called with multiple excluded commit messages'
        context.svn {
            location 'url'
            excludedCommitMsg 'commit1'
            excludedCommitMsg 'commit2'
        }

        then: 'the excludedCommitMessages node should contain the specified commits separated by a newline'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedCommitMessages[0].value() == 'commit1\ncommit2'
    }

    def 'call svn with a list of excluded commit messages'() {
        when: 'svn is called with a list of excluded commit messages'
        context.svn {
            location 'url'
            excludedCommitMsgs (['commit1','commit2']) // In Groovy, parenthesis are required for
                                                       // zero-argument methods or if the first
                                                       // argument is a list or map.
        }

        then: 'the excludedCommitMessages node should contain the specified commits separated by a newline'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedCommitMessages[0].value() == 'commit1\ncommit2'
    }

    def 'call svn with a mix of excluded commit message specifications'() {
        when: 'svn is called with an excluded commit message and a list of excluded commit messages'
        context.svn {
            location 'url'
            excludedCommitMsgs (['commit1','commit2']) // In Groovy, parenthesis are required for
                                                       // zero-argument methods or if the first
                                                       // argument is a list or map.
            excludedCommitMsg 'commit3'
        }

        then: 'the excludedCommitMessages node should contain the specified commits separated by a newline'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedCommitMessages[0].value() == 'commit1\ncommit2\ncommit3'
    }

    def 'call svn without excluded revprop'() {
        when: 'svn is called without specifying an excluded revision property'
        context.svn {
            location 'url'
        }

        then: 'an empty excludedRevprop node should be created'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedRevprop[0].value() == ''
    }

    def 'call svn with an excluded revprop'() {
        when: 'svn is called with an excluded revision property'
        context.svn {
            location 'url'
            excludedRevProp 'revprop'
        }

        then: 'the excludedRevprop node should contain the specified revision property'
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedRevprop[0].value() == 'revprop'
    }

    def 'call svn with configure'() {
        when: 'svn is called with a configure closure'
        context.svn {
            location 'url'
            configure { svnNode ->
                svnNode << testNode('testValue')
            }
        }

        then: 'the svn node should contain changes implemented by the configure closure'
        isValidSvnScmNode(context.scmNode)
        assertXmlEqual('<testNode>testValue</testNode>', context.scmNode.testNode[0])
    }

    def 'call svn with multiple browsers'() {
        when: 'svn is called with multiple browsers specified'
        context.svn {
            browserCollabnetSvn 'http://url'
            browserSvnWeb 'http://url'
        }

        then: 'an IllegalStateException should be thrown'
        thrown(IllegalStateException)
    }

    def getBrowserTestData() {
        [
                [
                        {location 'url'; browserCollabnetSvn 'http://url/'},
                        '<browser class="hudson.scm.browsers.CollabNetSVN"><url>http://url/</url></browser>'
                ],
                [
                        {location 'url'; browserFishEye 'http://url/', 'rootModule'},
                        '<browser class="hudson.scm.browsers.FishEyeSVN"><url>http://url/</url><rootModule>rootModule</rootModule></browser>'
                ],
                [
                        {location 'url'; browserSvnWeb 'http://url/'},
                        '<browser class="hudson.scm.browsers.SVNWeb"><url>http://url/</url></browser>'
                ],
                [
                        {location 'url'; browserSventon 'http://url/', 'repoInstance'},
                        '<browser class="hudson.scm.browsers.Sventon"><url>http://url/</url><repositoryInstance>repoInstance</repositoryInstance></browser>'
                ],
                [
                        {location 'url'; browserSventon2 'http://url/', 'repoInstance'},
                        '<browser class="hudson.scm.browsers.Sventon2"><url>http://url/</url><repositoryInstance>repoInstance</repositoryInstance></browser>'
                ],
                [
                        {location 'url'; browserViewSvn 'http://url/'},
                        '<browser class="hudson.scm.browsers.ViewSVN"><url>http://url/</url></browser>'
                ],
                [
                        {location 'url'; browserWebSvn 'http://url/'},
                        '<browser class="hudson.scm.browsers.WebSVN"><url>http://url/</url></browser>'
                ]
        ]
    }

    def 'call svn with browser'() {
        when: 'svn is called with a CollabNet SVN browser'
        context.svn svnClosure

        then: 'the svn node should contain a CollabNet SVN browser node'
        isValidSvnScmNode(context.scmNode)
        assertXmlEqual(xmlResult, context.scmNode.browser[0])

        where:
        [svnClosure, xmlResult] << getBrowserTestData()

    }

    def 'call legacy svn'() {
        when:
        context.svn('http://svn.apache.org/repos/asf/xml/crimson/trunk/') { svnNode ->
            svnNode / excludedRegions << '/trunk/.*'
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'http://svn.apache.org/repos/asf/xml/crimson/trunk/'
        context.scmNode.excludedRegions.size() == 1
        context.scmNode.excludedRegions[0].value() == '/trunk/.*'
    }

    def 'call legacy svn with remote and local'() {
        when:
        context.svn('http://svn.apache.org/repos/asf/xml/crimson/trunk/', '/mydir/mycode')

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == '/mydir/mycode'
    }
}
