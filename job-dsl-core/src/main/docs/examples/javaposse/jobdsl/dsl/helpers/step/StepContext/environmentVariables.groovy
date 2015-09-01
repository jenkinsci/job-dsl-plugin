job('example') {
    steps {
        environmentVariables {
            env('ONE', '1')
            envs(FOO: 'bar', TEST: '123')
            propertiesFile('env.properties')
        }
    }
}
