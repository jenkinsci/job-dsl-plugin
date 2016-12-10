package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class BitbucketScmNavigatorContextSpec extends Specification {

  private final JobManagement jobManagement = Mock(JobManagement)
  private final BitbucketScmNavigatorContext context = new BitbucketScmNavigatorContext(jobManagement)

  def "defaults are set"() {
    expect:
    with(context) {
      repoOwner == null
      scanCredentialsId == null
      repositoryPattern == '.*'
      !autoRegisterWebhooks
      checkoutCredentialsId == 'SAME'
      bitbucketServerUrl == null
      sshPort == -1
    }
  }
}
