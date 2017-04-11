package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

import static javaposse.jobdsl.dsl.helpers.triggers.BuildResultTriggerContext.BuildResult.ABORTED
import static javaposse.jobdsl.dsl.helpers.triggers.BuildResultTriggerContext.BuildResult.FAILURE
import static javaposse.jobdsl.dsl.helpers.triggers.BuildResultTriggerContext.BuildResult.NOT_BUILT
import static javaposse.jobdsl.dsl.helpers.triggers.BuildResultTriggerContext.BuildResult.SUCCESS
import static javaposse.jobdsl.dsl.helpers.triggers.BuildResultTriggerContext.BuildResult.UNSTABLE

class TriggerContextSpec extends Specification {
    JobManagement mockJobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    TriggerContext context = new TriggerContext(mockJobManagement, item)

    def 'node from extension is added'() {
        setup:
        Node node = Mock(Node)

        when:
        context.addExtensionNode(node)

        then:
        context.triggerNodes[0] == node
    }

    def 'call github trigger methods'() {
        when:
        context.githubPush()

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def githubPushTrigger = context.triggerNodes[0]
        githubPushTrigger.name() == 'com.cloudbees.jenkins.GitHubPushTrigger'
        githubPushTrigger.spec[0].value() == ''
        1 * mockJobManagement.requirePlugin('github')
    }

    def 'call scm trigger methods'() {
        when:
        context.scm('*/5 * * * *')

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def timerTrigger = context.triggerNodes[0]
        timerTrigger.name() == 'hudson.triggers.SCMTrigger'
        timerTrigger.spec[0].value() == '*/5 * * * *'
    }

    def 'call scm trigger with closure'() {
        when:
        context.scm('*/5 * * * *') {
            ignorePostCommitHooks()
        }

        then:
        with(context.triggerNodes[0]) {
            name() == 'hudson.triggers.SCMTrigger'
            children().size() == 2
            spec[0].value() == '*/5 * * * *'
            ignorePostCommitHooks[0].value() == true
        }
    }

