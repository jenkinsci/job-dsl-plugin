package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.scm.SvnCheckoutStrategy
import javaposse.jobdsl.dsl.helpers.scm.SvnDepth
import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import spock.lang.Specification
import spock.lang.Unroll

import static javaposse.jobdsl.dsl.helpers.scm.GitMergeOptionsContext.FastForwardMergeMode.FF_ONLY

class ScmContextSpec extends Specification {
    private static final String GIT_REPO_URL = 'git://github.com/Netflix/curator.git'

    JobManagement mockJobManagement = Mock(JobManagement)
    Item item = new FreeStyleJob(mockJobManagement, 'test')
    ScmContext context = new ScmContext(mockJobManagement, item)

    def 'extension node is transformed to SCM node'() {
        Node node = new Node(null, 'org.example.CustomSCM', [foo: 'bar'])
        node.appendNode('test', 'value')

        when:
        context.addExtensionNode(node)

        then:
        with(context.scmNodes[0]) {
            name() == 'scm'
            attributes().size() == 2
            attribute('class') == 'org.example.CustomSCM'
            attribute('foo') == 'bar'
            children().size() == 1
            test[0].text() == 'value'
        }
    }

    def 'call hg simple configuration'() {
        when:
        context.hg('http://selenic.com/repo/hello')

        then:
        context.scmNodes[0].@class == 'hudson.plugins.mercurial.MercurialSCM'
        with(context.scmNodes[0]) {
            name() == 'scm'
            children().size() == 7
            source[0].text() == 'http://selenic.com/repo/hello'
            modules[0].text() == ''
            revisionType[0].text() == 'BRANCH'
            revision[0].text() == 'default'
            clean[0].text() == 'false'
            credentialsId[0].text() == ''
            disableChangeLog[0].text() == 'false'
        }
        (1.._) * mockJobManagement.requireMinimumPluginVersion('mercurial', '1.50.1')
    }

    def 'call hg simple with branch'() {
        when:
        context.hg('http://selenic.com/repo/hello', 'not-default')

        then:
        context.scmNodes[0].@class == 'hudson.plugins.mercurial.MercurialSCM'
        with(context.scmNodes[0]) {
            name() == 'scm'
            children().size() == 7
            source[0].text() == 'http://selenic.com/repo/hello'
            modules[0].text() == ''
            revisionType[0].text() == 'BRANCH'
            revision[0].text() == 'not-default'
            clean[0].text() == 'false'
            credentialsId[0].text() == ''
            disableChangeLog[0].text() == 'false'
        }
        (1.._) * mockJobManagement.requireMinimumPluginVersion('mercurial', '1.50.1')
    }

    def 'call hg without url disallowed'() {
        when:
        context.hg(null) {
        }

        then:
        thrown(DslScriptException)
    }

    def 'call hg branch and tag disallowed'() {
        when:
        context.hg('http://selenic.com/repo/hello') {
            branch('branch')
            tag('tag')
        }

        then:
        thrown(DslScriptException)
    }

