job('example') {
    publishers {
        moveJiraIssues {
            projectKey('PROJECT')
            release('$JiraBuild')
            replaceVersion('PROJECT-01')
            query('status in (Resolved, Closed)')
        }
    }
}
