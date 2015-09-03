job('example-1') {
    blockOn('project-a')
}

job('example-2') {
    blockOn(['project-a', 'project-b']) {
        blockLevel('GLOBAL')
        scanQueueFor('ALL')
    }
}