    def 'call hg with branch'() {
        when:
        context.hg('http://selenic.com/repo/hello') {
            branch('not-default')
        }

        then:
        context.scmNodes[0].@class == 'hudson.plugins.mercurial.MercurialSCM'
        with(context.scmNodes[0]) {
            name() == 'scm'
            children().size() == 7
            source[0].text() == 'http://selenic.com/repo/hello'
            modules[0].text() == ''
            revisionType[0].text() == 'BRANCH'
            revision[0].text() == 'not-default'
            clean[0].text() == 'false'
            credentialsId[0].text() == ''
            disableChangeLog[0].text() == 'false'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('mercurial', '1.50.1')
    }

    def 'call hg with tag'() {
        when:
        context.hg('http://selenic.com/repo/hello') {
            tag('not-default')
        }

        then:
        context.scmNodes[0].@class == 'hudson.plugins.mercurial.MercurialSCM'
        with(context.scmNodes[0]) {
            name() == 'scm'
            children().size() == 7
            source[0].text() == 'http://selenic.com/repo/hello'
            modules[0].text() == ''
            revisionType[0].text() == 'TAG'
            revision[0].text() == 'not-default'
            clean[0].text() == 'false'
            credentialsId[0].text() == ''
            disableChangeLog[0].text() == 'false'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('mercurial', '1.50.1')
    }

    def 'call hg with all options'() {
        setup:
        when:
        context.hg('http://selenic.com/repo/hello') {
            installation('companyMercurial')
            credentials('user1')
            modules('module-2', 'module-3')
            clean()
            disableChangeLog()
            subdirectory('/foo/bar')
            configure {
                it << foo('bar')
            }
        }

        then:
        context.scmNodes[0].@class == 'hudson.plugins.mercurial.MercurialSCM'
        with(context.scmNodes[0]) {
            name() == 'scm'
            children().size() == 10
            source[0].text() == 'http://selenic.com/repo/hello'
            installation[0].text() == 'companyMercurial'
            modules[0].text() == 'module-2 module-3'
            revisionType[0].text() == 'BRANCH'
            revision[0].text() == 'default'
            clean[0].text() == 'true'
            credentialsId[0].text() == 'user1'
            disableChangeLog[0].text() == 'true'
            subdir[0].text() == '/foo/bar'
            foo[0].text() == 'bar'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('mercurial', '1.50.1')
    }

    def 'call hg with default values'() {
        when:
        context.hg('http://selenic.com/repo/hello') {
        }

        then:
        context.scmNodes[0].@class == 'hudson.plugins.mercurial.MercurialSCM'
        with(context.scmNodes[0]) {
            name() == 'scm'
            children().size() == 7
            source[0].text() == 'http://selenic.com/repo/hello'
            modules[0].text() == ''
            revisionType[0].text() == 'BRANCH'
            revision[0].text() == 'default'
            clean[0].text() == 'false'
            credentialsId[0].text() == ''
            disableChangeLog[0].text() == 'false'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('mercurial', '1.50.1')
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
        context.scmNodes[0] != null
        context.scmNodes[0].userRemoteConfigs.size() == 1
        context.scmNodes[0].userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'.size() == 2
        with(context.scmNodes[0].userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0]) {
            name[0].text() == 'origin'
            url[0].text() == 'https://github.com/jenkinsci/jenkins.git'
            refspec[0].text() == '+refs/heads/master:refs/remotes/origin/master'
        }
        with(context.scmNodes[0].userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[1]) {
            name[0].text() == 'other'
            url[0].text() == 'https://github.com/jenkinsci/job-dsl-plugin.git'
            refspec[0].text() == '+refs/heads/master:refs/remotes/other/master'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with RelativeTargetDirectory extension'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                relativeTargetDirectory('foo')
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].children()[0].name() == 'hudson.plugins.git.extensions.impl.RelativeTargetDirectory'
            extensions[0].children()[0].children().size() == 1
            extensions[0].children()[0].relativeTargetDir[0].value() == 'foo'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with minimal clone options'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                cloneOptions {
                }
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].children().size() == 4
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].reference[0].value() == ''
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].shallow[0].value() == false
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].noTags[0].value() == false
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].honorRefspec[0].value() == false
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with full clone options'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                cloneOptions {
                    shallow()
                    noTags()
                    reference('/foo')
                    timeout(40)
                    honorRefspec()
                }
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].children().size() == 5
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].reference[0].value() == '/foo'
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].shallow[0].value() == true
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].noTags[0].value() == true
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].timeout[0].value() == 40
            extensions[0].'hudson.plugins.git.extensions.impl.CloneOption'[0].honorRefspec[0].value() == true
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with PruneStaleBranch extension'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                pruneBranches()
            }
        }

        then:
        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].children()[0].name() == 'hudson.plugins.git.extensions.impl.PruneStaleBranch'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with LocalBranch extension'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                localBranch('bugfix')
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].children()[0].name() == 'hudson.plugins.git.extensions.impl.LocalBranch'
            extensions[0].children()[0].children().size() == 1
            extensions[0].children()[0].localBranch[0].value() == 'bugfix'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with LocalBranch extension with empty branch'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                localBranch(branch)
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].children()[0].name() == 'hudson.plugins.git.extensions.impl.LocalBranch'
            extensions[0].children()[0].children().size() == 0
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')

        where:
        branch << [null, '']
    }

    def 'call git scm with LocalBranch extension without branch'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                localBranch()
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].children()[0].name() == 'hudson.plugins.git.extensions.impl.LocalBranch'
            extensions[0].children()[0].children().size() == 0
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with PerBuildTag extension'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                perBuildTag()
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].children()[0].name() == 'hudson.plugins.git.extensions.impl.PerBuildTag'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with clean before checkout'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                cleanBeforeCheckout()
            }
        }

        then:
        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].children()[0].name() == 'hudson.plugins.git.extensions.impl.CleanBeforeCheckout'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with clean after checkout'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                cleanAfterCheckout()
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].children()[0].name() == 'hudson.plugins.git.extensions.impl.CleanCheckout'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with WipeWorkspace extension'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                wipeOutWorkspace()
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].children()[0].name() == 'hudson.plugins.git.extensions.impl.WipeWorkspace'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with DisableRemotePoll extension'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                disableRemotePoll()
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].children()[0].name() == 'hudson.plugins.git.extensions.impl.DisableRemotePoll'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with no branch'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
        }

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].branches.size() == 1
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'.size() == 1
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].text() == '**'
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
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
        context.scmNodes[0] != null
        context.scmNodes[0].branches.size() == 1
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'.size() == 3
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].text() == 'foo'
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'[1].name[0].text() == 'bar'
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'[2].name[0].text() == 'test'
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with minimal mergeOptions'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                mergeOptions {
                }
            }
        }

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].extensions.size() == 1
        context.scmNodes[0].extensions[0].'hudson.plugins.git.extensions.impl.PreBuildMerge'.size() == 1
        with(context.scmNodes[0].extensions[0].'hudson.plugins.git.extensions.impl.PreBuildMerge'[0]) {
            options.size() == 1
            options[0].children().size() == 4
            options[0].mergeRemote.size() == 1
            options[0].mergeRemote[0].text() == ''
            options[0].mergeTarget.size() == 1
            options[0].mergeTarget[0].text() == ''
            options[0].mergeStrategy.size() == 1
            options[0].mergeStrategy[0].text() == 'default'
            options[0].fastForwardMode.size() == 1
            options[0].fastForwardMode[0].text() == 'FF'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with full mergeOptions'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                mergeOptions {
                    remote('other')
                    branch('acme-plugin')
                    strategy('default')
                    fastForwardMode(FF_ONLY)
                }
            }
        }

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].extensions.size() == 1
        context.scmNodes[0].extensions[0].'hudson.plugins.git.extensions.impl.PreBuildMerge'.size() == 1
        with(context.scmNodes[0].extensions[0].'hudson.plugins.git.extensions.impl.PreBuildMerge'[0]) {
            options.size() == 1
            options[0].children().size() == 4
            options[0].mergeRemote.size() == 1
            options[0].mergeRemote[0].text() == 'other'
            options[0].mergeTarget.size() == 1
            options[0].mergeTarget[0].text() == 'acme-plugin'
            options[0].mergeStrategy.size() == 1
            options[0].mergeStrategy[0].text() == 'default'
            options[0].fastForwardMode.size() == 1
            options[0].fastForwardMode[0].text() == 'FF_ONLY'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with valid merge strategy'() {
        when:
        context.git {
             extensions {
                mergeOptions {
                    strategy(mergeStrategy)
                }
            }
        }

        then:
        with(context.scmNodes[0].extensions[0].'hudson.plugins.git.extensions.impl.PreBuildMerge'[0]) {
            options[0].mergeStrategy.size() == 1
            options[0].mergeStrategy[0].text() == mergeStrategy
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')

        where:
        mergeStrategy << ['default', 'resolve', 'recursive', 'octopus', 'ours', 'subtree']
    }

    def 'call git scm with inverse build chooser extension'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                choosingStrategy {
                    inverse()
                }
            }
        }

        then:
        context.scmNodes[0] != null
        context.scmNodes[0] != null
        context.scmNodes[0].extensions.size() == 1
        context.scmNodes[0].extensions[0].'hudson.plugins.git.extensions.impl.BuildChooserSetting'.size() == 1
        with(context.scmNodes[0].extensions[0].'hudson.plugins.git.extensions.impl.BuildChooserSetting'[0]) {
            children().size() == 1
            buildChooser[0].attribute('class') == 'hudson.plugins.git.util.InverseBuildChooser'
            buildChooser[0].children().size() == 0
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with ancestry build chooser extension'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                choosingStrategy {
                    ancestry(5, 'sha1')
                }
            }
        }

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].extensions.size() == 1
        context.scmNodes[0].extensions[0].'hudson.plugins.git.extensions.impl.BuildChooserSetting'.size() == 1
        with(context.scmNodes[0].extensions[0].'hudson.plugins.git.extensions.impl.BuildChooserSetting'[0]) {
            children().size() == 1
            buildChooser[0].attribute('class') == 'hudson.plugins.git.util.AncestryBuildChooser'
            buildChooser[0].children().size() == 2
            buildChooser[0].maximumAgeInDays[0].text() == '5'
            buildChooser[0].ancestorCommitSha1[0].text() == 'sha1'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with alternative build chooser extension'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                choosingStrategy {
                    alternative()
                }
            }
        }

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].extensions.size() == 1
        context.scmNodes[0].extensions[0].'hudson.plugins.git.extensions.impl.BuildChooserSetting'.size() == 1
        with(context.scmNodes[0].extensions[0].'hudson.plugins.git.extensions.impl.BuildChooserSetting'[0]) {
            children().size() == 1
            buildChooser[0].attribute('class') ==
                    'org.jenkinsci.plugins.git.chooser.alternative.AlternativeBuildChooser'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
        1 * mockJobManagement.requireMinimumPluginVersion('git-chooser-alternative', '1.1')
    }

    def 'call git scm with gerrit trigger build chooser extension'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                choosingStrategy {
                    gerritTrigger()
                }
            }
        }

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].extensions.size() == 1
        context.scmNodes[0].extensions[0].'hudson.plugins.git.extensions.impl.BuildChooserSetting'.size() == 1
        with(context.scmNodes[0].extensions[0].'hudson.plugins.git.extensions.impl.BuildChooserSetting'[0]) {
            children().size() == 1
            buildChooser[0].attribute('class') ==
                    'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTriggerBuildChooser'
            buildChooser[0].children().size() == 1
            buildChooser[0].separator[0].text() == '#'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
        1 * mockJobManagement.requireMinimumPluginVersion('gerrit-trigger', '2.0')
    }

    def 'call git scm with credentials'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
                credentials('ci-user')
            }
        }

        then:
        with(context.scmNodes[0]) {
            userRemoteConfigs.size() == 1
            userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'.size() == 1
            userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].credentialsId[0].text() == 'ci-user'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm methods'() {
        when:
        context.git(GIT_REPO_URL)

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            children().size() == 6
            userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() == GIT_REPO_URL
            branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == '**'
            createTag.size() == 0
            with(extensions[0]) {
                children().size() == 1
                delegate.'hudson.plugins.git.extensions.impl.PerBuildTag'.size() == 1
            }
        }
        (1.._) * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with branch'() {
        when:
        context.git(GIT_REPO_URL, 'feature-branch')

        then:
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'feature-branch'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
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
        context.scmNodes[0] != null
        context.scmNodes[0].browser.size() == 1
        context.scmNodes[0].browser[0].attribute('class') == 'hudson.plugins.git.browser.Stash'
        context.scmNodes[0].browser[0].'url'[0].value() == 'http://stash'
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with gitblitBrowser'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            browser {
                gitblit('http://gitblit', 'prj-name')
            }
        }

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].browser.size() == 1
        context.scmNodes[0].browser[0].attribute('class') == 'hudson.plugins.git.browser.GitBlitRepositoryBrowser'
        context.scmNodes[0].browser[0].'url'[0].value() == 'http://gitblit'
        context.scmNodes[0].browser[0].'projectName'[0].value() == 'prj-name'
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with gitlabBrowser'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            browser {
                gitLab('http://gitlab', '7.9')
            }
        }

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].browser.size() == 1
        context.scmNodes[0].browser[0].attribute('class') == 'hudson.plugins.git.browser.GitLab'
        context.scmNodes[0].browser[0].'url'[0].value() == 'http://gitlab'
        context.scmNodes[0].browser[0].'version'[0].value() == '7.9'
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with gitilesBrowser'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            browser {
                gitiles('http://gitiles')
            }
        }

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].browser.size() == 1
        context.scmNodes[0].browser[0].attribute('class') == 'hudson.plugins.git.browser.Gitiles'
        context.scmNodes[0].browser[0].'url'[0].value() == 'http://gitiles'
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with GitWeb browser'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            browser {
                gitWeb('http://acme.org')
            }
        }

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].browser.size() == 1
        context.scmNodes[0].browser[0].attribute('class') == 'hudson.plugins.git.browser.GitWeb'
        context.scmNodes[0].browser[0].'url'[0].value() == 'http://acme.org'
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with ignoreNotifyCommit extension'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                ignoreNotifyCommit()
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            extensions[0].children()[0].name() == 'hudson.plugins.git.extensions.impl.IgnoreNotifyCommit'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with full submodule options and plugin version 3.0.0'() {
        setup:
        mockJobManagement.isMinimumPluginVersionInstalled('git', '3.0.0') >> true

        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                submoduleOptions {
                    disable()
                    recursive()
                    tracking()
                    parentCredentials()
                }
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            with(extensions[0].'hudson.plugins.git.extensions.impl.SubmoduleOption'[0]) {
                children().size() == 5
                disableSubmodules[0].value() == true
                recursiveSubmodules[0].value() == true
                trackingSubmodules[0].value() == true
                reference[0].value() == ''
                parentCredentials[0].value() == true
            }
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
        1 * mockJobManagement.requireMinimumPluginVersion('git', '3.0.0')
    }

    def 'call git scm with minimal submodule options'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                submoduleOptions {
                }
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            with(extensions[0].'hudson.plugins.git.extensions.impl.SubmoduleOption'[0]) {
                children().size() == 4
                disableSubmodules[0].value() == false
                recursiveSubmodules[0].value() == false
                trackingSubmodules[0].value() == false
                reference[0].value() == ''
            }
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with full submodule options'() {
        when:
        context.git {
            remote {
                url('https://github.com/jenkinsci/job-dsl-plugin.git')
            }
            extensions {
                submoduleOptions {
                    disable()
                    recursive()
                    tracking()
                    reference('/foo')
                }
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            extensions.size() == 1
            extensions[0].children().size() == 1
            with(extensions[0].'hudson.plugins.git.extensions.impl.SubmoduleOption'[0]) {
                children().size() == 4
                disableSubmodules[0].value() == true
                recursiveSubmodules[0].value() == true
                trackingSubmodules[0].value() == true
                reference[0].value() == '/foo'
            }
        }
        1 * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with configure appending'() {
        when:
        context.git(GIT_REPO_URL, null) { Node gitNode ->
            gitNode << authorOrCommitter('true')
            gitNode.appendNode('gitTool', 'NotDefault')
        }

        then:
        context.scmNodes[0].authorOrCommitter.size() == 1
        context.scmNodes[0].authorOrCommitter[0].text() == 'true'
        context.scmNodes[0].gitTool.size() == 2
        context.scmNodes[0].gitTool[0].text() == 'Default'
        context.scmNodes[0].gitTool[1].text() == 'NotDefault'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call git scm with configure on Node'() {
        when:
        context.git(GIT_REPO_URL, null) { gitNode ->
            gitNode << gitConfigName('john') // Always append
            gitNode.appendNode('gitConfigEmail', 'john@gmail.com') // Clearly an append
            gitNode / scmName << 'Kittner' // Re-use node and set value
        }

        then:
        context.scmNodes[0].gitConfigName.size() == 1
        context.scmNodes[0].gitConfigName[0].text() == 'john'
        context.scmNodes[0].gitConfigEmail.size() == 1
        context.scmNodes[0].gitConfigEmail[0].text() == 'john@gmail.com'
        context.scmNodes[0].scmName.size() == 1
        context.scmNodes[0].scmName[0].text() == 'Kittner'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call github scm method'() {
        when:
        context.github('jenkinsci/job-dsl-plugin')

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() ==
                'https://github.com/jenkinsci/job-dsl-plugin.git'
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == '**'
        context.scmNodes[0].browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNodes[0].browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')

        when:
        Node root = item.node

        then:
        root.'properties'[0].'com.coravy.hudson.plugins.github.GithubProjectProperty'[0].projectUrl[0].value() ==
                'https://github.com/jenkinsci/job-dsl-plugin/'
    }

    def 'call github scm method with branch'() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master')

        then:
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call github scm method with ssh protocol'() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master', 'ssh')

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() ==
                'git@github.com:jenkinsci/job-dsl-plugin.git'
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNodes[0].browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNodes[0].browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')

        when:
        Node root = item.node

        then:
        root.'properties'[0].'com.coravy.hudson.plugins.github.GithubProjectProperty'[0].projectUrl[0].value() ==
                'https://github.com/jenkinsci/job-dsl-plugin/'
    }

    def 'call github scm method with git protocol'() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master', 'git')

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() ==
                'git://github.com/jenkinsci/job-dsl-plugin.git'
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNodes[0].browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNodes[0].browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')

        when:
        Node root = item.node

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
        context.scmNodes[0] != null
        context.scmNodes[0].userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() ==
                'git@github.acme.com:jenkinsci/job-dsl-plugin.git'
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNodes[0].browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNodes[0].browser[0].url[0].value() == 'https://github.acme.com/jenkinsci/job-dsl-plugin/'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')

        when:
        Node root = item.node

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
        context.scmNodes[0].gitConfigName.size() == 1
        context.scmNodes[0].gitConfigName[0].text() == 'john'
        context.scmNodes[0].browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNodes[0].browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call github scm with branch and closure'() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master') { gitNode ->
            gitNode << gitConfigName('john') // Always append
        }

        then:
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNodes[0].browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNodes[0].browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        context.scmNodes[0].gitConfigName.size() == 1
        context.scmNodes[0].gitConfigName[0].text() == 'john'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call github scm with branch, protocol and closure'() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master', 'ssh') { gitNode ->
            gitNode << gitConfigName('john') // Always append
        }

        then:
        context.scmNodes[0].userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() ==
                'git@github.com:jenkinsci/job-dsl-plugin.git'
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNodes[0].browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNodes[0].browser[0].url[0].value() == 'https://github.com/jenkinsci/job-dsl-plugin/'
        context.scmNodes[0].gitConfigName.size() == 1
        context.scmNodes[0].gitConfigName[0].text() == 'john'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'call github scm method with protocol, host and closure '() {
        when:
        context.github('jenkinsci/job-dsl-plugin', 'master', 'ssh', 'github.acme.com') { gitNode ->
            gitNode << gitConfigName('john') // Always append
        }

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].userRemoteConfigs[0].'hudson.plugins.git.UserRemoteConfig'[0].url[0].value() ==
                'git@github.acme.com:jenkinsci/job-dsl-plugin.git'
        context.scmNodes[0].branches[0].'hudson.plugins.git.BranchSpec'[0].name[0].value() == 'master'
        context.scmNodes[0].browser[0].attribute('class') == 'hudson.plugins.git.browser.GithubWeb'
        context.scmNodes[0].browser[0].url[0].value() == 'https://github.acme.com/jenkinsci/job-dsl-plugin/'
        context.scmNodes[0].gitConfigName.size() == 1
        context.scmNodes[0].gitConfigName[0].text() == 'john'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('git', '2.5.3')

        when:
        Node root = item.node

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
        context.scmNodes[0] != null
        context.scmNodes[0].attributes()['class'] == 'hudson.scm.SubversionSCM'
        context.scmNodes[0].locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() ==
                'http://svn.apache.org/repos/asf/xml/crimson/trunk/'
        context.scmNodes[0].excludedRegions.size() == 1
        context.scmNodes[0].excludedRegions[0].value() == '/trunk/.*'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with remote and local'() {
        when:
        context.svn('http://svn.apache.org/repos/asf/xml/crimson/trunk/', '/mydir/mycode')

        then:
        with(context.scmNodes[0]) {
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == '/mydir/mycode'
        }
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
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
        context.scmNodes[0] != null
        context.scmNodes[0].browser[0].attributes()['class'] == 'hudson.scm.browsers.FishEyeSVN'
        context.scmNodes[0].browser[0].url[0].value() == 'http://mycompany.com/fisheye/repo_name'
        context.scmNodes[0].browser[0].rootModule[0].value() == 'my_root_module'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with browser - ViewSVN example'() {
        when:
        context.svn('http://svn.apache.org/repos/asf/xml/crimson/trunk/') { svnNode ->
            svnNode / browser(class: 'hudson.scm.browsers.ViewSVN') / url << 'http://mycompany.com/viewsvn/repo_name'
        }

        then:
        context.scmNodes[0] != null
        context.scmNodes[0].browser[0].attributes()['class'] == 'hudson.scm.browsers.ViewSVN'
        context.scmNodes[0].browser[0].url[0].value() == 'http://mycompany.com/viewsvn/repo_name'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with no locations'() {
        when:
        context.svn {
        }

        then:
        thrown(DslScriptException)
    }

    def 'call svn with one location'() {
        when:
        context.svn {
            location('url') {
                directory('dir')
                ignoreExternals()
            }
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        with(context.scmNodes[0]) {
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() == 1
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].children().size() == 4
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'url'
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == 'dir'
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].depthOption[0].value() == 'infinity'
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].ignoreExternalsOption[0].value() == true
        }
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with credentials'() {
        when:
        context.svn {
            location('url') {
                credentials('foo')
            }
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        with(context.scmNodes[0]) {
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() == 1
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].children().size() == 5
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'url'
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == '.'
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].depthOption[0].value() == 'infinity'
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].credentialsId[0].value() == 'foo'
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].ignoreExternalsOption[0].value() == false
        }
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
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
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() == 2
        context.scmNodes[0].locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'url1'
        context.scmNodes[0].locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == '.'
        context.scmNodes[0].locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[1].remote[0].value() == 'url2'
        context.scmNodes[0].locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[1].local[0].value() == 'dir2'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn without specifying a local dir for the location'() {
        when:
        context.svn {
            location('url')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'.size() == 1
        context.scmNodes[0].locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].remote[0].value() == 'url'
        context.scmNodes[0].locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].local[0].value() == '.'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
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
        isValidSvnScmNode(context.scmNodes[0])
        with(context.scmNodes[0]) {
            locations[0].'hudson.scm.SubversionSCM_-ModuleLocation'[0].depthOption[0].value() == xmlValue
        }
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')

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
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].workspaceUpdater[0].attributes()['class'] == 'hudson.scm.subversion.UpdateUpdater'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with checkout strategy'() {
        when:
        context.svn {
            location('url')
            checkoutStrategy(strategy)
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].workspaceUpdater[0].attributes()['class'] == workspaceUpdaterClass
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')

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
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedRegions[0].value() == ''
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with single excluded region'() {
        when:
        context.svn {
            location('url')
            excludedRegions('exreg')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedRegions[0].value() == 'exreg'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with multiple excluded regions'() {
        when:
        context.svn {
            location('url')
            excludedRegions('exreg1')
            excludedRegions('exreg2')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedRegions[0].value() == 'exreg1\nexreg2'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with a list of excluded regions'() {
        when:
        context.svn {
            location('url')
            excludedRegions('exreg1', 'exreg2')
            excludedRegions(['exreg3', 'exreg4'])
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedRegions[0].value() == 'exreg1\nexreg2\nexreg3\nexreg4'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn without included regions'() {
        when:
        context.svn {
            location('url')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].includedRegions[0].value() == ''
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with single included region'() {
        when:
        context.svn {
            location('url')
            includedRegions('increg')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].includedRegions[0].value() == 'increg'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with multiple included regions'() {
        when:
        context.svn {
            location('url')
            includedRegions('increg1')
            includedRegions('increg2')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].includedRegions[0].value() == 'increg1\nincreg2'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with a list of included regions'() {
        when:
        context.svn {
            location('url')
            includedRegions('increg1', 'increg2')
            includedRegions(['increg3', 'increg4'])
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].includedRegions[0].value() == 'increg1\nincreg2\nincreg3\nincreg4'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn without excluded users'() {
        when:
        context.svn {
            location('url')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedUsers[0].value() == ''
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with single excluded user'() {
        when:
        context.svn {
            location('url')
            excludedUsers('user')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedUsers[0].value() == 'user'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with multiple excluded users'() {
        when:
        context.svn {
            location('url')
            excludedUsers('user1')
            excludedUsers('user2')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedUsers[0].value() == 'user1\nuser2'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with a list of excluded users'() {
        when:
        context.svn {
            location('url')
            excludedUsers('user1', 'user2')
            excludedUsers(['user3', 'user4'])
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedUsers[0].value() == 'user1\nuser2\nuser3\nuser4'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn without excluded commit messages'() {
        when:
        context.svn {
            location('url')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedCommitMessages[0].value() == ''
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with single excluded commit message'() {
        when:
        context.svn {
            location('url')
            excludedCommitMessages('commit')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedCommitMessages[0].value() == 'commit'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with multiple excluded commit messages'() {
        when:
        context.svn {
            location('url')
            excludedCommitMessages('commit1')
            excludedCommitMessages('commit2')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedCommitMessages[0].value() == 'commit1\ncommit2'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with a list of excluded commit messages'() {
        when:
        context.svn {
            location('url')
            excludedCommitMessages('commit1', 'commit2')
            excludedCommitMessages(['commit3', 'commit4'])
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedCommitMessages[0].value() == 'commit1\ncommit2\ncommit3\ncommit4'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with a mix of excluded commit message specifications'() {
        when:
        context.svn {
            location('url')
            excludedCommitMessages('commit1', 'commit2')
            excludedCommitMessages('commit3')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedCommitMessages[0].value() == 'commit1\ncommit2\ncommit3'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn without excluded revprop'() {
        when:
        context.svn {
            location('url')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedRevprop[0].value() == ''
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'call svn with an excluded revprop'() {
        when:
        context.svn {
            location('url')
            excludedRevisionProperty('revprop')
        }

        then:
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].excludedRevprop[0].value() == 'revprop'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
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
        isValidSvnScmNode(context.scmNodes[0])
        context.scmNodes[0].testNode[0].value() == 'testValue'
        (1.._) * mockJobManagement.requireMinimumPluginVersion('subversion', '2.1')
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
        context.scmNodes[0] != null
        context.scmNodes[0].attributes()['class'] == 'hudson.plugins.perforce.PerforceSCM'
        context.scmNodes[0].p4User[0].value() == 'roleoe'
        context.scmNodes[0].p4Passwd[0].value() == '0f0kqlwajkEPwz8Yp+A=' // Using PerforcePasswordEncryptor
        context.scmNodes[0].p4Port[0].value() == 'perforce:1666'
        context.scmNodes[0].alwaysForceSync.size() == 1 // Double check there's only one
        context.scmNodes[0].alwaysForceSync[0].value() == 'true'
        context.scmNodes[0].projectPath.size() == 1
        context.scmNodes[0].projectPath[0].value().contains('//depot')
        1 * mockJobManagement.requirePlugin('perforce')
    }

    def 'call perforceP4 with all options'() {
        when:
        context.perforceP4('p4-user-creds') {
            workspace {
                manual('ws', 'view')
            }
            configure { node ->
                node / foo('bar')
            }
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            children().size() == 4
            attributes()['class'] == 'org.jenkinsci.plugins.p4.PerforceScm'
            credential[0].value() == 'p4-user-creds'
            workspace.size() == 1
            with(workspace[0]) {
                children().size() == 4
                attributes()['class'] == 'org.jenkinsci.plugins.p4.workspace.ManualWorkspaceImpl'
                charset[0].value() == 'none'
                pinHost[0].value() == false
                name[0].value() == 'ws'
                spec.size() == 1
                with(spec[0]) {
                    children().size() == 9
                    allwrite[0].value() == false
                    clobber[0].value() == false
                    compress[0].value() == false
                    locked[0].value() == false
                    modtime[0].value() == false
                    rmdir[0].value() == false
                    streamName[0].value().empty
                    line[0].value() == 'LOCAL'
                    view[0].value() == 'view'
                }
            }
            with(populate[0]) {
                children().size() == 7
                attributes()['class'] == 'org.jenkinsci.plugins.p4.populate.AutoCleanImpl'
                have[0].value() == true
                force[0].value() == false
                modtime[0].value() == false
                quiet[0].value() == true
                pin[0].value().empty
                replace[0].value() == true
                delete[0].value() == true
            }
            foo[0].value() == 'bar'
        }
        1 * mockJobManagement.requireMinimumPluginVersion('p4', '1.3.5')
    }

    def 'call perforcep4 with no options'() {
        when:
        context.perforceP4('p4-user-creds') {
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            children().size() == 3
            attributes()['class'] == 'org.jenkinsci.plugins.p4.PerforceScm'
            credential[0].value() == 'p4-user-creds'
            workspace.size() == 1
            with(workspace[0]) {
                children().size() == 4
                attributes()['class'] == 'org.jenkinsci.plugins.p4.workspace.ManualWorkspaceImpl'
                charset[0].value() == 'none'
                pinHost[0].value() == false
                name[0].value().empty
                spec.size() == 1
                with(spec[0]) {
                    children().size() == 9
                    allwrite[0].value() == false
                    clobber[0].value() == false
                    compress[0].value() == false
                    locked[0].value() == false
                    modtime[0].value() == false
                    rmdir[0].value() == false
                    streamName[0].value().empty
                    line[0].value() == 'LOCAL'
                    view[0].value().empty
                }
            }
            with(populate[0]) {
                children().size() == 7
                attributes()['class'] == 'org.jenkinsci.plugins.p4.populate.AutoCleanImpl'
                have[0].value() == true
                force[0].value() == false
                modtime[0].value() == false
                quiet[0].value() == true
                pin[0].value().empty
                replace[0].value() == true
                delete[0].value() == true
            }
        }
        1 * mockJobManagement.requireMinimumPluginVersion('p4', '1.3.5')
    }

    def 'call cloneWorkspace'(parentJob, criteria) {
        when:
        context.cloneWorkspace(parentJob, criteria)

        then:
        context.scmNodes[0].parentJobName.text() == parentJob
        context.scmNodes[0].criteria.text() == criteria
        1 * mockJobManagement.requirePlugin('clone-workspace-scm')

        where:
        parentJob | criteria
        'parent'  | 'Any'
        'some'    | 'Successful'
    }

    def 'call baseClearCase with default configuration'() {
        when:
        context.baseClearCase()

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
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
        1 * mockJobManagement.requirePlugin('clearcase')
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
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
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
        1 * mockJobManagement.requirePlugin('clearcase')
    }

    def 'call rtc without build definition or workspace'() {
        when:
        context.rtc {
        }

        then:
        thrown(DslScriptException)
    }

    def 'call rtc with build definition'() {
        when:
        context.rtc {
            buildDefinition('buildDEF')
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            attributes()['class'] == 'com.ibm.team.build.internal.hjplugin.RTCScm'
            children().size() == 5
            overrideGlobal[0].value() == false
            timeout[0].value() == 0
            buildType[0].value() == 'buildDefinition'
            buildDefinition[0].value() == 'buildDEF'
            avoidUsingToolkit[0].value() == false
        }
        1 * mockJobManagement.requirePlugin('teamconcert')
    }

    def 'call rtc with build workspace'() {
        when:
        context.rtc {
            buildWorkspace('buildWS')
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            attributes()['class'] == 'com.ibm.team.build.internal.hjplugin.RTCScm'
            children().size() == 5
            overrideGlobal[0].value() == false
            timeout[0].value() == 0
            buildType[0].value() == 'buildWorkspace'
            buildWorkspace[0].value() == 'buildWS'
            avoidUsingToolkit[0].value() == false
        }
        1 * mockJobManagement.requirePlugin('teamconcert')
    }

    def 'call rtc with connection override'() {
        when:
        context.rtc {
            buildDefinition('buildDEF')
            connection('4.0.7', 'absd_credential', 'https//uri.com/ccm', 480)
        }

        then:
        context.scmNodes[0] != null
        with(context.scmNodes[0]) {
            attributes()['class'] == 'com.ibm.team.build.internal.hjplugin.RTCScm'
            children().size() == 8
            overrideGlobal[0].value() == true
            buildTool[0].value() == '4.0.7'
            serverURI[0].value() == 'https//uri.com/ccm'
            timeout[0].value() == 480
            credentialsId[0].value() == 'absd_credential'
            buildType[0].value() == 'buildDefinition'
            buildDefinition[0].value() == 'buildDEF'
            avoidUsingToolkit[0].value() == false
        }
        1 * mockJobManagement.requirePlugin('teamconcert')
    }
}
