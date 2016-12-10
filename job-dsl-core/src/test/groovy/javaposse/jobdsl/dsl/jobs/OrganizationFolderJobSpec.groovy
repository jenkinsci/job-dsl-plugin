package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.workflow.OrganizationFolderTriggerContext
import spock.lang.Specification

class OrganizationFolderJobSpec extends Specification {

  private final JobManagement jobManagement = Mock(JobManagement)
  private final OrganizationFolderJob job = new OrganizationFolderJob(jobManagement, 'test')

  def "can construct an organization folder job with default settings"() {
    expect:
    with(job.node) {
      name() == 'jenkins.branch.OrganizationFolder'
      actions.size() == 1
      actions[0].children().size() == 0
      orphanedItemStrategy[0].attribute('class') ==
        'com.cloudbees.hudson.plugins.folder.computed.DefaultOrphanedItemStrategy'
      projectFactories.size() == 1
      projectFactories[0].'org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProjectFactory'.size() == 1
      navigators.size() == 1
      navigators[0].children().size() == 0
      triggers.size() == 1
      with(triggers[0]) {
        it.'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger'.size() == 1
        with(it.'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger'[0]) {
          spec.size() == 1
          interval.size() == 1
        }
      }
      healthMetrics.size() == 1
      with(healthMetrics[0]) {
        it.'com.cloudbees.hudson.plugins.folder.health.WorstChildHealthMetric'.size() == 1
      }
      viewsTabBar.size() == 1
      viewsTabBar[0].attribute('class') == 'hudson.views.DefaultViewsTabBar'
    }
  }

  def "can configure display name"() {
    given:
    def expectedDisplayName = 'This is the display name'

    when:
    job.displayName(expectedDisplayName)

    then:
    with(job.node) {
      displayName.size() == 1
      displayName[0].value() == expectedDisplayName
    }
  }

  def "can configure description"() {
    given:
    def expectedDescription = '''the description can be set.
It can also contain multiple lines.
'''

    when:
    job.description(expectedDescription)

    then:
    with(job.node) {
      description.size() == 1
      description[0].value() == expectedDescription
    }
  }

  def "can configure orphaned item strategy"() {
    given:
    def expectedDaysToKeep = 20
    def expectedMaxOldItemsToKeep = 15

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

  def "can configure automatic branch trigger pattern"() {
    given:
    def pattern = 'master|feature/*'

    when:
    job.branchAutoTriggerPattern(pattern)

    then:
    with(job.node) {
      it.'properties'.size() == 1
      with(it.'properties'[0]) {
        it.'jenkins.branch.NoTriggerOrganizationFolderProperty'.size() == 1
        with(it.'jenkins.branch.NoTriggerOrganizationFolderProperty') {
          branches.size() == 1
          branches[0].value() == pattern
        }
      }
    }
  }

  def "can configure triggers"() {
    given:
    String expectedCron = 'H * * * *'

    when:
    job.triggers {
      cron(expectedCron)
      periodicIfNotOtherwiseTriggered(OrganizationFolderTriggerContext.PeriodicFolderTrigger.ONE_WEEK)
    }

    then:
    with(job.node) {
      triggers.size() == 1
      with(triggers[0]) {
        children().size() == 2
        it.'hudson.triggers.TimerTrigger'.size() == 1
        it.'hudson.triggers.TimerTrigger'[0].children().size() == 1
        it.'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger'.size() == 1
        it.'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger'[0].children().size() == 2
      }
    }
  }

  def "can configure Bitbucket Branch Source SCM Navigator"() {
    when:
    job.organizations {
      bitbucket {}
    }

    then:
    with(job.node) {
      navigators.size() == 1
      with(navigators[0]) {
        children().size() == 1
        with(it.'com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMNavigator') {
          size() == 1
          !children().empty
        }
      }
    }
  }

  def "can configure GitHub Branch Source SCM Navigator"() {
    when:
    job.organizations {
      github {}
    }

    then:
    with(job.node) {
      navigators.size() == 1
      with(navigators[0]) {
        children().size() == 1
        with(it.'org.jenkinsci.plugins.github__branch__source.GitHubSCMNavigator') {
          size() == 1
          !children().empty
        }
      }
    }
  }

  def "can configure multiple SCM Navigators"() {
    when:
    job.organizations {
      github {}
      bitbucket {}
      bitbucket {}
      github {}
      github {}
    }

    then:
    with(job.node) {
      navigators.size() == 1
      with(navigators[0]) {
        children().size() == 5
        with(it.'org.jenkinsci.plugins.github__branch__source.GitHubSCMNavigator') {
          size() == 3
          !children().empty
        }
        with(it.'com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMNavigator') {
          size() == 2
          !children().empty
        }
      }
    }

  }
}
