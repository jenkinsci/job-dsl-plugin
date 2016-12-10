package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class ScmNavigatorsContextSpec extends Specification {
  private final JobManagement jobManagement = Mock(JobManagement)
  private final ScmNavigatorsContext context = new ScmNavigatorsContext(jobManagement)

  def "has no navigators by default"() {
    expect:
    context.scmNavigatorNodes.empty
  }

  def "can configure a GitHub Branch Source navigator node"() {
    given:
    String expectedRepoOwner = 'repoOwner'
    String expectedScanCredentialsId = 'scanCredentials'
    String expectedCheckoutCredentialsId = 'checkoutCredentials'
    String expectedApiUri = 'https://github.internal.com/api'
    String expectedRepositoryPattern = 'repository*'
    String expectedIncludes = 'includes*'
    String expectedExcludes = 'excludes*'

    when:
    context.github {
      repoOwner(expectedRepoOwner)
      scanCredentialsId(expectedScanCredentialsId)
      checkoutCredentialsId(expectedCheckoutCredentialsId)
      apiUri(expectedApiUri)
      repositoryPattern(expectedRepositoryPattern)
      includeBranches(expectedIncludes)
      excludeBranches(expectedExcludes)
      buildOriginBranch()
      buildOriginBranchWithPr()
      buildOriginPrMerge()
      buildOriginPrHead()
      buildForkPrMerge()
      buildForkPrHead()
    }

    then:
    context.scmNavigatorNodes.size() == 1
    with(context.scmNavigatorNodes[0]) {
      name() == 'org.jenkinsci.plugins.github__branch__source.GitHubSCMNavigator'
      children().size() == 13
      repoOwner.size() == 1
      repoOwner[0].value() == expectedRepoOwner
      apiUri.size() == 1
      apiUri[0].value() == expectedApiUri
      scanCredentialsId.size() == 1
      scanCredentialsId[0].value() == expectedScanCredentialsId
      checkoutCredentialsId.size() == 1
      checkoutCredentialsId[0].value() == expectedCheckoutCredentialsId
      pattern.size() == 1
      pattern[0].value() == expectedRepositoryPattern
      includes.size() == 1
      includes[0].value() == expectedIncludes
      excludes.size() == 1
      excludes[0].value() == expectedExcludes
      buildOriginBranch.size() == 1
      buildOriginBranch[0].value() == true
      buildOriginBranchWithPR.size() == 1
      buildOriginBranchWithPR[0].value() == true
      buildOriginPRMerge.size() == 1
      buildOriginPRMerge[0].value() == true
      buildOriginPRHead.size() == 1
      buildOriginPRHead[0].value() == true
      buildForkPRMerge.size() == 1
      buildForkPRMerge[0].value() == true
      buildForkPRHead.size() == 1
      buildForkPRHead[0].value() == true
    }
  }

  def "can configure a Bitbucket Branch Source navigator"() {
    given:
    String expectedRepoOwner = 'JENKINS'
    String expectedScanCredentialsId = 'scanCredentials'
    String expectedRepositoryPattern = 'pattern*'
    String expectedCheckoutCredentialsId = 'checkoutCredentials'
    String expectedBitbucketServerUrl = 'https://bitbucket.internal.com'
    int expectedSshPort = 7999

    when:
    context.bitbucket {
      repoOwner(expectedRepoOwner)
      scanCredentialsId(expectedScanCredentialsId)
      repositoryPattern(expectedRepositoryPattern)
      autoRegisterWebhooks()
      checkoutCredentialsId(expectedCheckoutCredentialsId)
      bitbucketServerUrl(expectedBitbucketServerUrl)
      sshPort(expectedSshPort)
    }

    then:
    context.scmNavigatorNodes.size() == 1
    with(context.scmNavigatorNodes[0]) {
      name() == 'com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMNavigator'
      children().size() == 7
      repoOwner.size() == 1
      repoOwner[0].value() == expectedRepoOwner
      scanCredentialsId.size() == 1
      scanCredentialsId[0].value() == expectedScanCredentialsId
      checkoutCredentialsId.size() == 1
      checkoutCredentialsId[0].value() == expectedCheckoutCredentialsId
      pattern.size() == 1
      pattern[0].value() == expectedRepositoryPattern
      autoRegisterHooks.size() == 1
      autoRegisterHooks[0].value() == true
      bitbucketServerUrl.size() == 1
      bitbucketServerUrl[0].value() == expectedBitbucketServerUrl
      sshPort.size() == 1
      sshPort[0].value() == expectedSshPort
    }
  }

  def "default Bitbucket SCM navigator configuration"() {
    when:
    context.bitbucket {}

    then:
    context.scmNavigatorNodes.size() == 1
    with(context.scmNavigatorNodes[0]) {
      children().size() == 5
      repoOwner.size() == 1
      repoOwner[0].value().empty
      checkoutCredentialsId.size() == 1
      checkoutCredentialsId[0].value() == 'SAME'
      pattern.size() == 1
      pattern[0].value() == '.*'
      autoRegisterHooks.size() == 1
      autoRegisterHooks[0].value() == false
      sshPort.size() == 1
      sshPort[0].value() == -1
    }
  }

  def "default GitHub SCM Navigator configuration"() {
    when:
    context.github {}

    then:
    context.scmNavigatorNodes.size() == 1
    with(context.scmNavigatorNodes[0]) {
      children().size() == 10
      repoOwner.size() == 1
      repoOwner[0].value() == ''
      checkoutCredentialsId.size() == 1
      checkoutCredentialsId[0].value() == 'SAME'
      pattern.size() == 1
      pattern[0].value() == '.*'
      includes.size() == 1
      includes[0].value() == '*'
      buildOriginBranch.size() == 1
      buildOriginBranch[0].value() == true
      buildOriginBranchWithPR.size() == 1
      buildOriginBranchWithPR[0].value() == true
      buildOriginPRMerge.size() == 1
      buildOriginPRMerge[0].value() == false
      buildOriginPRHead.size() == 1
      buildOriginPRHead[0].value() == false
      buildForkPRMerge.size() == 1
      buildForkPRMerge[0].value() == true
      buildForkPRHead.size() == 1
      buildForkPRHead[0].value() == false
    }
  }

  def "can configure multiple navigators"() {
    when:
    context.bitbucket {}
    context.bitbucket {}
    context.github {}
    context.bitbucket {}
    context.github {}

    then:
    context.scmNavigatorNodes.size() == 5
  }
}
