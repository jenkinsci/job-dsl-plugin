job('example') {
    publishers {
        createJiraIssue {
            projectKey('PROJECT')
            testDescription('bla')
            assignee('Bob')
            component('ComponentA')
        }
    }
}