    def 'call empty gerrit trigger methods'() {
        when:
        context.gerrit {
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def gerritTrigger = context.triggerNodes[0]
        gerritTrigger.name().contains('GerritTrigger')
        !gerritTrigger.buildStartMessage.isEmpty()
        1 * mockJobManagement.requirePlugin('gerrit-trigger')
    }

    def 'call advanced gerrit trigger methods'() {
        when:
        context.gerrit {
            events {
                changeMerged()
                draftPublished()
            }
            project('reg_exp:myProject', ['ant:feature-branch', 'plain:origin/refs/mybranch']) // full access
            project('test-project', '**') // simplified
            configure { node ->
                node / gerritBuildSuccessfulVerifiedValue << '10'
            }
        }

        then:
        with(context.triggerNodes[0]) {
            gerritBuildSuccessfulVerifiedValue.size() == 1
            gerritBuildSuccessfulVerifiedValue[0].value() == '10'
            with(triggerOnEvents[0]) {
                children().size() == 2
                children()[0].name() =~ /com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.Plugin/
            }
            with(gerritProjects[0]) {
                children().size() == 2
                with(children()[0]) {
                    name() == 'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.GerritProject'
                    compareType[0].value() == 'REG_EXP'
                    pattern[0].value() == 'myProject'
                    branches[0].children().size() == 2
                    with(branches[0].children()[0]) {
                        name() == 'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.Branch'
                        compareType[0].value() == 'ANT'
                        pattern[0].value() == 'feature-branch'
                    }
                }
                with(children()[1]) {
                    compareType[0].value() == 'PLAIN'
                    pattern[0].value() == 'test-project'
                    branches[0].children().size() == 1
                }
            }
        }
        1 * mockJobManagement.requirePlugin('gerrit-trigger')
    }

    def 'call gerrit trigger with events'(String event) {
        when:
        context.gerrit {
            events {
                delegate."${event}"()
            }
        }

        then:
        String xmlEvent = event.capitalize()
        with(context.triggerNodes[0].triggerOnEvents[0]) {
            children().size() == 1
            children()[0].name() ==
                    "com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.Plugin${xmlEvent}Event"
        }
        1 * mockJobManagement.requirePlugin('gerrit-trigger')

        where:
        event << [
                'changeAbandoned', 'changeMerged', 'changeRestored', 'commentAdded', 'draftPublished',
                'patchsetCreated', 'refUpdated'
        ]
    }

    def 'call gerrit trigger and verify build status value settings'() {
        when:
        context.gerrit {
            events {
                patchsetCreated()
                draftPublished()
            }

            project('test-project', '**')
        }

        then:
        with(context.triggerNodes[0]) {
            gerritBuildSuccessfulCodeReviewValue.size() == 0
            gerritBuildSuccessfulVerifiedValue.size() == 0
            gerritBuildFailedVerifiedValue.size() == 0
            gerritBuildFailedCodeReviewValue.size() == 0
            gerritBuildUnstableVerifiedValue.size() == 0
            gerritBuildUnstableCodeReviewValue.size() == 0
        }
        1 * mockJobManagement.requirePlugin('gerrit-trigger')
    }

    def 'call gerrit trigger and verify build status value methods'() {
        when:
        context.gerrit {
            events {
                patchsetCreated()
                draftPublished()
            }

            project('test-project', '**')

            buildSuccessful(11, 10)
            buildFailed(-21, 20)
            buildUnstable(30, 32)
            buildNotBuilt(40, 42)
            buildStarted(50, 55)
        }

        then:
        with(context.triggerNodes[0]) {
            gerritBuildSuccessfulCodeReviewValue.size() == 1
            gerritBuildSuccessfulCodeReviewValue[0].value() == 10

            gerritBuildSuccessfulVerifiedValue.size() == 1
            gerritBuildSuccessfulVerifiedValue[0].value() == 11

            gerritBuildFailedVerifiedValue.size() == 1
            gerritBuildFailedVerifiedValue[0].value() == -21

            gerritBuildFailedCodeReviewValue.size() == 1
            gerritBuildFailedCodeReviewValue[0].value() == 20

            gerritBuildUnstableVerifiedValue.size() == 1
            gerritBuildUnstableVerifiedValue[0].value() == 30

            gerritBuildUnstableCodeReviewValue.size() == 1
            gerritBuildUnstableCodeReviewValue[0].value() == 32

            gerritBuildNotBuiltVerifiedValue.size() == 1
            gerritBuildNotBuiltVerifiedValue[0].value() == 40

            gerritBuildNotBuiltCodeReviewValue.size() == 1
            gerritBuildNotBuiltCodeReviewValue[0].value() == 42

            gerritBuildStartedVerifiedValue.size() == 1
            gerritBuildStartedVerifiedValue[0].value() == 50

            gerritBuildStartedCodeReviewValue.size() == 1
            gerritBuildStartedCodeReviewValue[0].value() == 55
        }
        1 * mockJobManagement.requirePlugin('gerrit-trigger')
    }

    def 'call upstream trigger methods'() {
        when:
        context.upstream('THE-JOB')

        then:
        with(context.triggerNodes[0]) {
            name() == 'jenkins.triggers.ReverseBuildTrigger'
            children().size() == 3
            spec[0].value().empty
            upstreamProjects[0].value() == 'THE-JOB'
            with(threshold[0]) {
                children().size() == 4
                name[0].value() == 'SUCCESS'
                ordinal[0].value() == 0
                color[0].value() == 'BLUE'
                completeBuild[0].value() == true
            }
        }
    }

    def 'call upstream trigger methods with threshold'() {
        when:
        context.upstream('THE-JOB', thresholdValue)

        then:
        with(context.triggerNodes[0]) {
            name() == 'jenkins.triggers.ReverseBuildTrigger'
            children().size() == 3
            spec[0].value().empty
            upstreamProjects[0].value() == 'THE-JOB'
            with(threshold[0]) {
                children().size() == 4
                name[0].value() == thresholdValue
                ordinal[0].value() == ordinalValue
                color[0].value() == colorValue
                completeBuild[0].value() == true
            }
        }

        where:
        thresholdValue | ordinalValue | colorValue
        'SUCCESS'      | 0            | 'BLUE'
        'UNSTABLE'     | 1            | 'YELLOW'
        'FAILURE'      | 2            | 'RED'
    }

    def 'call upstream trigger methods with bad args'() {
        when:
        context.upstream(projects, threshold)

        then:
        thrown(DslScriptException)

        where:
        projects | threshold
        'foo'    | 'bar'
        ''       | 'SUCCESS'
        null     | 'UNSTABLE'
    }

    def 'call bitbucket trigger'() {
        when:
        context.bitbucketPush()

        then:
        context.triggerNodes.size() == 1
        with(context.triggerNodes[0]) {
            name() == 'com.cloudbees.jenkins.plugins.BitBucketTrigger'
            children().size() == 1
            spec[0].value().empty
        }
        1 * mockJobManagement.requireMinimumPluginVersion('bitbucket', '1.1.2')
    }

    def 'call gitlabPush trigger with no options'() {
        when:
        context.gitlabPush {
        }

        then:
        with(context.triggerNodes[0]) {
            name() == 'com.dabsquared.gitlabjenkins.GitLabPushTrigger'
            children().size() == 14
            spec[0].value().empty
            triggerOnPush[0].value() == true
            triggerOnMergeRequest[0].value() == true
            triggerOpenMergeRequestOnPush[0].value() == 'never'
            ciSkip[0].value() == true
            setBuildDescription[0].value() == true
            addNoteOnMergeRequest[0].value() == true
            addCiMessage[0].value() == false
            addVoteOnMergeRequest[0].value() == true
            branchFilterType[0].value() == 'All'
            includeBranchesSpec[0].value().empty
            excludeBranchesSpec[0].value().empty
            targetBranchRegex[0].value().empty
            acceptMergeRequestOnSuccess[0].value() == false
        }
        1 * mockJobManagement.requireMinimumPluginVersion('gitlab-plugin', '1.2.0')
        1 * mockJobManagement.logPluginDeprecationWarning('gitlab-plugin', '1.4.0')
    }

    def 'call gitlabPush trigger with no options and newer plugin version'() {
        setup:
        mockJobManagement.isMinimumPluginVersionInstalled('gitlab-plugin', '1.2.4') >> true

        when:
        context.gitlabPush {
        }

        then:
        with(context.triggerNodes[0]) {
            name() == 'com.dabsquared.gitlabjenkins.GitLabPushTrigger'
            children().size() == 17
            spec[0].value().empty
            triggerOnPush[0].value() == true
            triggerOnMergeRequest[0].value() == true
            triggerOpenMergeRequestOnPush[0].value() == 'never'
            ciSkip[0].value() == true
            setBuildDescription[0].value() == true
            addNoteOnMergeRequest[0].value() == true
            addCiMessage[0].value() == false
            addVoteOnMergeRequest[0].value() == true
            branchFilterType[0].value() == 'All'
            includeBranchesSpec[0].value().empty
            excludeBranchesSpec[0].value().empty
            targetBranchRegex[0].value().empty
            acceptMergeRequestOnSuccess[0].value() == false
            triggerOnNoteRequest[0].value() == true
            noteRegex[0].value() == 'Jenkins please retry a build'
            skipWorkInProgressMergeRequest[0].value() == true
        }
        1 * mockJobManagement.requireMinimumPluginVersion('gitlab-plugin', '1.2.0')
        1 * mockJobManagement.logPluginDeprecationWarning('gitlab-plugin', '1.4.0')
    }

    def 'call gitlabPush trigger with all options and name based filter'() {
        when:
        context.gitlabPush {
            buildOnMergeRequestEvents(value)
            buildOnPushEvents(value)
            enableCiSkip(value)
            setBuildDescription(value)
            addNoteOnMergeRequest(value)
            rebuildOpenMergeRequest('both')
            addVoteOnMergeRequest(value)
            useCiFeatures(value)
            acceptMergeRequestOnSuccess(value)
            includeBranches('include1,include2')
            excludeBranches('exclude1,exclude2')
        }

        then:
        with(context.triggerNodes[0]) {
            name() == 'com.dabsquared.gitlabjenkins.GitLabPushTrigger'
            children().size() == 14
            spec[0].value().empty
            triggerOnPush[0].value() == value
            triggerOnMergeRequest[0].value() == value
            triggerOpenMergeRequestOnPush[0].value() == 'both'
            ciSkip[0].value() == value
            setBuildDescription[0].value() == value
            addNoteOnMergeRequest[0].value() == value
            addCiMessage[0].value() == value
            addVoteOnMergeRequest[0].value() == value
            branchFilterType[0].value() == 'NameBasedFilter'
            includeBranchesSpec[0].value() == 'include1,include2'
            excludeBranchesSpec[0].value() == 'exclude1,exclude2'
            targetBranchRegex[0].value().empty
            acceptMergeRequestOnSuccess[0].value() == value
        }
        1 * mockJobManagement.requireMinimumPluginVersion('gitlab-plugin', '1.2.0')
        1 * mockJobManagement.logPluginDeprecationWarning('gitlab-plugin', '1.4.0')
        4 * mockJobManagement.logDeprecationWarning()

        where:
        value << [true, false]
    }

    def 'call gitlabPush trigger with all options, name based filter and newer plugin version'() {
        setup:
        mockJobManagement.isMinimumPluginVersionInstalled('gitlab-plugin', '1.2.4') >> true

        when:
        context.gitlabPush {
            buildOnMergeRequestEvents(value)
            buildOnPushEvents(value)
            enableCiSkip(value)
            setBuildDescription(value)
            addNoteOnMergeRequest(value)
            rebuildOpenMergeRequest('both')
            addVoteOnMergeRequest(value)
            useCiFeatures(value)
            acceptMergeRequestOnSuccess(value)
            includeBranches('include1,include2')
            excludeBranches('exclude1,exclude2')
            commentTrigger(null)
            skipWorkInProgressMergeRequest(value)
        }

        then:
        with(context.triggerNodes[0]) {
            name() == 'com.dabsquared.gitlabjenkins.GitLabPushTrigger'
            children().size() == 17
            spec[0].value().empty
            triggerOnPush[0].value() == value
            triggerOnMergeRequest[0].value() == value
            triggerOpenMergeRequestOnPush[0].value() == 'both'
            ciSkip[0].value() == value
            setBuildDescription[0].value() == value
            addNoteOnMergeRequest[0].value() == value
            addCiMessage[0].value() == value
            addVoteOnMergeRequest[0].value() == value
            branchFilterType[0].value() == 'NameBasedFilter'
            includeBranchesSpec[0].value() == 'include1,include2'
            excludeBranchesSpec[0].value() == 'exclude1,exclude2'
            targetBranchRegex[0].value().empty
            acceptMergeRequestOnSuccess[0].value() == value
            triggerOnNoteRequest[0].value() == false
            noteRegex[0].value() == ''
            skipWorkInProgressMergeRequest[0].value() == value
        }
        1 * mockJobManagement.requireMinimumPluginVersion('gitlab-plugin', '1.2.0')
        1 * mockJobManagement.logPluginDeprecationWarning('gitlab-plugin', '1.4.0')
        4 * mockJobManagement.logDeprecationWarning()

        where:
        value << [true, false]
    }

    def 'call gitlabPush trigger with all options and regex based filter'() {
        when:
        context.gitlabPush {
            buildOnMergeRequestEvents(value)
            buildOnPushEvents(value)
            enableCiSkip(value)
            setBuildDescription(value)
            addNoteOnMergeRequest(value)
            rebuildOpenMergeRequest('both')
            addVoteOnMergeRequest(value)
            useCiFeatures(value)
            acceptMergeRequestOnSuccess(value)
            targetBranchRegex('(.*debug.*|.*release.*)')
        }

        then:
        with(context.triggerNodes[0]) {
            name() == 'com.dabsquared.gitlabjenkins.GitLabPushTrigger'
            children().size() == 14
            spec[0].value().empty
            triggerOnPush[0].value() == value
            triggerOnMergeRequest[0].value() == value
            triggerOpenMergeRequestOnPush[0].value() == 'both'
            ciSkip[0].value() == value
            setBuildDescription[0].value() == value
            addNoteOnMergeRequest[0].value() == value
            addCiMessage[0].value() == value
            addVoteOnMergeRequest[0].value() == value
            branchFilterType[0].value() == 'RegexBasedFilter'
            includeBranchesSpec[0].value().empty
            excludeBranchesSpec[0].value().empty
            targetBranchRegex[0].value() == '(.*debug.*|.*release.*)'
            acceptMergeRequestOnSuccess[0].value() == value
        }
        1 * mockJobManagement.requireMinimumPluginVersion('gitlab-plugin', '1.2.0')
        1 * mockJobManagement.logPluginDeprecationWarning('gitlab-plugin', '1.4.0')
        4 * mockJobManagement.logDeprecationWarning()

        where:
        value << [true, false]
    }

    def 'call gitlabPush trigger with invalid rebuildOpenMergeRequest'() {
        when:
        context.gitlabPush {
            rebuildOpenMergeRequest('FOO')
        }

        then:
        thrown(DslScriptException)
    }

    def 'call build result trigger with minimal options'() {
        when:
        context.buildResult('H/10 * * * *') {
        }

        then:
        context.triggerNodes.size() == 1
        with(context.triggerNodes[0]) {
            name() == 'org.jenkinsci.plugins.buildresulttrigger.BuildResultTrigger'
            children().size() == 2
            spec[0].value() == 'H/10 * * * *'
            combinedJobs[0].value() == false
        }
        1 * mockJobManagement.requireMinimumPluginVersion('buildresult-trigger', '0.17')
    }

    def 'call build result trigger with multiple job triggers'() {
        when:
        context.buildResult('H/10 * * * *') {
            combinedJobs()
            triggerInfo('job-1', SUCCESS, UNSTABLE, FAILURE, NOT_BUILT, ABORTED)
            triggerInfo('job-2')
        }

        then:
        context.triggerNodes.size() == 1
        with(context.triggerNodes[0]) {
            name() == 'org.jenkinsci.plugins.buildresulttrigger.BuildResultTrigger'
            children().size() == 3
            spec[0].value() == 'H/10 * * * *'
            combinedJobs[0].value() == true
            with(jobsInfo[0]) {
                name() == 'jobsInfo'
                children().size() == 2
                with(children()[0]) {
                    name() == 'org.jenkinsci.plugins.buildresulttrigger.model.BuildResultTriggerInfo'
                    children().size() == 2
                    jobNames[0].value() == 'job-1'
                    with(checkedResults[0]) {
                        children().size() == 5
                        with(children()[0]) {
                            name() == 'org.jenkinsci.plugins.buildresulttrigger.model.CheckedResult'
                            children().size() == 1
                            checked[0].value() == 'SUCCESS'
                        }
                        with(children()[1]) {
                            name() == 'org.jenkinsci.plugins.buildresulttrigger.model.CheckedResult'
                            children().size() == 1
                            checked[0].value() == 'UNSTABLE'
                        }
                        with(children()[2]) {
                            name() == 'org.jenkinsci.plugins.buildresulttrigger.model.CheckedResult'
                            children().size() == 1
                            checked[0].value() == 'FAILURE'
                        }
                        with(children()[3]) {
                            name() == 'org.jenkinsci.plugins.buildresulttrigger.model.CheckedResult'
                            children().size() == 1
                            checked[0].value() == 'NOT_BUILT'
                        }
                        with(children()[4]) {
                            name() == 'org.jenkinsci.plugins.buildresulttrigger.model.CheckedResult'
                            children().size() == 1
                            checked[0].value() == 'ABORTED'
                        }
                    }
                }
                with(children()[1]) {
                    name() == 'org.jenkinsci.plugins.buildresulttrigger.model.BuildResultTriggerInfo'
                    children().size() == 1
                    jobNames[0].value() == 'job-2'
                }
            }
        }
        1 * mockJobManagement.requireMinimumPluginVersion('buildresult-trigger', '0.17')
    }

    def 'call build result trigger without jobs names'() {
        when:
        context.buildResult('H/10 * * * *') {
            triggerInfo(jobs)
        }

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ 'Jobs names are required'

        where:
        jobs << ['', null]
    }
}
