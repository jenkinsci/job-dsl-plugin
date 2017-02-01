organizationFolder('orgFolder') {
    description('This contains branch source jobs for Bitbucket and GitHub')
    displayName('Organization Folder')
    triggers {
        periodicIfNotOtherwiseTriggered(PeriodicFolderTrigger.TWELVE_HOURS)
    }
}
