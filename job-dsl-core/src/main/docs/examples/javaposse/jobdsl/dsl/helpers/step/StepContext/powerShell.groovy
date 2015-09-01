job('example') {
    steps {
        powerShell('Write-Output "Hello World!"')
        powerShell(readFileFromWorkspace('build.ps1'))
    }
}
