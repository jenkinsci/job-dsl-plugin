job('example') {
    publishers {
        railsNotes {
            rakeVersion('v10.2.2')
            rakeWorkingDirectory('src')
        }
    }
}
