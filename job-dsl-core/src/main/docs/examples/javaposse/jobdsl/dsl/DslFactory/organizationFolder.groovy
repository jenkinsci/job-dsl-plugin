organizationFolder('example') {
    description('This contains branch source jobs for Bitbucket and GitHub')
    displayName('Organization Folder')
    triggers {
        periodic(86400)
    }
}
