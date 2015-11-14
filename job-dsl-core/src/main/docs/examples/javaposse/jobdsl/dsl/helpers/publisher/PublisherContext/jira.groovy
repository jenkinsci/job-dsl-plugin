job('example') {
    publishers {
        jira {
            jiraIssueUpdater()
            jiraReleaseVersionUpdater {
                jiraProjectKey('bla')
                jiraRelease('bla')
            }
            jiraIssueMigrator {
                jiraProjectKey('bla')
                jiraRelease('bla')
                jiraReplaceVersion('bla')
                jiraQuery('bla')
            }
            jiraCreateIssueNotifier {
                projectKey('bla')
                testDescription('bla')
                assignee('bla')
                component('bla')
            }
            jiraVersionCreator {
                jiraVersion('bla')
                jiraProjectKey('bla')
            }
        }
    }
}
