package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class BranchSourcesContextsSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final BranchSourcesContext context = new BranchSourcesContext(jobManagement, Mock(Item))

    def 'node from extension is added'() {
        setup:
        Node node = Mock(Node)

        when:
        context.addExtensionNode(node)

        then:
        context.branchSourceNodes[0] == node
    }

    def 'git with minimal options'() {
        when:
        context.git {}

        then:
        context.branchSourceNodes.size() == 1
        with(context.branchSourceNodes[0]) {
            name() == 'jenkins.branch.BranchSource'
            children().size() == 2
            with(source[0]) {
                children().size() == 6
                id[0].value() instanceof String
                remote[0].value().empty
                credentialsId[0].value().empty
                includes[0].value() == '*'
                excludes[0].value().empty
                ignoreOnPushNotifications[0].value() == false
            }
            with(strategy[0]) {
                children().size() == 1
                attribute('class') == 'jenkins.branch.DefaultBranchPropertyStrategy'
                properties[0].value() == []
                properties[0].attribute('class') == 'empty-list'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'git with all options'() {
        when:
        context.git {
            id('test')
            remote('foo')
            credentialsId('bar')
            includes('lorem')
            excludes('ipsum')
            ignoreOnPushNotifications()
        }

        then:
        context.branchSourceNodes.size() == 1
        with(context.branchSourceNodes[0]) {
            name() == 'jenkins.branch.BranchSource'
            children().size() == 2
            with(source[0]) {
                children().size() == 6
                id[0].value() == 'test'
                remote[0].value() == 'foo'
                credentialsId[0].value() == 'bar'
                includes[0].value() == 'lorem'
                excludes[0].value() == 'ipsum'
                ignoreOnPushNotifications[0].value() == true
            }
            with(strategy[0]) {
                children().size() == 1
                attribute('class') == 'jenkins.branch.DefaultBranchPropertyStrategy'
                properties[0].value() == []
                properties[0].attribute('class') == 'empty-list'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'github with minimal options'() {
        when:
        context.github {}

        then:
        context.branchSourceNodes.size() == 1
        with(context.branchSourceNodes[0]) {
            name() == 'jenkins.branch.BranchSource'
            children().size() == 2
            with(source[0]) {
                children().size() == 5
                id[0].value() instanceof String
                credentialsId[0].value().empty
                repoOwner[0].value().empty
                repository[0].value().empty
                with(traits[0]) {
                    children().size() == 3
                }
            }
            with(strategy[0]) {
                children().size() == 1
                attribute('class') == 'jenkins.branch.DefaultBranchPropertyStrategy'
                properties[0].value() == []
                properties[0].attribute('class') == 'empty-list'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('github-branch-source', '2.2.0')
    }

    def 'github with all options'() {
        when:
        context.github {
            id('test')
            apiUri('https://custom.url')
            scanCredentialsId('scanCreds')
            checkoutCredentialsId('checkoutCreds')
            repoOwner('ownerName')
            repository('repoName')
            includes('lorem')
            excludes('ipsum')
            buildOriginBranch(false)
            buildOriginBranchWithPR(false)
            buildOriginPRMerge()
            buildOriginPRHead()
            buildForkPRMerge(false)
            buildForkPRHead()
        }

        then:
        context.branchSourceNodes.size() == 1
        with(context.branchSourceNodes[0]) {
            name() == 'jenkins.branch.BranchSource'
            children().size() == 2
            with(source[0]) {
                children().size() == 6
                id[0].value() == 'test'
                apiUri[0].value() == 'https://custom.url'
                credentialsId[0].value() == 'scanCreds'
                repoOwner[0].value() == 'ownerName'
                repository[0].value() == 'repoName'
                with(traits[0]) {
                    children().size() == 4
                }
            }
            with(strategy[0]) {
                children().size() == 1
                attribute('class') == 'jenkins.branch.DefaultBranchPropertyStrategy'
                properties[0].value() == []
                properties[0].attribute('class') == 'empty-list'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('github-branch-source', '2.2.0')
    }
}
