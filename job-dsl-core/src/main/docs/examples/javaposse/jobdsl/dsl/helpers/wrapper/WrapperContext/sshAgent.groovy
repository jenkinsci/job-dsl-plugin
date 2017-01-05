job('example') {
    wrappers {
        sshAgent('deployment-key', 'another-deployment-key')
    }
}
