// Reuse the same custom workspace for every axis.
matrixJob('example') {
    customWorkspace('example')
    childCustomWorkspace('.')
}
