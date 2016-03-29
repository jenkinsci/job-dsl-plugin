job('example') {
    steps {
        cmake {
            cmakeInstallation('InSearchPath')
            generator('Unix Makefiles')
            cleanBuild()
            sourceDir('src')
            buildDir('target')
            args('foo')
            args('bar')
            buildToolStep {
                vars('KEY', 'VALUE')
                useCmake()
            }
            buildToolStep {
                useCmake(false)
            }
        }
    }
}
