job('example-1') {
    environmentVariables(FOO: 'bar', TEST: '123')
}

job('example-2') {
    environmentVariables {
        env('ONE', '1')
        propertiesFile('env.properties')
        keepBuildVariables(true)
    }
}
