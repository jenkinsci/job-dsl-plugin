package javaposse.jobdsl.dsl.helpers.triggers

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

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

    def 'call urltrigger with proxy, etag and last modified check'() {
        when:
        context.urlTrigger {
            url('http://www.example.com/some/url') {
                proxy true
                check 'etag'
                check 'lastModified'
            }
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        1 * mockJobManagement.requirePlugin('urltrigger')
    }

    def 'call urltrigger with inspection for content change'() {
        when:
        context.urlTrigger {

            url('http://www.example.com/some/other/url') {
                inspection 'change'
            }

        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def utc = context.triggerNodes[0]
        utc.entries != null
        utc.entries.size() == 1
        def entry = utc.entries[0].'org.jenkinsci.plugins.urltrigger.URLTriggerEntry'[0]
        entry.inspectingContent[0].value() == true
        entry.contentTypes != null
        entry.contentTypes.size() == 1
        entry.contentTypes[0].'org.jenkinsci.plugins.urltrigger.content.SimpleContentType' != null
        1 * mockJobManagement.requirePlugin('urltrigger')
    }

    def 'call urltrigger with JSON path inspection'() {
        when:
        context.urlTrigger {
            url('http://www.example.com/some/other/url') {
                inspection('json') {
                    path('/foo/bar')
                    path('/*/baz')
                }
            }
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def utc = context.triggerNodes[0]
        utc.entries != null
        utc.entries.size() == 1
        def entry = utc.entries[0].'org.jenkinsci.plugins.urltrigger.URLTriggerEntry'[0]
        entry.inspectingContent[0].value() == true
        entry.contentTypes != null
        entry.contentTypes.size() == 1
        entry.contentTypes[0].'org.jenkinsci.plugins.urltrigger.content.JSONContentType' != null
        def ct = entry.contentTypes[0].'org.jenkinsci.plugins.urltrigger.content.JSONContentType'[0]
        ct.jsonPaths != null
        ct.jsonPaths.size() == 1
        def paths = ct.jsonPaths[0]
        def contentEntries = paths.'org.jenkinsci.plugins.urltrigger.content.JSONContentEntry'
        contentEntries != null
        contentEntries.size() == 2
        contentEntries[0].jsonPath != null
        contentEntries[0].jsonPath[0].value() == '/foo/bar'
        contentEntries[1].jsonPath != null
        contentEntries[1].jsonPath[0].value() == '/*/baz'
        1 * mockJobManagement.requirePlugin('urltrigger')
    }

    def 'call urltrigger with XML path inspection'() {
        when:
        context.urlTrigger {
            url('http://www.example.com/some/other/url') {
                inspection('xml') {
                    path('//*[@name="foo"]')
                }
            }
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def utc = context.triggerNodes[0]
        utc.entries != null
        utc.entries.size() == 1
        def entry = utc.entries[0].'org.jenkinsci.plugins.urltrigger.URLTriggerEntry'[0]
        entry.inspectingContent[0].value() == true
        entry.contentTypes != null
        entry.contentTypes.size() == 1
        entry.contentTypes[0].'org.jenkinsci.plugins.urltrigger.content.XMLContentType' != null
        def ct = entry.contentTypes[0].'org.jenkinsci.plugins.urltrigger.content.XMLContentType'[0]
        ct.xPaths != null
        ct.xPaths.size() == 1
        def paths = ct.xPaths[0]
        def contentEntries = paths.'org.jenkinsci.plugins.urltrigger.content.XMLContentEntry'
        contentEntries != null
        contentEntries.size() == 1
        contentEntries[0].xPath != null
        contentEntries[0].xPath[0].value() == '//*[@name="foo"]'
        1 * mockJobManagement.requirePlugin('urltrigger')
    }

    def 'call urltrigger with TEXT regex inspection'() {
        when:
        context.urlTrigger {
            url('http://www.example.com/some/other/url') {
                inspection('text') {
                    regexp('_(foo|bar).+')
                }
            }
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def utc = context.triggerNodes[0]
        utc.entries != null
        utc.entries.size() == 1
        def entry = utc.entries[0].'org.jenkinsci.plugins.urltrigger.URLTriggerEntry'[0]
        entry.inspectingContent[0].value() == true
        entry.contentTypes != null
        entry.contentTypes.size() == 1
        entry.contentTypes[0].'org.jenkinsci.plugins.urltrigger.content.TEXTContentType' != null
        def ct = entry.contentTypes[0].'org.jenkinsci.plugins.urltrigger.content.TEXTContentType'[0]
        ct.regExElements != null
        ct.regExElements.size() == 1
        def paths = ct.regExElements[0]
        def contentEntries = paths.'org.jenkinsci.plugins.urltrigger.content.TEXTContentEntry'
        contentEntries != null
        contentEntries.size() == 1
        contentEntries[0].regEx != null
        contentEntries[0].regEx[0].value() == '_(foo|bar).+'
        1 * mockJobManagement.requirePlugin('urltrigger')
    }

    def 'call urltrigger methods with defaults and check for response status'() {
        when:
        context.urlTrigger {
            url('http://www.example.com/some/url') {
                check 'status'
            }
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def utc = context.triggerNodes[0]
        utc.spec[0].value() == 'H/5 * * * *'
        utc.labelRestriction != null
        utc.labelRestriction.size() == 1
        utc.labelRestriction[0].value() == false
        utc.entries != null
        utc.entries.size() == 1
        def entry = utc.entries[0].'org.jenkinsci.plugins.urltrigger.URLTriggerEntry'[0]
        entry.url != null
        entry.url.size() == 1
        entry.url[0].value() == 'http://www.example.com/some/url'
        entry.statusCode[0].value() == 200
        entry.timeout[0].value() == 300
        entry.checkStatus[0].value() == true
        1 * mockJobManagement.requirePlugin('urltrigger')
    }

    def 'call urltrigger methods with non-default status code and timeout'() {
        when:
        context.urlTrigger {
            url('http://www.example.com/some/url') {
                status 404
                timeout 6000
            }
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def utc = context.triggerNodes[0]
        utc.entries != null
        utc.entries.size() == 1
        def entry = utc.entries[0].'org.jenkinsci.plugins.urltrigger.URLTriggerEntry'[0]
        entry.url != null
        entry.url.size() == 1
        entry.url[0].value() == 'http://www.example.com/some/url'
        entry.statusCode[0].value() == 404
        entry.timeout[0].value() == 6000
        1 * mockJobManagement.requirePlugin('urltrigger')
    }

    def 'call urltrigger methods with non-default cron'() {
        when:
        context.urlTrigger {
            cron '* 0 * 0 *'
            url 'http://www.example.com/some/url'
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def utc = context.triggerNodes[0]
        utc.spec[0].value() == '* 0 * 0 *'
        1 * mockJobManagement.requirePlugin('urltrigger')
    }

    def 'call urltrigger methods with label restriction'() {
        when:
        context.urlTrigger {
            restrictToLabel 'foo'
            url 'http://www.example.com/some/url'
        }

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def utc = context.triggerNodes[0]
        utc.labelRestriction[0].value() == true
        utc.triggerLabel[0].value() == 'foo'
        1 * mockJobManagement.requirePlugin('urltrigger')
    }

    def 'call cron trigger methods'() {
        when:
        context.cron('*/10 * * * *')

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def timerTrigger = context.triggerNodes[0]
        timerTrigger.name() == 'hudson.triggers.TimerTrigger'
        timerTrigger.spec[0].value() == '*/10 * * * *'
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

    def 'call pull request trigger with no args'() {
        when:
        context.pullRequest {
        }

        then:
        with(context.triggerNodes[0]) {
            name() == 'org.jenkinsci.plugins.ghprb.GhprbTrigger'
            children().size() == 13
            onlyTriggerPhrase[0].value() == false
            useGitHubHooks[0].value() == false
            allowMembersOfWhitelistedOrgsAsAdmin[0].value() == false
            permitAll[0].value() == false
            autoCloseFailedPullRequests[0].value() == false
            cron[0].value() == 'H/5 * * * *'
            spec[0].value() == 'H/5 * * * *'
            triggerPhrase[0].value() == ''
            adminlist[0].value() == ''
            whitelist[0].value() == ''
            orgslist[0].value() == ''
            commentFilePath[0].value() == ''
        }
        1 * mockJobManagement.requirePlugin('ghprb')
    }

    def 'call pull request trigger with plugin version 1.13'() {
        setup:
        mockJobManagement.getPluginVersion('ghprb') >> new VersionNumber('1.13')

        when:
        context.pullRequest {
        }

        then:
        with(context.triggerNodes[0]) {
            name() == 'org.jenkinsci.plugins.ghprb.GhprbTrigger'
            children().size() == 10
            onlyTriggerPhrase[0].value() == false
            useGitHubHooks[0].value() == false
            permitAll[0].value() == false
            autoCloseFailedPullRequests[0].value() == false
            cron[0].value() == 'H/5 * * * *'
            spec[0].value() == 'H/5 * * * *'
            triggerPhrase[0].value() == ''
            adminlist[0].value() == ''
            whitelist[0].value() == ''
            orgslist[0].value() == ''
        }
        1 * mockJobManagement.requirePlugin('ghprb')
        1 * mockJobManagement.logPluginDeprecationWarning('ghprb', '1.26')
    }

    def 'call pull request trigger with plugin version 1.14'() {
        setup:
        mockJobManagement.getPluginVersion('ghprb') >> new VersionNumber('1.14')

        when:
        context.pullRequest {
        }

        then:
        with(context.triggerNodes[0]) {
            name() == 'org.jenkinsci.plugins.ghprb.GhprbTrigger'
            children().size() == 11
            onlyTriggerPhrase[0].value() == false
            useGitHubHooks[0].value() == false
            permitAll[0].value() == false
            autoCloseFailedPullRequests[0].value() == false
            cron[0].value() == 'H/5 * * * *'
            spec[0].value() == 'H/5 * * * *'
            triggerPhrase[0].value() == ''
            adminlist[0].value() == ''
            whitelist[0].value() == ''
            orgslist[0].value() == ''
            commentFilePath[0].value() == ''
        }
        1 * mockJobManagement.requirePlugin('ghprb')
        1 * mockJobManagement.logPluginDeprecationWarning('ghprb', '1.26')
    }

    def 'call pull request trigger with multiple admins and orgs'() {
        when:
        context.pullRequest {
            admins(['test1', 'test2'])
            userWhitelist(['test1', 'test2'])
            orgWhitelist(['test1', 'test2'])
        }

        then:
        with(context.triggerNodes[0]) {
            name() == 'org.jenkinsci.plugins.ghprb.GhprbTrigger'
            children().size() == 13
            adminlist[0].value() == 'test1\ntest2'
            whitelist[0].value() == 'test1\ntest2'
            orgslist[0].value() == 'test1\ntest2'
        }
        1 * mockJobManagement.requirePlugin('ghprb')
    }

    def 'call pull request trigger with all args'() {
        when:
        context.pullRequest {
            admins(['test'])
            userWhitelist(['test'])
            orgWhitelist(['test'])
            cron('*/5 * * * *')
            triggerPhrase('ok to test')
            onlyTriggerPhrase(true)
            useGitHubHooks(true)
            allowMembersOfWhitelistedOrgsAsAdmin(true)
            permitAll(true)
            autoCloseFailedPullRequests(true)
            commentFilePath('myCommentFile')
        }

        then:
        with(context.triggerNodes[0]) {
            name() == 'org.jenkinsci.plugins.ghprb.GhprbTrigger'
            children().size() == 13
            adminlist[0].value() == 'test'
            whitelist[0].value() == 'test'
            orgslist[0].value() == 'test'
            cron[0].value() == '*/5 * * * *'
            spec[0].value() == '*/5 * * * *'
            triggerPhrase[0].value() == 'ok to test'
            onlyTriggerPhrase[0].value() == true
            useGitHubHooks[0].value() == true
            allowMembersOfWhitelistedOrgsAsAdmin[0].value() == true
            permitAll[0].value() == true
            autoCloseFailedPullRequests[0].value() == true
            commentFilePath[0].value() == 'myCommentFile'
        }
        1 * mockJobManagement.requirePlugin('ghprb')
        1 * mockJobManagement.requireMinimumPluginVersion('ghprb', '1.14')
        1 * mockJobManagement.requireMinimumPluginVersion('ghprb', '1.15-0')
    }

    def 'call pull request trigger with commit status extension'() {
        when:
        context.pullRequest {
            extensions {
                commitStatus {
                    delegate.context('Deploy to staging site')
                    triggeredStatus('deploy triggered')
                    startedStatus('deploy started')
                    statusUrl('http://mysite.com')
                    completedStatus('SUCCESS', 'All is well')
                    completedStatus('FAILURE', 'Something has gone wrong')
                }
            }
        }

        then:
        with(context.triggerNodes[0]) {
            name() == 'org.jenkinsci.plugins.ghprb.GhprbTrigger'
            children().size() == 13
            adminlist[0].value() == ''
            whitelist[0].value() == ''
            orgslist[0].value() == ''
            cron[0].value() == 'H/5 * * * *'
            spec[0].value() == 'H/5 * * * *'
            triggerPhrase[0].value() == ''
            onlyTriggerPhrase[0].value() == false
            useGitHubHooks[0].value() == false
            allowMembersOfWhitelistedOrgsAsAdmin[0].value() == false
            permitAll[0].value() == false
            autoCloseFailedPullRequests[0].value() == false
            commentFilePath[0].value() == ''
            with(extensions[0]) {
                children().size() == 1
                with(children()[0]) {
                    name() == 'org.jenkinsci.plugins.ghprb.extensions.status.GhprbSimpleStatus'
                    children().size() == 5
                    commitStatusContext[0].value() == 'Deploy to staging site'
                    triggeredStatus[0].value() == 'deploy triggered'
                    startedStatus[0].value() == 'deploy started'
                    statusUrl[0].value() == 'http://mysite.com'
                    with(completedStatus[0]) {
                        children().size() == 2
                        with(children()[0]) {
                            name() == 'org.jenkinsci.plugins.ghprb.extensions.comments.GhprbBuildResultMessage'
                            children().size() == 2
                            result[0].value() == 'SUCCESS'
                            message[0].value() == 'All is well'
                        }
                        with(children()[1]) {
                            name() == 'org.jenkinsci.plugins.ghprb.extensions.comments.GhprbBuildResultMessage'
                            children().size() == 2
                            result[0].value() == 'FAILURE'
                            message[0].value() == 'Something has gone wrong'
                        }
                    }
                }
            }
        }
        1 * mockJobManagement.requirePlugin('ghprb')
        1 * mockJobManagement.requireMinimumPluginVersion('ghprb', '1.26')
        1 * mockJobManagement.logPluginDeprecationWarning('ghprb', '1.26')
    }

    def 'call pull request trigger invalid build result'() {
        when:
        context.pullRequest {
            extensions {
                commitStatus {
                    completedStatus(buildResult, 'Something has gone wrong')
                }
            }
        }

        then:
        thrown(DslScriptException)

        where:
        buildResult << [null, '', 'FOO']
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

    def 'call rundeck trigger with default options'() {
        when:
        context.rundeck()

        then:
        with(context.triggerNodes[0]) {
            name() == 'org.jenkinsci.plugins.rundeck.RundeckTrigger'
            children().size() == 4
            spec[0].value().empty
            filterJobs[0].value() == false
            jobsIdentifiers[0].value().empty
            executionStatuses[0].value().empty
        }
        1 * mockJobManagement.requireMinimumPluginVersion('rundeck', '3.4')
    }

    def 'call rundeck trigger with all options'() {
        when:
        context.rundeck {
            jobIdentifiers('2027ce89-7924-4ecf-a963-30090ada834f', 'my-project-name:main-group/sub-group/my-job-name')
            executionStatuses('FAILED', 'ABORTED')
        }

        then:
        with(context.triggerNodes[0]) {
            name() == 'org.jenkinsci.plugins.rundeck.RundeckTrigger'
            children().size() == 4
            spec[0].value().empty
            filterJobs[0].value() == true
            with(jobsIdentifiers[0]) {
                children().size() == 2
                string[0].value() == '2027ce89-7924-4ecf-a963-30090ada834f'
                string[1].value() == 'my-project-name:main-group/sub-group/my-job-name'
            }
            with(executionStatuses[0]) {
                children().size() == 2
                children().any { it.name() == 'string' && it.value() == 'FAILED' }
                children().any { it.name() == 'string' && it.value() == 'ABORTED' }
            }
        }
        1 * mockJobManagement.requireMinimumPluginVersion('rundeck', '3.4')
    }

    def 'call rundeck trigger with invalid execution status'() {
        when:
        context.rundeck {
            executionStatuses('FOO')
        }

        then:
        thrown(DslScriptException)
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
}
