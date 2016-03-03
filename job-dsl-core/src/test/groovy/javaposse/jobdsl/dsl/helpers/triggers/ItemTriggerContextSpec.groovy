package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class ItemTriggerContextSpec extends Specification {
    JobManagement mockJobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    ItemTriggerContext context = new ItemTriggerContext(mockJobManagement, item)

    def 'node from extension is added'() {
        setup:
        Node node = Mock(Node)

        when:
        context.addExtensionNode(node)

        then:
        context.triggerNodes[0] == node
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
        1 * mockJobManagement.requireMinimumPluginVersion('ghprb', '1.26')
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
        1 * mockJobManagement.requireMinimumPluginVersion('ghprb', '1.26')
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
        1 * mockJobManagement.requireMinimumPluginVersion('ghprb', '1.26')
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
        1 * mockJobManagement.requireMinimumPluginVersion('ghprb', '1.26')
        1 * mockJobManagement.logDeprecationWarning()
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

    def 'call dos trigger with all options'() {
        when:
        context.dos('*/10 * * * *') {
            triggerScript('set CAUSE=Build successfully triggered by dostrigger.')
        }

        then:
        context.triggerNodes.size() == 1
        with(context.triggerNodes[0]) {
            name() == 'org.jenkinsci.plugins.dostrigger.DosTrigger'
            children().size() == 3
            spec[0].value() == '*/10 * * * *'
            script[0].value() == 'set CAUSE=Build successfully triggered by dostrigger.'
            nextBuildNum[0].value() == 0
        }
        1 * mockJobManagement.requireMinimumPluginVersion('dos-trigger', '1.23')
    }

    def 'call dos trigger with minimal options'() {
        when:
        context.dos('*/10 * * * *') {
        }

        then:
        context.triggerNodes.size() == 1
        with(context.triggerNodes[0]) {
            name() == 'org.jenkinsci.plugins.dostrigger.DosTrigger'
            children().size() == 3
            spec[0].value() == '*/10 * * * *'
            script[0].value() == ''
            nextBuildNum[0].value() == 0
        }
        1 * mockJobManagement.requireMinimumPluginVersion('dos-trigger', '1.23')
    }

    def 'call dos trigger without schedule'() {
        when:
        context.dos(schedule) {
        }

        then:
        Exception e = thrown(DslScriptException)
        e.message ==~ /\(.+, line \d+\) cronString must be specified/

        where:
        schedule << [null, '']
    }
}
