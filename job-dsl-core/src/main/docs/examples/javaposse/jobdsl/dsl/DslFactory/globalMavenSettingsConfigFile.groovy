// creates a global Maven settings file from a file named maven/settings.xml in the seed job's workspace
globalMavenSettingsConfigFile('company-settings') {
    comment('Company Maven Settings')
    content(readFileFromWorkspace('maven/settings.xml'))
    replaceAll()
    serverCredentials('company', 'company-maven-repository-credentials')
}
