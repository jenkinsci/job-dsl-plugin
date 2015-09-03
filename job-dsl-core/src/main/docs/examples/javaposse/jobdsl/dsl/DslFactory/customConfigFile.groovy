// creates a custom config file from a file named acme/settings.json in the seed job's workspace
customConfigFile('acme-settings') {
    comment('Settings for ACME tools')
    content(readFileFromWorkspace('acme/settings.json'))
}
