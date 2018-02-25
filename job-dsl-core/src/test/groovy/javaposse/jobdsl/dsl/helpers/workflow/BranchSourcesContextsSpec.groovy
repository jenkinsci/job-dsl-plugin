package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class BranchSourcesContextsSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    BranchSourcesContext context = new BranchSourcesContext(jobManagement, Mock(Item))

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

    def 'git with default branch property strategy'() {
        when:
        context.git {
            defaultBranchPropertyStrategy {
                noTriggerBranchProperty()
            }
        }

        then:
        with(context.branchSourceNodes[0]) {
            with(strategy[0]) {
                children().size() == 1
                attribute('class') == 'jenkins.branch.DefaultBranchPropertyStrategy'
                with('properties'[0]) {
                    children().size() == 1
                    with('jenkins.branch.NoTriggerBranchProperty'[0]) {
                    }
                }
            }
        }
    }

    def 'git with default branch property strategy and multiple properties'() {
        when:
        context.git {
            defaultBranchPropertyStrategy {
                noTriggerBranchProperty()
                noTriggerBranchProperty()
            }
        }

        then:
        with(context.branchSourceNodes[0]) {
            with(strategy[0]) {
                children().size() == 1
                attribute('class') == 'jenkins.branch.DefaultBranchPropertyStrategy'
                with(getByName('properties')[0]) {
                    children().size() == 2
                    with('jenkins.branch.NoTriggerBranchProperty'[0]) {
                    }
                    with('jenkins.branch.NoTriggerBranchProperty'[1]) {
                    }
                }
            }
        }
    }

    def 'git with named exceptions branch property strategy'() {
        when:
        context.git {
            namedExceptionsBranchPropertyStrategy {
                defaultProperties {
                    noTriggerBranchProperty()
                }

                namedException {
                    branch('dev')
                }
            }
        }

        then:
        with(context.branchSourceNodes[0]) {
            with(strategy[0]) {
                children().size() == 2
                attribute('class') == 'jenkins.branch.NamedExceptionsBranchPropertyStrategy'
                with(defaultProperties[0]) {
                    children().size() == 1
                    with('jenkins.branch.NoTriggerBranchProperty'[0]) {
                    }
                }
                with(namedExceptions[0]) {
                    children().size() == 1
                    with(getByName('jenkins.branch.NamedExceptionsBranchPropertyStrategy_-Named')[0]) {
                        children().size() == 2
                        with(props[0]) {
                            value() == []
                            attribute('class') == 'empty-list'
                        }
                        with('name'[0]) {
                            text() == 'dev'
                        }
                    }
                }
            }
        }
    }

    def 'git with named exceptions branch property strategy and multiple named exceptions'() {
        when:
        context.git {
            namedExceptionsBranchPropertyStrategy {
                defaultProperties {
                    noTriggerBranchProperty()
                }

                namedException {
                    branch('dev')
                }

                namedException {
                    branch('release')
                }
            }
        }

        then:
        with(context.branchSourceNodes[0]) {
            with(strategy[0]) {
                children().size() == 2
                attribute('class') == 'jenkins.branch.NamedExceptionsBranchPropertyStrategy'
                with(defaultProperties[0]) {
                    children().size() == 1
                    with('jenkins.branch.NoTriggerBranchProperty'[0]) {
                    }
                }
                with(namedExceptions[0]) {
                    children().size() == 2
                    with(getByName('jenkins.branch.NamedExceptionsBranchPropertyStrategy_-Named')[0]) {
                        children().size() == 2
                        with(props[0]) {
                            value() == []
                            attribute('class') == 'empty-list'
                        }
                        with('name'[0]) {
                            text() == 'dev'
                        }
                    }
                    with(getByName('jenkins.branch.NamedExceptionsBranchPropertyStrategy_-Named')[1]) {
                        children().size() == 2
                        with(props[0]) {
                            value() == []
                            attribute('class') == 'empty-list'
                        }
                        with('name'[0]) {
                            text() == 'release'
                        }
                    }
                }
            }
        }
    }

    def 'git with named exceptions branch property strategy and only default properties'() {
        when:
        context.git {
            namedExceptionsBranchPropertyStrategy {
                defaultProperties {
                    noTriggerBranchProperty()
                }
            }
        }

        then:
        with(context.branchSourceNodes[0]) {
            with(strategy[0]) {
                children().size() == 2
                attribute('class') == 'jenkins.branch.NamedExceptionsBranchPropertyStrategy'
                with(defaultProperties[0]) {
                    children().size() == 1
                    with('jenkins.branch.NoTriggerBranchProperty'[0]) {
                    }
                }
                with(namedExceptions[0]) {
                    children().size == 0
                }
            }
        }
    }

    def 'git with named exceptions branch property strategy and no default properties'() {
        when:
        context.git {
            namedExceptionsBranchPropertyStrategy {
                namedException {
                    branch('dev')
                    noTriggerBranchProperty()
                }
            }
        }

        then:
        with(context.branchSourceNodes[0]) {
            with(strategy[0]) {
                children().size() == 2
                attribute('class') == 'jenkins.branch.NamedExceptionsBranchPropertyStrategy'
                with(defaultProperties[0]) {
                    children().size() == 0
                }
                with(namedExceptions[0]) {
                    children().size() == 1
                    with(getByName('jenkins.branch.NamedExceptionsBranchPropertyStrategy_-Named')[0]) {
                        children().size() == 2
                        with(props[0]) {
                            children().size() == 1
                            with('jenkins.branch.NoTriggerBranchProperty'[0]) {
                            }
                        }
                        with('name'[0]) {
                            text() == 'dev'
                        }
                    }
                }
            }
        }
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
                children().size() == 13
                id[0].value() instanceof String
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
}
