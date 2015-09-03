job('example') {
    steps {
        publishOverSsh {
            server('server-name') {
                transferSet {
                    sourceFiles('file')
                }
            }
        }
    }
}
