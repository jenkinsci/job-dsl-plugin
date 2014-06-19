package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

class ScmHelperSpec extends Specification {

    private static final String GIT_REPO_URL = 'git://github.com/Netflix/curator.git'
    private static final String HG_REPO_URL = 'http://selenic.com/repo/hello'

    List<WithXmlAction> mockActions = Mock()
    JobManagement mockJobManagement = Mock(JobManagement)
    ScmContextHelper helper = new ScmContextHelper(mockActions, JobType.Freeform, mockJobManagement)
    ScmContext context = new ScmContext(false, [], mockJobManagement)
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.XML))

    def 'base hg configuration'() {
        when:
        context.hg(HG_REPO_URL)

        then:
        context.scmNode != null
        context.scmNode.source[0].text() == HG_REPO_URL
        context.scmNode.modules[0].text() == ''
    }

    def 'hg with branch'() {
        String branch = 'not-default'

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

    def 'call git scm with two remotes'() {
        when:
        context.git {
            remote {
                name('origin')
                url('https://github.com/jenkinsci/jenkins.git')
                refspec('+refs/heads/master:refs/remotes/origin/master')
            }
            remote {
                name('other')
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
                refspec('+refs/heads/master:refs/remotes/other/master')
            }
        }

        then:
        context.scmNode != null
        context.scmNode.userRemoteConfigs.size() == 1
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'.size() == 2
        with(context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0]) {
            name[0].text() == 'origin'
            url[0].text() == 'https://github.com/jenkinsci/jenkins.git'
            refspec[0].text() == '+refs/heads/master:refs/remotes/origin/master'
        }
        with(context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[1]) {
            name[0].text() == 'other'
            url[0].text() == 'https://github.com/jenkinsci/job-dsl-plugin.git'
            refspec[0].text() == '+refs/heads/master:refs/remotes/other/master'
        }
    }

    def 'call git scm with relativeTargetDir'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            relativeTargetDir('checkout')
        }

        then:
        context.scmNode != null
        context.scmNode.relativeTargetDir.size() == 1
        context.scmNode.relativeTargetDir[0].text() == 'checkout'
    }

    def 'call git scm with second relativeTargetDirs'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            relativeTargetDir('ignored')
            relativeTargetDir('checkout')
        }

        then:
        context.scmNode != null
        context.scmNode.relativeTargetDir.size() == 1
        context.scmNode.relativeTargetDir[0].text() == 'checkout'
    }

    def 'call git scm with reference'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            reference('/foo/bar')
        }

        then:
        context.scmNode != null
        context.scmNode.reference.size() == 1
        context.scmNode.reference[0].text() == '/foo/bar'
    }

    def 'call git scm with second reference'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            reference('/foo/bar')
            reference('/foo/baz')
        }

        then:
        context.scmNode != null
        context.scmNode.reference.size() == 1
        context.scmNode.reference[0].text() == '/foo/baz'
    }

    def 'call git scm with shallowClone'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            shallowClone(true)
        }

        then:
        context.scmNode != null
        context.scmNode.useShallowClone.size() == 1
        context.scmNode.useShallowClone[0].text() == 'true'
    }

    def 'call git scm with shallowClone, no argument'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            shallowClone()
        }

        then:
        context.scmNode != null
        context.scmNode.useShallowClone.size() == 1
        context.scmNode.useShallowClone[0].text() == 'true'
    }

    def 'call git scm with second shallowClone'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            shallowClone(false)
            shallowClone(true)
        }

        then:
        context.scmNode != null
        context.scmNode.useShallowClone.size() == 1
        context.scmNode.useShallowClone[0].text() == 'true'
    }

    def 'call git scm with pruneBranches'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            pruneBranches(true)
        }

        then:
        context.scmNode != null
        context.scmNode.pruneBranches.size() == 1
        context.scmNode.pruneBranches[0].text() == 'true'
    }

    def 'call git scm with pruneBranches, no argument'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            pruneBranches()
        }

        then:
        context.scmNode != null
        context.scmNode.pruneBranches.size() == 1
        context.scmNode.pruneBranches[0].text() == 'true'
    }

    def 'call git scm with createTag'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            createTag(true)
        }

        then:
        context.scmNode != null
        context.scmNode.skipTag.size() == 1
        context.scmNode.skipTag[0].text() == 'false'
    }

    def 'call git scm with skipTag, no argument'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            createTag()
        }

        then:
        context.scmNode != null
        context.scmNode.skipTag.size() == 1
        context.scmNode.skipTag[0].text() == 'false'
    }

    def 'call git scm with second skipTag'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            createTag(false)
            createTag(true)
        }

        then:
        context.scmNode != null
        context.scmNode.skipTag.size() == 1
        context.scmNode.skipTag[0].text() == 'false'
    }

    def 'call git scm with clean'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            clean(true)
        }

        then:
        context.scmNode != null
        context.scmNode.clean.size() == 1
        context.scmNode.clean[0].text() == 'true'
    }

    def 'call git scm with clean, no argument'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            clean()
        }

        then:
        context.scmNode != null
        context.scmNode.clean.size() == 1
        context.scmNode.clean[0].text() == 'true'
    }

    def 'call git scm with second clean'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            clean(false)
            clean(true)
        }

        then:
        context.scmNode != null
        context.scmNode.clean.size() == 1
        context.scmNode.clean[0].text() == 'true'
    }

    def 'call git scm with wipeOutWorkspace'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            wipeOutWorkspace(true)
        }

        then:
        context.scmNode != null
        context.scmNode.wipeOutWorkspace.size() == 1
        context.scmNode.wipeOutWorkspace[0].text() == 'true'
    }

    def 'call git scm with wipeOutWorkspace, no argument'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            wipeOutWorkspace()
        }

        then:
        context.scmNode != null
        context.scmNode.wipeOutWorkspace.size() == 1
        context.scmNode.wipeOutWorkspace[0].text() == 'true'
    }

    def 'call git scm with second wipeOutWorkspace'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            wipeOutWorkspace(false)
            wipeOutWorkspace(true)
        }

        then:
        context.scmNode != null
        context.scmNode.wipeOutWorkspace.size() == 1
        context.scmNode.wipeOutWorkspace[0].text() == 'true'
    }

    def 'call git scm with remotePoll'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            remotePoll(true)
        }

        then:
        context.scmNode != null
        context.scmNode.remotePoll.size() == 1
        context.scmNode.remotePoll[0].text() == 'true'
    }

    def 'call git scm with remotePoll, no argument'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            remotePoll()
        }

        then:
        context.scmNode != null
        context.scmNode.remotePoll.size() == 1
        context.scmNode.remotePoll[0].text() == 'true'
    }

    def 'call git scm with second remotePoll'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            remotePoll(false)
            remotePoll(true)
        }

        then:
        context.scmNode != null
        context.scmNode.remotePoll.size() == 1
        context.scmNode.remotePoll[0].text() == 'true'
    }

    def 'call git scm with no branch'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
        }

        then:
        context.scmNode != null
        context.scmNode.branches.size() == 1
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'.size() == 1
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].text() == '**'
    }

    def 'call git scm with multiple branches'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            branch('foo')
            branches('bar', 'test')
        }

        then:
        context.scmNode != null
        context.scmNode.branches.size() == 1
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'.size() == 3
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].text() == 'foo'
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[1].name[0].text() == 'bar'
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[2].name[0].text() == 'test'
    }

    def 'call git scm with mergeOptions'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            mergeOptions('acme-plugin')
        }

        then:
        context.scmNode != null
        context.scmNode.userMergeOptions.size() == 1
        context.scmNode.userMergeOptions[0].mergeRemote.size() == 1
        context.scmNode.userMergeOptions[0].mergeRemote[0].text() == ''
        context.scmNode.userMergeOptions[0].mergeTarget.size() == 1
        context.scmNode.userMergeOptions[0].mergeTarget[0].text() == 'acme-plugin'
    }

    def 'call git scm with second mergeOptions'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            mergeOptions('ignored')
            mergeOptions('acme-plugin')
        }

        then:
        context.scmNode != null
        context.scmNode.userMergeOptions.size() == 1
        context.scmNode.userMergeOptions[0].mergeRemote.size() == 1
        context.scmNode.userMergeOptions[0].mergeRemote[0].text() == ''
        context.scmNode.userMergeOptions[0].mergeTarget.size() == 1
        context.scmNode.userMergeOptions[0].mergeTarget[0].text() == 'acme-plugin'
    }

    def 'call git scm with complex mergeOptions'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            remote {
                name('other')
                url('https://github.com/daspilker/job-dsl-plugin.git')
            }
            mergeOptions('other', 'acme-plugin')
        }

        then:
        context.scmNode != null
        context.scmNode.userMergeOptions.size() == 1
        context.scmNode.userMergeOptions[0].mergeRemote.size() == 1
        context.scmNode.userMergeOptions[0].mergeRemote[0].text() == 'other'
        context.scmNode.userMergeOptions[0].mergeTarget.size() == 1
        context.scmNode.userMergeOptions[0].mergeTarget[0].text() == 'acme-plugin'
    }

    def 'call git scm with credentials'() {
        setup:
        mockJobManagement.getCredentialsId('ci-user') >> '0815'

        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
                credentials('ci-user')
            }
        }

        then:
        context.scmNode != null
        context.scmNode.userRemoteConfigs.size() == 1
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'.size() == 1
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].credentialsId[0].text() == '0815'
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
            gitNode.appendNode('gitConfigEmail', 'john@gmail.com') // Clearly an append
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
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() ==
                'https://github.com/jenkinsci/job-dsl-plugin.git'
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == '**'
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNode.browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        context.withXmlActions.size() == 1

        when:
        context.withXmlActions[0].execute(root)

        then:
        root.'properties'[0].'com.coravy.hudson.plugins.github.GithubProjectProperty'[0].projectUrl[0].value() ==
                'https://github.com/jenkinsci/job-dsl-plugin/'
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
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() ==
                'git@github.com:jenkinsci/job-dsl-plugin.git'
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNode.browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        context.withXmlActions.size() == 1

        when:
        context.withXmlActions[0].execute(root)

        then:
        root.'properties'[0].'com.coravy.hudson.plugins.github.GithubProjectProperty'[0].projectUrl[0].value() ==
                'https://github.com/jenkinsci/job-dsl-plugin/'
    }

    def 'call github scm method with git protocol'() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master', 'git')

        then:
        context.scmNode != null
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() ==
                'git://github.com/jenkinsci/job-dsl-plugin.git'
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNode.browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        context.withXmlActions.size() == 1

        when:
        context.withXmlActions[0].execute(root)

        then:
        root.'properties'[0].'com.coravy.hudson.plugins.github.GithubProjectProperty'[0].projectUrl[0].value() ==
                'https://github.com/jenkinsci/job-dsl-plugin/'
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
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() ==
                'git@github.acme.com:jenkinsci/job-dsl-plugin.git'
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNode.browser[0].url[0].value() == 'https://github.acme.com/jenkinsci/job-dsl-plugin/'
        context.withXmlActions.size() == 1

        when:
        context.withXmlActions[0].execute(root)

        then:
        root.'properties'[0].'com.coravy.hudson.plugins.github.GithubProjectProperty'[0].projectUrl[0].value() ==
                'https://github.acme.com/jenkinsci/job-dsl-plugin/'
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
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() ==
                'git@github.com:jenkinsci/job-dsl-plugin.git'
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
        context.scmNode.userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() ==
                'git@github.acme.com:jenkinsci/job-dsl-plugin.git'
        context.scmNode.branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNode.browser[0].url[0].value() == 'https://github.acme.com/jenkinsci/job-dsl-plugin/'
        context.scmNode.gitConfigName.size() == 1
        context.scmNode.gitConfigName[0].text() == 'john'
        context.withXmlActions.size() == 1

        when:
        context.withXmlActions[0].execute(root)

        then:
        root.'properties'[0].'com.coravy.hudson.plugins.github.GithubProjectProperty'[0].projectUrl[0].value() ==
                'https://github.acme.com/jenkinsci/job-dsl-plugin/'
    }

    def 'call svn'() {
        when:
        context.svn('http://svn.apache.org/repos/asf/xml/crimson/trunk/') { svnNode ->
            svnNode / excludedRegions << '/trunk/.*'
        }

        then:
        context.scmNode != null
        context.scmNode.attributes()['class'] == 'hudson.scm.SubversionSCM'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() ==
                'http://svn.apache.org/repos/asf/xml/crimson/trunk/'
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
            svnNode / browser(class: 'hudson.scm.browsers.FishEyeSVN') {
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
            svnNode / browser(class: 'hudson.scm.browsers.ViewSVN') / url << 'http://mycompany.com/viewsvn/repo_name'
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
        Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.XML))
        def nodeBuilder = new NodeBuilder()

        Node scmNode = nodeBuilder.scm(class: 'hudson.plugins.git.GitSCM') {
            wipeOutWorkspace 'true'
        }

        when:
        def withXmlAction = helper.generateWithXmlAction(new ScmContext(scmNode))
        withXmlAction.execute(root)

        then:
        root.scm[0].wipeOutWorkspace[0].text() == 'true'
    }

    def 'call cloneWorkspace'(parentJob, criteria) {
        when:
        context.cloneWorkspace(parentJob, criteria)

        then:
        context.scmNode.parentJobName.text() == parentJob
        context.scmNode.criteria.text() == criteria

        where:
        parentJob | criteria
        'parent'  | 'Any'
        'some'    | 'Successful'
    }

    def 'call baseClearCase with default configuration'() {
        when:
        context.baseClearCase()

        then:
        context.scmNode != null
        with(context.scmNode) {
            attributes()['class'] == 'hudson.plugins.clearcase.ClearCaseSCM'
            changeset[0].value() == 'BRANCH'
            createDynView[0].value() == false
            excludedRegions[0].value() == ''
            extractLoadRules[0].value() == false
            filteringOutDestroySubBranchEvent[0].value() == false
            freezeCode[0].value() == false
            loadRules[0].value() == ''
            loadRulesForPolling[0].value() == ''
            mkviewOptionalParam[0].value() == ''
            multiSitePollBuffer[0].value() == 0
            recreateView[0].value() == false
            removeViewOnRename[0].value() == false
            useDynamicView[0].value() == false
            useOtherLoadRulesForPolling[0].value() == false
            useUpdate[0].value() == true
            viewDrive[0].value() == '/view'
            viewName[0].value() == 'Jenkins_${USER_NAME}_${NODE_NAME}_${JOB_NAME}${DASH_WORKSPACE_NUMBER}'
            viewPath[0].value() == 'view'
            branch[0].value() == ''
            configSpec[0].value() == ''
            configSpecFileName[0].value() == ''
            doNotUpdateConfigSpec[0].value() == false
            extractConfigSpec[0].value() == false
            label[0].value() == ''
            refreshConfigSpec[0].value() == false
            refreshConfigSpecCommand[0].value() == ''
            useTimeRule[0].value() == false
        }
    }

    def 'call baseClearCase with all configuration parameters'() {
        when:
        context.baseClearCase {
            configSpec('element .../foo1/... /main/LATEST\nelement .../bar1/... /main/LATEST')
            configSpec('element .../foo2/... /main/LATEST', 'element .../bar2/... /main/LATEST')
            loadRules('/vobs/foo1\n/vobs/bar1')
            loadRules('/vobs/foo2', '/vobs/bar2')
            mkviewOptionalParameter('foo1\nbar1')
            mkviewOptionalParameter('foo2', 'bar2')
            viewName('Jenkins_${USER_NAME}_${JOB_NAME}${DASH_WORKSPACE_NUMBER}')
            viewPath('views')
        }

        then:
        context.scmNode != null
        with(context.scmNode) {
            attributes()['class'] == 'hudson.plugins.clearcase.ClearCaseSCM'
            changeset[0].value() == 'BRANCH'
            createDynView[0].value() == false
            excludedRegions[0].value() == ''
            extractLoadRules[0].value() == false
            filteringOutDestroySubBranchEvent[0].value() == false
            freezeCode[0].value() == false
            loadRules[0].value() == '/vobs/foo1\n/vobs/bar1\n/vobs/foo2\n/vobs/bar2'
            loadRulesForPolling[0].value() == ''
            mkviewOptionalParam[0].value() == 'foo1\nbar1\nfoo2\nbar2'
            multiSitePollBuffer[0].value() == 0
            recreateView[0].value() == false
            removeViewOnRename[0].value() == false
            useDynamicView[0].value() == false
            useOtherLoadRulesForPolling[0].value() == false
            useUpdate[0].value() == true
            viewDrive[0].value() == '/view'
            viewName[0].value() == 'Jenkins_${USER_NAME}_${JOB_NAME}${DASH_WORKSPACE_NUMBER}'
            viewPath[0].value() == 'views'
            branch[0].value() == ''
            configSpec[0].value() == 'element .../foo1/... /main/LATEST\nelement .../bar1/... /main/LATEST\n' +
                    'element .../foo2/... /main/LATEST\nelement .../bar2/... /main/LATEST'
            configSpecFileName[0].value() == ''
            doNotUpdateConfigSpec[0].value() == false
            extractConfigSpec[0].value() == false
            label[0].value() == ''
            refreshConfigSpec[0].value() == false
            refreshConfigSpecCommand[0].value() == ''
            useTimeRule[0].value() == false
        }
    }
}
