package javaposse.jobdsl.dsl.helpers

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlActionSpec
import javaposse.jobdsl.dsl.helpers.scm.SvnCheckoutStrategy
import javaposse.jobdsl.dsl.helpers.scm.SvnDepth
import spock.lang.Specification
import spock.lang.Unroll

class ScmContextSpec extends Specification {
    private static final String GIT_REPO_URL = 'git://github.com/Netflix/curator.git'
    private static final String HG_REPO_URL = 'http://selenic.com/repo/hello'

    JobManagement mockJobManagement = Mock(JobManagement)
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
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('1.9.3')

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
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('1.9.3')

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

    def 'call git scm with reference, plugin version 2.x'() {
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('2.0.0')

        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            reference('/foo/bar')
        }

        then:
        context.scmNode != null
        with(context.scmNode) {
            reference.size() == 0
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].children().size() == 2
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].reference[0].value() == '/foo/bar'
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].shallow[0].value() == false
        }
    }

    def 'call git scm with shallowClone'() {
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('1.9.3')

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
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('1.9.3')

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
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('1.9.3')

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

    def 'call git scm with shallowClone, plugin version 2.x'() {
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('2.0.0')

        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            shallowClone()
        }

        then:
        context.scmNode != null
        with(context.scmNode) {
            reference.size() == 0
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].children().size() == 2
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].reference[0].value() == ''
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].shallow[0].value() == true
        }
    }

    def 'call git scm with cloneTimeout, plugin version 2.x'() {
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('2.0.0')

        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            cloneTimeout(50)
        }

        then:
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.0.0')
        context.scmNode != null
        with(context.scmNode) {
            reference.size() == 0
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].children().size() == 3
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].reference[0].value() == ''
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].shallow[0].value() == false
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].timeout[0].value() == 50
        }
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

    def 'call git scm with localBranch'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            localBranch('bugfix')
        }

        then:
        context.scmNode != null
        context.scmNode.localBranch.size() == 1
        context.scmNode.localBranch[0].text() == 'bugfix'
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
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('1.9.3')

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
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('1.9.3')

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
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('1.9.3')

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

    def 'call git scm with mergeOptions, plugin version 2.x'() {
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('2.0.0')

        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            mergeOptions('acme-plugin')
        }

        then:
        context.scmNode != null
        context.scmNode.extensions.size() == 1
        context.scmNode.extensions[0].'hudson.plugins.git.extensions.impl.PreBuildMerge'.size() == 1
        with(context.scmNode.extensions[0].'hudson.plugins.git.extensions.impl.PreBuildMerge'[0]) {
            options.size() == 1
            options[0].children().size() == 3
            options[0].mergeRemote.size() == 1
            options[0].mergeRemote[0].text() == ''
            options[0].mergeTarget.size() == 1
            options[0].mergeTarget[0].text() == 'acme-plugin'
            options[0].mergeStrategy.size() == 1
            options[0].mergeStrategy[0].text() == 'default'
        }
    }

    def 'call git scm with second mergeOptions, plugin version 2.x'() {
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('2.0.0')

        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            mergeOptions('testing')
            mergeOptions('acme-plugin')
        }

        then:
        context.scmNode != null
        context.scmNode.extensions.size() == 1
        context.scmNode.extensions[0].'hudson.plugins.git.extensions.impl.PreBuildMerge'.size() == 2
        with(context.scmNode.extensions[0].'hudson.plugins.git.extensions.impl.PreBuildMerge'[0]) {
            options.size() == 1
            options[0].children().size() == 3
            options[0].mergeRemote.size() == 1
            options[0].mergeRemote[0].text() == ''
            options[0].mergeTarget.size() == 1
            options[0].mergeTarget[0].text() == 'testing'
            options[0].mergeStrategy.size() == 1
            options[0].mergeStrategy[0].text() == 'default'
        }
        with(context.scmNode.extensions[0].'hudson.plugins.git.extensions.impl.PreBuildMerge'[1]) {
            options.size() == 1
            options[0].children().size() == 3
            options[0].mergeRemote.size() == 1
            options[0].mergeRemote[0].text() == ''
            options[0].mergeTarget.size() == 1
            options[0].mergeTarget[0].text() == 'acme-plugin'
            options[0].mergeStrategy.size() == 1
            options[0].mergeStrategy[0].text() == 'default'
        }
    }

    def 'call git scm with complex mergeOptions, plugin version 2.x'() {
        setup:
        mockJobManagement.getPluginVersion('git') >> new VersionNumber('2.0.0')

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
        context.scmNode.extensions.size() == 1
        context.scmNode.extensions[0].'hudson.plugins.git.extensions.impl.PreBuildMerge'.size() == 1
        with(context.scmNode.extensions[0].'hudson.plugins.git.extensions.impl.PreBuildMerge'[0]) {
            options.size() == 1
            options[0].children().size() == 3
            options[0].mergeRemote.size() == 1
            options[0].mergeRemote[0].text() == 'other'
            options[0].mergeTarget.size() == 1
            options[0].mergeTarget[0].text() == 'acme-plugin'
            options[0].mergeStrategy.size() == 1
            options[0].mergeStrategy[0].text() == 'default'
        }
    }

    def 'call git scm with inverse build chooser'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            strategy {
                inverse()
            }
        }

        then:
        context.scmNode != null
        context.scmNode.buildChooser.size() == 1
        context.scmNode.buildChooser[0].attribute('class') == 'hudson.plugins.git.util.InverseBuildChooser'
    }

    def 'call git scm with ancestry build chooser'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            strategy {
                ancestry(5, 'sha1')
            }
        }

        then:
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.3.1')
        context.scmNode != null
        context.scmNode.buildChooser.size() == 1
        context.scmNode.buildChooser[0].attribute('class') == 'hudson.plugins.git.util.AncestryBuildChooser'
        context.scmNode.buildChooser[0].children().size() == 2
        context.scmNode.buildChooser[0].maximumAgeInDays[0].text() == '5'
        context.scmNode.buildChooser[0].ancestorCommitSha1[0].text() == 'sha1'
    }

    def 'call git scm with gerrit trigger build chooser'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            strategy {
                gerritTrigger()
            }
        }

        then:
        1 * mockJobManagement.requireMinimumPluginVersion('gerrit-trigger', '2.0')
        context.scmNode != null
        context.scmNode.buildChooser.size() == 1
        context.scmNode.buildChooser[0].attribute('class') ==
                    'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTriggerBuildChooser'
        context.scmNode.buildChooser[0].children().size() == 1
        context.scmNode.buildChooser[0].separator[0].text() == '#'
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

    def 'call git scm with stashBrowser'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            browser {
                stash('http://stash')
            }
        }

        then:
        context.scmNode != null
        context.scmNode.browser.size() == 1
        context.scmNode.browser[0].attribute('class') == 'hudson.plugins.git.browser.Stash'
        context.scmNode.browser[0].'url'[0].value() == 'http://stash'
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

    def 'call svn with no locations'() {
        when:
        context.svn {
        }

        then:
        thrown(IllegalStateException)
    }

    def 'call svn with one location'() {
        when:
        context.svn {
            location('url') {
                directory('dir')
            }
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() == 1
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].children().size() == 3
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'url'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == 'dir'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].depthOption[0].value() == 'infinity'
    }

    def 'call svn with credentials'() {
        setup:
        mockJobManagement.getCredentialsId('foo') >> '4711'

        when:
        context.svn {
            location('url') {
                credentials('foo')
            }
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() == 1
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'url'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == '.'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].credentialsId[0].value() == '4711'
        1 * mockJobManagement.requireMinimumPluginVersion('subversion', '2.0')
    }

    def 'call svn with multiple locations'() {
        when:
        context.svn {
            location('url1')
            location('url2') {
                directory('dir2')
            }
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() == 2
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'url1'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == '.'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[1].remote[0].value() == 'url2'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[1].local[0].value() == 'dir2'
    }

    def 'call svn without specifying a local dir for the location'() {
        when:
        context.svn {
            location('url')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() == 1
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'url'
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == '.'
    }

    @Unroll
    def 'call svn setting the checkout depth to #depth'(SvnDepth depth, String xmlValue) {
        when:
        context.svn {
            location('url') {
                delegate.depth depth
            }
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].depthOption[0].value() == xmlValue

        where:
        depth              || xmlValue
        SvnDepth.EMPTY      | 'empty'
        SvnDepth.AS_IT_IS   | 'unknown'
        SvnDepth.INFINITY   | 'infinity'
        SvnDepth.FILES      | 'files'
        SvnDepth.IMMEDIATES | 'immediates'
    }

    def 'call svn without checkout strategy'() {
        when:
        context.svn {
            location('url')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.workspaceUpdater[0].attributes()['class'] == 'hudson.scm.subversion.UpdateUpdater'
    }

    def 'call svn with checkout strategy'() {
        when:
        context.svn {
            location('url')
            checkoutStrategy(strategy)
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.workspaceUpdater[0].attributes()['class'] == workspaceUpdaterClass

        where:
        strategy                               | workspaceUpdaterClass
        SvnCheckoutStrategy.UPDATE             | 'hudson.scm.subversion.UpdateUpdater'
        SvnCheckoutStrategy.CHECKOUT           | 'hudson.scm.subversion.CheckoutUpdater'
        SvnCheckoutStrategy.UPDATE_WITH_CLEAN  | 'hudson.scm.subversion.UpdateWithCleanUpdater'
        SvnCheckoutStrategy.UPDATE_WITH_REVERT | 'hudson.scm.subversion.UpdateWithRevertUpdater'
    }

    def 'call svn without excluded regions'() {
        when:
        context.svn {
            location('url')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedRegions[0].value() == ''
    }

    def 'call svn with single excluded region'() {
        when:
        context.svn {
            location('url')
            excludedRegions('exreg')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedRegions[0].value() == 'exreg'
    }

    def 'call svn with multiple excluded regions'() {
        when:
        context.svn {
            location('url')
            excludedRegions('exreg1')
            excludedRegions('exreg2')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedRegions[0].value() == 'exreg1\nexreg2'
    }

    def 'call svn with a list of excluded regions'() {
        when:
        context.svn {
            location('url')
            excludedRegions('exreg1', 'exreg2')
            excludedRegions(['exreg3', 'exreg4'])
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedRegions[0].value() == 'exreg1\nexreg2\nexreg3\nexreg4'
    }

    def 'call svn without included regions'() {
        when:
        context.svn {
            location('url')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.includedRegions[0].value() == ''
    }

    def 'call svn with single included region'() {
        when:
        context.svn {
            location('url')
            includedRegions('increg')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.includedRegions[0].value() == 'increg'
    }

    def 'call svn with multiple included regions'() {
        when:
        context.svn {
            location('url')
            includedRegions('increg1')
            includedRegions('increg2')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.includedRegions[0].value() == 'increg1\nincreg2'
    }

    def 'call svn with a list of included regions'() {
        when:
        context.svn {
            location('url')
            includedRegions('increg1', 'increg2')
            includedRegions(['increg3', 'increg4'])
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.includedRegions[0].value() == 'increg1\nincreg2\nincreg3\nincreg4'
    }

    def 'call svn without excluded users'() {
        when:
        context.svn {
            location('url')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedUsers[0].value() == ''
    }

    def 'call svn with single excluded user'() {
        when:
        context.svn {
            location('url')
            excludedUsers('user')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedUsers[0].value() == 'user'
    }

    def 'call svn with multiple excluded users'() {
        when:
        context.svn {
            location('url')
            excludedUsers('user1')
            excludedUsers('user2')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedUsers[0].value() == 'user1\nuser2'
    }

    def 'call svn with a list of excluded users'() {
        when:
        context.svn {
            location('url')
            excludedUsers('user1', 'user2')
            excludedUsers(['user3', 'user4'])
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedUsers[0].value() == 'user1\nuser2\nuser3\nuser4'
    }

    def 'call svn without excluded commit messages'() {
        when:
        context.svn {
            location('url')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedCommitMessages[0].value() == ''
    }

    def 'call svn with single excluded commit message'() {
        when:
        context.svn {
            location('url')
            excludedCommitMessages('commit')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedCommitMessages[0].value() == 'commit'
    }

    def 'call svn with multiple excluded commit messages'() {
        when:
        context.svn {
            location('url')
            excludedCommitMessages('commit1')
            excludedCommitMessages('commit2')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedCommitMessages[0].value() == 'commit1\ncommit2'
    }

    def 'call svn with a list of excluded commit messages'() {
        when:
        context.svn {
            location('url')
            excludedCommitMessages('commit1', 'commit2')
            excludedCommitMessages(['commit3', 'commit4'])
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedCommitMessages[0].value() == 'commit1\ncommit2\ncommit3\ncommit4'
    }

    def 'call svn with a mix of excluded commit message specifications'() {
        when:
        context.svn {
            location('url')
            excludedCommitMessages('commit1', 'commit2')
            excludedCommitMessages('commit3')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedCommitMessages[0].value() == 'commit1\ncommit2\ncommit3'
    }

    def 'call svn without excluded revprop'() {
        when:
        context.svn {
            location('url')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedRevprop[0].value() == ''
    }

    def 'call svn with an excluded revprop'() {
        when:
        context.svn {
            location('url')
            excludedRevisionProperty('revprop')
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.excludedRevprop[0].value() == 'revprop'
    }

    def 'call svn with configure'() {
        when:
        context.svn {
            location('url')
            configure { svnNode ->
                svnNode << testNode('testValue')
            }
        }

        then:
        isValidSvnScmNode(context.scmNode)
        context.scmNode.testNode[0].value() == 'testValue'
    }

    private static void isValidSvnScmNode(scmNode) {
        assert scmNode != null
        assert scmNode.attributes()['class'] == 'hudson.scm.SubversionSCM'
        assert scmNode.locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() > 0
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

    def 'call rtc without build definition or workspace'() {
        when:
        context.rtc {
        }

        then:
        thrown(IllegalArgumentException)
    }

    def 'call rtc with build definition'() {
        when:
        context.rtc {
            buildDefinition('buildDEF')
        }

        then:
        context.scmNode != null
        with(context.scmNode) {
            attributes()['class'] == 'com.ibm.team.build.internal.hjplugin.RTCScm'
            children().size() == 5
            overrideGlobal[0].value() == false
            timeout[0].value() == 0
            buildType[0].value() == 'buildDefinition'
            buildDefinition[0].value() == 'buildDEF'
            avoidUsingToolkit[0].value() == false
        }
    }

    def 'call rtc with build workspace'() {
        when:
        context.rtc {
            buildWorkspace('buildWS')
        }

        then:
        context.scmNode != null
        with(context.scmNode) {
            attributes()['class'] == 'com.ibm.team.build.internal.hjplugin.RTCScm'
            children().size() == 5
            overrideGlobal[0].value() == false
            timeout[0].value() == 0
            buildType[0].value() == 'buildWorkspace'
            buildWorkspace[0].value() == 'buildWS'
            avoidUsingToolkit[0].value() == false
        }
    }

    def 'call rtc with connection override'() {
        setup:
        mockJobManagement.getCredentialsId('absd_credential') >> '0815'

        when:
        context.rtc {
            buildDefinition('buildDEF')
            connection('4.0.7', 'absd_credential', 'https//uri.com/ccm', 480)
        }

        then:
        context.scmNode != null
        with(context.scmNode) {
            attributes()['class'] == 'com.ibm.team.build.internal.hjplugin.RTCScm'
            children().size() == 8
            overrideGlobal[0].value() == true
            buildTool[0].value() == '4.0.7'
            serverURI[0].value() == 'https//uri.com/ccm'
            timeout[0].value() == 480
            credentialsId[0].value() == '0815'
            buildType[0].value() == 'buildDefinition'
            buildDefinition[0].value() == 'buildDEF'
            avoidUsingToolkit[0].value() == false
        }
    }

    def 'rtc validates single SCM'() {
        when:
        context.rtc {
            buildDefinition('foo')
        }
        context.rtc {
            buildDefinition('bar')
        }

        then:
        thrown(IllegalStateException)
    }
}
