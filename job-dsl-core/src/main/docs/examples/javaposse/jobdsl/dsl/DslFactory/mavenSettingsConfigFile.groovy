// creates a Maven settings file from a file named maven/settings.xml in the seed job's workspace
customConfigFile('company-settings') {
    comment('Company Maven Settings')
    content(readFileFromWorkspace('maven/settings.xml'))
}
