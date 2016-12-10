package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class GitHubScmNavigatorContextSpec extends Specification {
  private final JobManagement jobManagement = Mock(JobManagement)
  private final GitHubScmNavigatorContext context = new GitHubScmNavigatorContext(jobManagement)

  def "defaults are set"() {
    expect:
    with(context) {
      repoOwner == null
      scanCredentialsId == null
      checkoutCredentialsId == 'SAME'
      repositoryPattern == '.*'
      includeBranchesPattern == '*'
      excludeBranchesPattern == null
      buildOriginBranch
      buildOriginBranchWithPr
      !buildOriginPrMerge
      !buildOriginPrHead
      buildForkPrMerge
      !buildForkPrHead
    }
  }
}
