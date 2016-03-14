job('example') {
    steps {
        xShell {
            commandLine('echo Hi!')
        }
        xShell {
            commandLine('echo Hi!')
            executableInWorkspaceDir()
        }
        xShell {
            commandLine('echo Hi!')
            executableInWorkspaceDir()
            regexToKill(/.*regexp.*/)
        }
        xShell {
            commandLine('echo Hi!')
            executableInWorkspaceDir()
            regexToKill(/.*regexp.*/)
            timeAllocated(10)
        }
    }
}
