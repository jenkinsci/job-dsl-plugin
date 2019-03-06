job('example') {
    wrappers {
        phabricator {
            createCommit()
            applyToMaster()
            showBuildStartedMessage()
            workDir('source')
        }
    }
}
