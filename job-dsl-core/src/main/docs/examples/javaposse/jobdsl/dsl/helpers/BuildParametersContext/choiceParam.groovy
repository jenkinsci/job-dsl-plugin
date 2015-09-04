job('example') {
    parameters {
        choiceParam('myParameterName', ['option 1 (default)', 'option 2', 'option 3'], 'my description')
    }
}
