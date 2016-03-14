job('example') {
    steps {
        progressJiraIssues {
            jqlSearch('project = PROJECT and fixVersion = "$JiraBuild"')
            workflowActionName('Closed')
            comment('Comment')
        }
    }
}
