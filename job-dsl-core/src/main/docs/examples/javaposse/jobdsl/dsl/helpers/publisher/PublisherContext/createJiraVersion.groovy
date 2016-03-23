job('example') {
    publishers {
        createJiraVersion {
            projectKey('PROJECT')
            version('VersionA')
        }
    }
}
