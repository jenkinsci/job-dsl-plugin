job('example') {
    steps {
        jira {
            jiraIssueUpdateBuilder {
                jqlSearch('bla')
                workflowActionName('bla')
                comment('bla')
            }
        }
    }
}
