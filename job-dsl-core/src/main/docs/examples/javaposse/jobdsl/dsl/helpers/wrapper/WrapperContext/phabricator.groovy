job('example') {
    wrappers {
        phabricator {
            createCommit()
            applyToMaster()
            showBuildStartedMessage()
        }
    }
}
