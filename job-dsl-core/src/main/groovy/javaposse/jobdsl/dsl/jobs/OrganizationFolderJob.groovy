package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.workflow.OrganizationFolderTriggerContext
import javaposse.jobdsl.dsl.helpers.workflow.OrphanedItemStrategyContext
import javaposse.jobdsl.dsl.helpers.workflow.ScmNavigatorsContext

/**
 * @since 1.56
 */
class OrganizationFolderJob extends Item {

  protected OrganizationFolderJob(JobManagement jobManagement, String name) {
    super(jobManagement, name)
  }

  @Deprecated
  protected OrganizationFolderJob(final JobManagement jobManagement) {
    super(jobManagement)
  }

  /**
   * Sets the name to display instead of the folder name.
   * @since 1.56
   */
  void displayName(String displayName) {
    configure {
      it / methodMissing('displayName', displayName)
    }
  }

  /**
   * Sets the description of this folder.
   * @since 1.56
   */
  void description(String description) {
    configure { Node project ->
      project / methodMissing('description', description)
    }
  }

  /**
   * Sets the organizations in this folder.
   * @since 1.56
   */
  void organizations(@DslContext(ScmNavigatorsContext) Closure closure) {
    ScmNavigatorsContext context = new ScmNavigatorsContext(jobManagement)
    ContextHelper.executeInContext(closure, context)

    configure { Node project ->
      Node navigators = project / navigators
      navigators.children().clear()
      context.scmNavigatorNodes.each {
        navigators << it
      }
    }
  }

  /**
   * Allows you to control the SCM commit trigger coming from branch indexing.
   * Supply a regular expression of branch names, for example {@code (?!release.*).*} or {@code PR-\d+}.
   * Matching branches will be triggered automatically. (You may still build other branches manually or via CLI/REST.)
   * @param branchTriggerPattern the branch pattern
   * @since 1.56
   */
  void branchAutoTriggerPattern(String branchTriggerPattern) {
    configure { Node project ->
      project / 'properties' \
        / 'jenkins.branch.NoTriggerOrganizationFolderProperty' \
        / methodMissing('branches', branchTriggerPattern)
    }
  }

  /**
   * Sets the orphaned item strategy.
   * @since 1.56
   */
  void orphanedItemStrategy(@DslContext(OrphanedItemStrategyContext) Closure closure) {
    OrphanedItemStrategyContext context = new OrphanedItemStrategyContext()
    ContextHelper.executeInContext(closure, context)

    if (context.orphanedItemStrategyNode != null) {
      configure { Node project ->
        Node orphanedItemStrategy = project / 'orphanedItemStrategy'
        if (orphanedItemStrategy) {
          // there can only be only one orphanedItemStrategy, so remove if there
          project.remove(orphanedItemStrategy)
        }

        project << context.orphanedItemStrategyNode
      }
    }
  }

  /**
   * Sets the build triggers for this job.
   * @since 1.56
   */
  void triggers(@DslContext(OrganizationFolderTriggerContext) Closure closure) {
    OrganizationFolderTriggerContext context = new OrganizationFolderTriggerContext(jobManagement, this)
    ContextHelper.executeInContext(closure, context)

    configure { Node project ->
      Node triggers = project / 'triggers'
      triggers.children().clear()
      context.triggerNodes.each {
        triggers << it
      }
    }
  }
}
