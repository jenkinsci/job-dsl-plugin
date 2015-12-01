job('example') {
    publishers {
        railsNotes {
            rakeVersion('(Default)')
            rakeWorkingDir('src')
        }
    }
}
