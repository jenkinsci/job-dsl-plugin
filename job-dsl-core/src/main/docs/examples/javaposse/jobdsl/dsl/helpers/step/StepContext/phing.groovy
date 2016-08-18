job('example') {
    steps {
        phing {
            properties('KEY', 'VALUE')
            targets('test')
            options('--debug')
            buildFile('dir1/build.xml')
            phingInstallation('Phing 1.8')
            useModuleRoot(false)
        }
    }
}
