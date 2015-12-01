job('example') {
    publishers {
        railsStats {
            rakeVersion('(Default)')
            rakeWorkingDir('src')
        }
    }
}
