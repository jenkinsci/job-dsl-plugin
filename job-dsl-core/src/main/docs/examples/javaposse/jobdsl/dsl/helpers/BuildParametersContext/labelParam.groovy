job('example-1') {
    parameters {
        labelParam('MY_LABEL')
    }
}

// runs on all nodes which are labeled with "linux" and are online
job('example-2') {
    parameters {
        labelParam('MY_LABEL') {
            defaultValue('linux')
            description('Select nodes')
            allNodes('allCases', 'IgnoreOfflineNodeEligibility')
        }
    }
}
