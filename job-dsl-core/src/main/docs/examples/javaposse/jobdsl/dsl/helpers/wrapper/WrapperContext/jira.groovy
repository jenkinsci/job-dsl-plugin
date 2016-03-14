job('example') {
    wrappers {
        generateJiraReleaseNotes {
            environmentVariable('JiraReleaseNotes')
            projectKey('PROJECT')
            release('$JiraBuild')
            filter('status in (Resolved, Closed)')
        }
    }
}
