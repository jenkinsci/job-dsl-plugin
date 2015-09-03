// allows to select a single node from all nodes available
job('example-1') {
    parameters {
        nodeParam('TEST_HOST')
    }
}

// runs on node1 by default and can be run on node1, node2 or node3 when triggered manually
job('example-2') {
    parameters {
        nodeParam('TEST_HOST') {
            description('select test host')
            defaultNodes(['node1'])
            allowedNodes(['node1', 'node2', 'node3'])
            trigger('multiSelectionDisallowed')
            eligibility('IgnoreOfflineNodeEligibility')
        }
    }
}
