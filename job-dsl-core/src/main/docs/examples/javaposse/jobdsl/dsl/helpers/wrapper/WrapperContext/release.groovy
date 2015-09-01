job('example') {
    wrappers {
        release {
            doNotKeepLog()
            overrideBuildParameters()
            parameters {
                booleanParam('param', false, 'some boolean build parameter')
            }
            preBuildSteps {
                shell("echo 'hello'")
            }
        }
    }
}
