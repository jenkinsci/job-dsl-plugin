package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

/**
 * @since 1.56
 */
class ScmNavigatorsContext extends AbstractContext {

  final List<Node> scmNavigatorNodes = []

  ScmNavigatorsContext(final JobManagement jobManagement) {
    super(jobManagement)
  }

  /**
   * Creates a Bitbucket Branch Source SCM Navigator.
   * @see <a href="https://wiki.jenkins-ci.org/display/JENKINS/Bitbucket+Branch+Source+Plugin"></a>
   * @since 1.56
   */
  @RequiresPlugin(id = 'cloudbees-bitbucket-branch-source', minimumVersion = '1.8')
  void bitbucket(@DslContext(BitbucketScmNavigatorContext) Closure closure) {
    BitbucketScmNavigatorContext context = new BitbucketScmNavigatorContext(jobManagement)
    ContextHelper.executeInContext(closure, context)

    scmNavigatorNodes << new NodeBuilder().'com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMNavigator' {
      repoOwner(context.repoOwner ?: '')
      if (context.checkoutCredentialsId) {
        checkoutCredentialsId(context.checkoutCredentialsId)
      }
      if (context.scanCredentialsId) {
        scanCredentialsId(context.scanCredentialsId)
      }
      pattern(context.repositoryPattern ?: '')
      autoRegisterHooks(context.autoRegisterWebhooks)
      if (context.bitbucketServerUrl) {
        bitbucketServerUrl(context.bitbucketServerUrl)
      }
      sshPort(context.sshPort)
    }
  }

  /**
   * Creates a GitHub Branch Source SCM Navigator.
   * @see <a href="https://wiki.jenkins-ci.org/display/JENKINS/GitHub+Branch+Source+Plugin"></a>
   * @since 1.56
   */
  @RequiresPlugin(id = 'github-branch-source', minimumVersion = '1.10.1')
  void github(@DslContext(GitHubScmNavigatorContext) Closure closure) {
    GitHubScmNavigatorContext context = new GitHubScmNavigatorContext(jobManagement)
    ContextHelper.executeInContext(closure, context)

    scmNavigatorNodes << new NodeBuilder().'org.jenkinsci.plugins.github__branch__source.GitHubSCMNavigator' {
      repoOwner(context.repoOwner ?: '')
      if (context.checkoutCredentialsId) {
        checkoutCredentialsId(context.checkoutCredentialsId ?: '')
      }
      if (context.scanCredentialsId) {
        scanCredentialsId(context.scanCredentialsId)
      }
      pattern(context.repositoryPattern ?: '')
      if (context.excludeBranchesPattern) {
        excludes(context.excludeBranchesPattern)
      }
      if (context.includeBranchesPattern) {
        includes(context.includeBranchesPattern)
      }
      if (context.apiUri) {
        apiUri(context.apiUri)
      }
      buildOriginBranch(context.buildOriginBranch)
      buildOriginBranchWithPR(context.buildOriginBranchWithPr)
      buildOriginPRMerge(context.buildOriginPrMerge)
      buildOriginPRHead(context.buildOriginPrHead)
      buildForkPRMerge(context.buildForkPrMerge)
      buildForkPRHead(context.buildForkPrHead)
    }
  }
}
