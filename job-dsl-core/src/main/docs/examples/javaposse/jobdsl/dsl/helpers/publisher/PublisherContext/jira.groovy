job('example') {
    publishers {
        jiraIssueUpdater()
        releaseJiraVersion {
            projectKey('PROJECT')
            release('$JiraBuild')
        }
        moveJiraIssues {
            projectKey('PROJECT')
            release('$JiraBuild')
            replaceVersion('PROJECT-01')
            query('status in (Resolved, Closed)')
        }
        createJiraIssue {
            projectKey('PROJECT')
            testDescription('bla')
            assignee('Bob')
            component('ComponentA')
        }
        createJiraVersion {
            projectKey('PROJECT')
            version('VersionA')
        }
    }
}
