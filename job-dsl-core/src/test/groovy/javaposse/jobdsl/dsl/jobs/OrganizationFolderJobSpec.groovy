package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.workflow.ScmNavigatorsContext
import spock.lang.Specification

class OrganizationFolderJobSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    OrganizationFolderJob job = new OrganizationFolderJob(jobManagement, 'test')

    def 'can construct an organization folder job with default settings'() {
        expect:
        with(job.node) {
            name() == 'jenkins.branch.OrganizationFolder'
            actions.size() == 1
            actions[0].children().size() == 0
            orphanedItemStrategy[0].attribute('class') ==
                    'com.cloudbees.hudson.plugins.folder.computed.DefaultOrphanedItemStrategy'
            projectFactories.size() == 1
            projectFactories[0].'org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProjectFactory'
                    .size() == 1
            navigators.size() == 1
            navigators[0].children().size() == 0
            triggers.size() == 1
            triggers[0].children().size() == 0
            healthMetrics.size() == 1
            healthMetrics[0].'com.cloudbees.hudson.plugins.folder.health.WorstChildHealthMetric'.size() == 1
            viewsTabBar.size() == 1
            viewsTabBar[0].attribute('class') == 'hudson.views.DefaultViewsTabBar'
        }
    }

    def 'can configure display name'() {
        setup:
        String expectedDisplayName = 'This is the display name'

        when:
        job.displayName(expectedDisplayName)

        then:
        with(job.node) {
            displayName.size() == 1
            displayName[0].value() == expectedDisplayName
        }
    }

    def 'can configure description'() {
        setup:
        String expectedDescription = 'the description can be set'

        when:
        job.description(expectedDescription)

        then:
        with(job.node) {
            description.size() == 1
            description[0].value() == expectedDescription
        }
    }

    def 'can configure orphaned item strategy'() {
        setup:
        int expectedDaysToKeep = 20
        int expectedMaxOldItemsToKeep = 15

        when:
        job.orphanedItemStrategy {
            discardOldItems {
                daysToKeep(expectedDaysToKeep)
                numToKeep(expectedMaxOldItemsToKeep)
            }
        }

        then:
        with(job.node) {
            orphanedItemStrategy.size() == 1
            orphanedItemStrategy[0].attribute('class') ==
                    'com.cloudbees.hudson.plugins.folder.computed.DefaultOrphanedItemStrategy'
            orphanedItemStrategy[0].children().size() == 3
            orphanedItemStrategy[0].pruneDeadBranches[0].value() == true
            orphanedItemStrategy[0].daysToKeep[0].value() == expectedDaysToKeep
            orphanedItemStrategy[0].numToKeep[0].value() == expectedMaxOldItemsToKeep
        }
    }

    def 'can configure triggers'() {
        when:
        job.triggers {
            periodic(2)
        }

        then:
        with(job.node) {
            triggers.size() == 1
            with(triggers[0]) {
                children().size() == 1
                it.'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger'.size() == 1
                it.'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger'[0].interval[0].text() ==
                        '120000'
            }
        }
    }

    def 'can configure multiple SCM Navigators'() {
        setup:
        jobManagement.callExtension('navigator1', job, ScmNavigatorsContext, []) >>
                new Node(null, 'org.example.ScmNavigator1')
        jobManagement.callExtension('navigator2', job, ScmNavigatorsContext, []) >>
                new Node(null, 'org.example.ScmNavigator2')

        when:
        job.organizations {
            navigator1()
            navigator2()
            navigator1()
            navigator2()
            navigator2()
        }

        then:
        with(job.node) {
            navigators.size() == 1
            with(navigators[0]) {
                children().size() == 5
                with(it.'org.example.ScmNavigator1') {
                    size() == 2
                }
                with(it.'org.example.ScmNavigator2') {
                    size() == 3
                }
            }
        }
    }
}
