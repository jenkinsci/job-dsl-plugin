job('example') {
    parameters {
        gitParam('sha') {
            description('Revision commit SHA')
            type('REVISION')
            branch('master')
        }
    }
}
