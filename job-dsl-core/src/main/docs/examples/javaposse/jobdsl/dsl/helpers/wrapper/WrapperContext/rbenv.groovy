job('example') {
    wrappers {
        rbenv('2.1.2') {
            ignoreLocalVersion()
            gems('bundler', 'rake')
        }
    }
}
