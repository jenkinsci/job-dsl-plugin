job('example') {
    wrappers {
        configFiles {
            file('myCustomConfigFile') {
                variable('CONFIG_FILE')
            }
            mavenSettings('myJenkinsSettingsFile') {
                targetLocation('settings.xml')
            }
        }
    }
}
