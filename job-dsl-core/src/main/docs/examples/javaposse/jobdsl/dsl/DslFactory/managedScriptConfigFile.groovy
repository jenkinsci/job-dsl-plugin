managedScriptConfigFile('example') {
    content('echo Hello $1')
    arguments('NAME')
}
