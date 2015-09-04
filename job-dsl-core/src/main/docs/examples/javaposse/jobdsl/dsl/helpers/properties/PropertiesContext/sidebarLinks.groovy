userContent('wiki.png', streamFileFromWorkspace('images/wiki.png'))

job('example') {
    properties {
        sidebarLinks {
            // use built-in image
            link('https://jira.acme.org/', 'JIRA', 'notepad.png')
            // use uploaded image
            link('https://wiki.acme.org/', 'Wiki', '/userContent/wiki.png')
        }
    }
}
