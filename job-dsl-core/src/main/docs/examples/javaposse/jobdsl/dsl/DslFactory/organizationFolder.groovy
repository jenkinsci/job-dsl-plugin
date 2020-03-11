organizationFolder('example') {
    description('This contains branch source jobs for Bitbucket and GitHub')
    displayName('Organization Folder')
    triggers {
        cron('@midnight')
    }
}
