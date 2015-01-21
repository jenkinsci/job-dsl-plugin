package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import spock.lang.Specification

class TriggerContextSpec extends Specification {
    List<WithXmlAction> mockActions = Mock()
    JobManagement mockJobManagement = Mock(JobManagement)
    TriggerContext context = new TriggerContext(mockActions, JobType.Freeform, mockJobManagement)

    def 'call github trigger methods'() {
        when:
        context.githubPush()

        then:
        context.triggerNodes != null
        context.triggerNodes.size() == 1
        def githubPushTrigger = context.triggerNodes[0]
        githubPushTrigger.name() == 'com.cloudbees.jenkins.GitHubPushTrigger'
        githubPushTrigger.spec[0].value() == ''
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

    def 'call pull request trigger with no args'() {
        when:
        context.pullRequest()

        then:
        def pullRequestNode = context.triggerNodes[0]
        with(pullRequestNode) {
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

    }

    def 'call pull request trigger with multiple admins and orgs'() {
        when:
        context.pullRequest {
            admins(['test1', 'test2'])
            userWhitelist(['test1', 'test2'])
            orgWhitelist(['test1', 'test2'])
        }

        then:
        def pullRequestNode = context.triggerNodes[0]
        with(pullRequestNode) {
            adminlist[0].value() == 'test1\ntest2'
            whitelist[0].value() == 'test1\ntest2'
            orgslist[0].value() == 'test1\ntest2'
        }
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
            permitAll(true)
            autoCloseFailedPullRequests(true)
        }

        then:
        def pullRequestNode = context.triggerNodes[0]
        with(pullRequestNode) {
            name() == 'org.jenkinsci.plugins.ghprb.GhprbTrigger'
            adminlist[0].value() == 'test'
            whitelist[0].value() == 'test'
            orgslist[0].value() == 'test'
            cron[0].value() == '*/5 * * * *'
            spec[0].value() == '*/5 * * * *'
            triggerPhrase[0].value() == 'ok to test'
            onlyTriggerPhrase[0].value() == true
            useGitHubHooks[0].value() == true
            permitAll[0].value() == true
            autoCloseFailedPullRequests[0].value() == true
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
    }

    def 'call advanced gerrit trigger methods'() {
        when:
        context.gerrit {
            events {
                ChangeMerged
                DraftPublished
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

        where:
        event << [
                'changeAbandoned', 'changeMerged', 'changeRestored', 'commentAdded', 'draftPublished',
                'patchsetCreated', 'refUpdated'
        ]
    }

    def 'call gerrit trigger with deprecated events'(String event) {
        when:
        context.gerrit {
            events {
                delegate."${event}"
            }
        }

        then:
        with(context.triggerNodes[0].triggerOnEvents[0]) {
            children().size() == 1
            children()[0].name() ==
                    "com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events.Plugin${event}Event"
        }

        where:
        event << [
                'ChangeAbandoned', 'ChangeMerged', 'ChangeRestored', 'CommentAdded', 'DraftPublished',
                'PatchsetCreated', 'RefUpdated'
        ]
    }

    def 'call gerrit trigger and verify build status value settings'() {
        when:
        context.gerrit {
            events {
                PatchsetCreated
                DraftPublished
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
    }

    def 'call gerrit trigger and verify build status value methods'() {
        when:
        context.gerrit {
            events {
                PatchsetCreated
                DraftPublished
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
    }

    def 'call snapshotDependencies for free-style job fails'() {
        when:
        context.snapshotDependencies(false)

        then:
        thrown(IllegalStateException)
    }

    def 'call snapshotDependencies for Maven job succeeds'() {
        when:
        TriggerContext context = new TriggerContext([], JobType.Maven, mockJobManagement)
        context.snapshotDependencies(false)

        then:
        context.withXmlActions != null
        context.withXmlActions.size() == 1
    }

    def 'call bitbucket pull request trigger with no args'() {
        when:
        context.bitbucketPullRequest()

        then:
        def pullRequestNode = context.triggerNodes[0]
        with(pullRequestNode) {
            name() == 'bitbucketpullrequestbuilder.bitbucketpullrequestbuilder.BitbucketBuildTrigger'
            cron[0].value() == 'H/5 * * * *'
            spec[0].value() == 'H/5 * * * *'
            username[0].value() == ''
            password[0].value() == ''
            repositoryOwner[0].value() == ''
            repositoryName[0].value() == ''
            ciSkipPhases[0].value() == ''
            checkDestinationCommit[0].value() == false
        }
    }

    def 'call bitbucket pull request trigger with all args'() {
        when:
        context.bitbucketPullRequest {
            cron('H/10 * * * *')
            username('bitbucketUsername')
            password('bitbucketPassword')
            repositoryOwner('bitbucketRepositoryOwner')
            repositoryName('bitbucketRepositoryName')
            ciSkipPhases('.*\\[skip\\W+ci\\].*')
            checkDestinationCommit(true)
        }

        then:
        def pullRequestNode = context.triggerNodes[0]
        with(pullRequestNode) {
            name() == 'bitbucketpullrequestbuilder.bitbucketpullrequestbuilder.BitbucketBuildTrigger'
            cron[0].value() == 'H/10 * * * *'
            spec[0].value() == 'H/10 * * * *'
            username[0].value() == 'bitbucketUsername'
            password[0].value() == 'bitbucketPassword'
            repositoryOwner[0].value() == 'bitbucketRepositoryOwner'
            repositoryName[0].value() == 'bitbucketRepositoryName'
            ciSkipPhases[0].value() == '.*\\[skip\\W+ci\\].*'
            checkDestinationCommit[0].value() == true
        }
    }
}
