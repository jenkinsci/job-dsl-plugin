organizationFolderJob('orgFolder') {
  description('This contains branch source jobs for Bitbucket and GitHub')
  displayName('Organization Folder')
  branchAutoTriggerPattern('master|develop|feature/*')
  triggers {
    periodicIfNotOtherwiseTriggered(PeriodicFolderTrigger.TWELVE_HOURS)
  }
  organizations {
    bitbucket {
      repoOwner('KEY')
      scanCredentialsId('bitbucketScanCredentials')
      repositoryPattern('*')
      autoRegisterWebhooks()
      checkoutCredentialsId('bitbucketSshCheckoutCredentials')
      bitbucketServerUrl('https://bitbucket.corp.com')
      sshPort(7990)
    }
    github {
      repoOwner('organization')
      scanCredentialsId('githubEnterpriseScanCredentials')
      checkoutCredentialsId('githubSshCheckoutCredentials')
      apiUri('https://ghe.corp.com')
      repositoryPattern('*')
      buildOriginBranch()
      buildOriginPrMerge()
      buildForkPrMerge()
      buildForkPrHead()
    }
  }
}
