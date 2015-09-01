job('example') {
    parameters {
        credentialsParam('DEPLOY_KEY') {
            type('com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey')
            required()
            defaultValue('ssh-key-staging')
            description('SSH Key for deploying build artifacts')
        }
    }
}
