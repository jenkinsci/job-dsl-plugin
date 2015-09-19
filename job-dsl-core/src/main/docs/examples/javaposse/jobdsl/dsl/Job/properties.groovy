job('example') {
    properties {
        customIcon('job.png')
        sidebarLinks {
            link('https://jira.acme.org/', 'JIRA', 'notepad.png')
        }
        rebuild {
            autoRebuild()
        }
    }
}
