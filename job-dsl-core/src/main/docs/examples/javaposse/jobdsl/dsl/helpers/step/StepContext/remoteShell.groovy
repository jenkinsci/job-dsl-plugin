job('example') {
    steps {
        remoteShell('root@example.com:22') {
            command('echo Hello', 'echo World!')
            command('echo How are you?')
            command(["echo I'm fine!", 'echo And you?'])
            command(readFileFromWorkspace('script.sh'))
        }
    }
}
