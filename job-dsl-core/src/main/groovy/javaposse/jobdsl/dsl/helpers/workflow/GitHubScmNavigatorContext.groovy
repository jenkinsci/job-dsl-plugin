package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

/**
 * @since 1.56
 */
class GitHubScmNavigatorContext extends AbstractContext {

  String repoOwner
  String scanCredentialsId
  String checkoutCredentialsId = 'SAME'
  String apiUri
  String repositoryPattern = '.*'
  String includeBranchesPattern = '*'
  String excludeBranchesPattern
  boolean buildOriginBranch = true
  boolean buildOriginBranchWithPr = true
  boolean buildOriginPrMerge = false
  boolean buildOriginPrHead = false
  boolean buildForkPrMerge = true
  boolean buildForkPrHead = false

  protected GitHubScmNavigatorContext(JobManagement jobManagement) {
    super(jobManagement)
  }

  /**
   * Specify the name of the <b>GitHub Organization</b> or <b>GitHub User Account</b>.
   * @since 1.56
   */
  void repoOwner(String repoOwner) {
    this.repoOwner = repoOwner
  }

  /**
   * Credentials used to <b>scan</b> branches and pull requests, <b>check out</b>
   * sources and <b>mark</b> commit statuses.
   *
   * <p>If none is given, only the public repositories will be scanned, and
   * commit status will not be set on GitHub.</p>
   *
   * <p>If your organization contains private repositories, then you need to specify a credential
   * from an user who have access to those repositories. This is done by creating a
   * "username with password" credential where the password is
   * <a href="https://github.com/settings/tokens">GitHub personal access tokens</a>.
   * The necessary scope is "repo".
   *
   * @since 1.56
   */
  void scanCredentialsId(String scanCredentialsId) {
    this.scanCredentialsId = scanCredentialsId
  }

  /**
   * Credentials used to check out sources during a build.
   *
   * <p>The default choice of "Same as scan credentials" is sufficient so long as you
   * don't need Jenkins to push some changes back into these repositories.</p>
   *
   * <p>Otherwise you should create an SSH username with private key credential and specify
   * an SSH private key that belongs to the same user you've used for the scan credentials.
   * This causes Jenkins to check out source code over SSH, and therefore you can run
   * git push from your pipeline to push changes back.
   *
   * @since 1.56
   */
  void checkoutCredentialsId(String checkoutCredentialsId) {
    this.checkoutCredentialsId = checkoutCredentialsId
  }

  /**
   * The API endpoint to scan against.
   * @since 1.56
   */
  void apiUri(String apiUri) {
    this.apiUri = apiUri
  }

  /**
   * Space-separated list of branch name patterns to consider.
   * You may use {@code *} as a wildcard; for example: {@code master release*}
   * @since 1.56
   */
  void includeBranches(String includeBranchesPattern) {
    this.includeBranchesPattern = includeBranchesPattern
  }

  /**
   * Branch name patterns to ignore even if matched by the includes list. For example: {@code release}
   * @since 1.56
   */
  void excludeBranches(String excludeBranchesPattern) {
    this.excludeBranchesPattern = excludeBranchesPattern
  }

  /**
   * Regular expression to specify what repositories one wants to include
   * @since 1.56
   */
  void repositoryPattern(String repositoryPattern) {
    this.repositoryPattern = repositoryPattern
  }

  /**
   * Whether to build branches defined in the origin (primary) repository,
   * not associated with any pull request. The job name will match the branch name.
   * @since 1.56
   */
  void buildOriginBranch(boolean buildOriginBranch = true) {
    this.buildOriginBranch = buildOriginBranch
  }

  /**
   * Whether to build branches defined in the origin (primary) repository for which pull
   * requests happen to have been filed. The job name will match the
   * branch name, not the pull request(s).
   * @since 1.56
   */
  void buildOriginBranchWithPr(boolean buildOriginBranchWithPr = true) {
    this.buildOriginBranchWithPr = buildOriginBranchWithPr
  }

  /**
   * Whether to build pull requests filed from branches in the origin repository.
   * The job will be named according to the PR and builds will attempt to
   * merge with the base branch.
   * @since 1.56
   */
  void buildOriginPrMerge(boolean buildOriginPrMerge = true) {
    this.buildOriginPrMerge = buildOriginPrMerge
  }

  /**
   * Whether to build pull requests filed from branches in the origin repository.
   * The job will be named according to the PR and builds will use the head of the pull
   * request, ignoring subsequent changes to the base branch. Other than naming, the behavior is
   * similar to Build origin branches also filed as PRs.
   * @since 1.56
   */
  void buildOriginPrHead(boolean buildOriginPrHead = true) {
    this.buildOriginPrHead = buildOriginPrHead
  }

  /**
   * Whether to build pull requests filed from forks of the main repository.
   * The job will be named according to the PR and builds will attempt to merge
   * with the base branch.
   * @since 1.56
   */
  void buildForkPrMerge(boolean buildForkPrMerge = true) {
    this.buildForkPrMerge = buildForkPrMerge
  }

  /**
   * Whether to build pull requests filed from forks of the main repository.
   * The job will be named according to the PR and builds will use the head of the pull request,
   * ignoring subsequent changes to the base branch.
   * @since 1.56
   */
  void buildForkPrHead(boolean buildForkPrHead = true) {
    this.buildForkPrHead = buildForkPrHead
  }
}
