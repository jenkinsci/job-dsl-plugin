job('example') {
    steps {
        cmake {
            cmakeInstallation('InSearchPath')
            generator('Unix Makefiles')
            cleanBuild()
            sourceDir('src')
            buildDir('target')
            preloadScript('')
            args('foo')
            args('bar')
            buildToolStep {
                args('')
                vars('KEY=VALUE')
                useCmake()
            }
            buildToolStep {
                useCmake(false)
            }
        }
    }
}
