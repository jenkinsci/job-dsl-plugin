job('example') {
    wrappers {
        customTools(['NodeJS', 'figlet']) {
            skipMasterInstallation()
        }
    }
}
