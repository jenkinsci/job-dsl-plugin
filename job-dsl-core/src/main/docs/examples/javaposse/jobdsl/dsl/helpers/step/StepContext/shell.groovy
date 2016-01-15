job('example') {
    steps {
        shell('echo Hello World!')
        shell(readFileFromWorkspace('build.sh'))
        shell {
            block('MESSAGE="Hello, World!"')
            block('echo $MESSAGE')
        }
    }
}
