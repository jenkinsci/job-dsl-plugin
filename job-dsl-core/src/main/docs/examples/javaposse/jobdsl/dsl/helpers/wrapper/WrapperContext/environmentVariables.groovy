job('example') {
    wrappers {
        environmentVariables {
            env('ONE', '1')
            envs(FOO: 'bar', TEST: '123')
            propertiesFile('env.properties')
        }
    }
}
