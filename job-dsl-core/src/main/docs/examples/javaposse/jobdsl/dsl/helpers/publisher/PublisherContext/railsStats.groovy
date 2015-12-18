job('example') {
    publishers {
        railsStats {
            rakeVersion('v10.2.2')
            rakeWorkingDirectory('src')
        }
    }
}
