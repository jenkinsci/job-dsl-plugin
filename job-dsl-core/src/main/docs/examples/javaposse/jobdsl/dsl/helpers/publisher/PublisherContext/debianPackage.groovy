job('example') {
    publishers {
        debianPackage('precise-default') {
            commitMessage('automatic commit by Jenkins')
        }
    }
}
