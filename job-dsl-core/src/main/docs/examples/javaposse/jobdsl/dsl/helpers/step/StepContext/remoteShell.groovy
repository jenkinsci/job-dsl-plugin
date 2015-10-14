job('example-1') {
    steps {
        remoteShell('root@example.com:22', 'echo Hello World!')
    }
}

job('example-2') {
    steps {
        remoteShell('root@example.com:22', 'echo Hello', 'echo World!')
    }
}

job('example-3') {
    steps {
        remoteShell('root@example.com:22', ['echo Hello', 'echo World!'])
    }
}

job('example-4') {
    steps {
        remoteShell('root@example.com:22') {
            command('echo Hello', 'echo World!')
            command('echo How are you?')
            command(["echo I'm fine!", 'echo And you?'])
            command(readFileFromWorkspace('script.sh'))
        }
    }
}
