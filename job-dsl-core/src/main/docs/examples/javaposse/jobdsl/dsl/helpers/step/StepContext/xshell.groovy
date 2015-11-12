job('example') {
    steps {
        xshell {
            commandLine('echo Hi!')
        }
        xshell {
            commandLine('echo Hi!')
            executableInWorkspaceDir()
        }
        xshell {
            commandLine('echo Hi!')
            executableInWorkspaceDir()
            regexToKill('.*regexp.*')
        }
        xshell {
            commandLine('echo Hi!')
            executableInWorkspaceDir()
            regexToKill('.*regexp.*')
            timeAllocated(10)
        }
    }
}
