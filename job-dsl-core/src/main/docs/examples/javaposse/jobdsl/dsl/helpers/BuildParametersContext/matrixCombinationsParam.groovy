matrixJob('example') {
    parameters {
        matrixCombinationsParam('COMBINATIONS', "axis1 != 'value3'", 'choose which combinations to run')
    }
}
