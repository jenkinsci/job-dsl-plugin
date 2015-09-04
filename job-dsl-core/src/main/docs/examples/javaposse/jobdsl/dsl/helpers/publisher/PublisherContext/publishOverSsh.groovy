job('example') {
    publishers {
        publishOverSsh {
            server('my-server-01') {
                credentials('user01') {
                    pathToKey('path01')
                }
                label('server-01')
                transferSet {
                    sourceFiles('files')
                    execCommand('command')
                }
            }
            server('my-server-02') {
                credentials('user2') {
                    key('key')
                }
                label('server-02')
                transferSet {
                    sourceFiles('files2')
                    execCommand('command2')
                }
            }
            parameterizedPublishing('PARAMETER')
        }
    }
}
