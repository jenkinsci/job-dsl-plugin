job('example') {
    parameters {
        globalVariableParam('myParameterName', '${MY_DEFAULT_GLOBAL_VARIABLE}', 'my description')
    }
}
