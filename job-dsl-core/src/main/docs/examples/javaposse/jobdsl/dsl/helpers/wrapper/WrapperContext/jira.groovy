job('example') {
    wrappers {
        jira {
            jiraCreateReleaseNotes {
                jiraEnvironmentVariable('bla')
                jiraProjectKey('bla')
                jiraRelease('bla')
                jiraFilter('bla')
            }
        }
    }
}
