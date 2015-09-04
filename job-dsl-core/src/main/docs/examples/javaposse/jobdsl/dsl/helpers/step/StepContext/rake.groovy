job('example') {
    steps {
        rake('task')
        rake('first') {
            task('second')
            tasks(['third', 'fourth'])
            file('/opt/app/Rakefile')
            installation('ruby-2.0.0-p481')
            libDir('./rakelib')
            workingDir('/opt/app')
            bundleExec()
            silent()
        }
    }
}
