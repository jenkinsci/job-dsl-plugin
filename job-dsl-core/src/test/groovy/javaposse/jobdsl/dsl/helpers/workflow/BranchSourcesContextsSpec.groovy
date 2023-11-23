package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.DslException
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

    def 'git with missing id'() {
        when:
        context.git {}

        then:
        Exception e = thrown DslException
        e.message =~ 'id must be specified'
    }

    def 'git with minimal options'() {
        when:
        context.git {
            id('test')
        }

        then:
        context.branchSourceNodes.size() == 1
        with(context.branchSourceNodes[0]) {
            name() == 'jenkins.branch.BranchSource'
            children().size() == 2
            with(source[0]) {
                children().size() == 6
                id[0].value() == 'test'
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

    def 'github with missing id'() {
        when:
        context.github {}

        then:
        Exception e = thrown DslException
        e.message =~ 'id must be specified'
    }

    def 'github with minimal options'() {
        when:
        context.github {
            id('test')
        }

        then:
        context.branchSourceNodes.size() == 1
        with(context.branchSourceNodes[0]) {
            name() == 'jenkins.branch.BranchSource'
            children().size() == 2
            with(source[0]) {
                children().size() == 13
                id[0].value() == 'test'
                scanCredentialsId[0].value().empty
                checkoutCredentialsId[0].value() == 'SAME'
                repoOwner[0].value().empty
                repository[0].value().empty
                includes[0].value() == '*'
                excludes[0].value().empty
                buildOriginBranch[0].value() == true
                buildOriginBranchWithPR[0].value() == true
                buildOriginPRMerge[0].value() == false
                buildOriginPRHead[0].value() == false
                buildForkPRMerge[0].value() == true
                buildForkPRHead[0].value() == false
            }
            with(strategy[0]) {
                children().size() == 1
                attribute('class') == 'jenkins.branch.DefaultBranchPropertyStrategy'
                properties[0].value() == []
                properties[0].attribute('class') == 'empty-list'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('github-branch-source', '1.8')
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
                children().size() == 14
                id[0].value() == 'test'
                apiUri[0].value() == 'https://custom.url'
                scanCredentialsId[0].value() == 'scanCreds'
                checkoutCredentialsId[0].value() == 'checkoutCreds'
                repoOwner[0].value() == 'ownerName'
                repository[0].value() == 'repoName'
                includes[0].value() == 'lorem'
                excludes[0].value() == 'ipsum'
                buildOriginBranch[0].value() == false
                buildOriginBranchWithPR[0].value() == false
                buildOriginPRMerge[0].value() == true
                buildOriginPRHead[0].value() == true
                buildForkPRMerge[0].value() == false
                buildForkPRHead[0].value() == true
            }
            with(strategy[0]) {
                children().size() == 1
                attribute('class') == 'jenkins.branch.DefaultBranchPropertyStrategy'
                properties[0].value() == []
                properties[0].attribute('class') == 'empty-list'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('github-branch-source', '1.8')
    }

    def 'gitlab with missing id'() {
        when:
        context.gitlab {}

        then:
        Exception e = thrown DslException
        e.message =~ 'id must be specified'
    }

    def 'gitlab with minimal options'() {
        when:
        context.gitlab {
            id('test')
        }

        then:
        context.branchSourceNodes.size() == 1
        with(context.branchSourceNodes[0]) {
            name() == 'jenkins.branch.BranchSource'
            children().size() == 2
            with(source[0]) {
                children().size() == 5
                id[0].value() == 'test'
                serverName[0].value().empty
                credentialsId[0].value().empty
                projectOwner[0].value().empty
                projectPath[0].value().empty
            }
            with(strategy[0]) {
                children().size() == 1
                attribute('class') == 'jenkins.branch.DefaultBranchPropertyStrategy'
                properties[0].value() == []
                properties[0].attribute('class') == 'empty-list'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('gitlab-branch-source', '670.vf7df45517001')
    }

    def 'gitlab with all options'() {
        when:
        context.gitlab {
            id('test')
            serverName('GitLab')
            credentialsId('checkoutCreds')
            projectOwner('ownerName')
            projectPath('ownerName/projectName')
        }

        then:
        context.branchSourceNodes.size() == 1
        with(context.branchSourceNodes[0]) {
            name() == 'jenkins.branch.BranchSource'
            children().size() == 2
            with(source[0]) {
                children().size() == 5
                id[0].value() == 'test'
                serverName[0].value() == 'GitLab'
                credentialsId[0].value() == 'checkoutCreds'
                projectOwner[0].value() == 'ownerName'
                projectPath[0].value() == 'ownerName/projectName'
            }
            with(strategy[0]) {
                children().size() == 1
                attribute('class') == 'jenkins.branch.DefaultBranchPropertyStrategy'
                properties[0].value() == []
                properties[0].attribute('class') == 'empty-list'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('gitlab-branch-source', '670.vf7df45517001')
    }
}
