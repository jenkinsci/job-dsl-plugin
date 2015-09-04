// cleanup all files
job('example-1') {
    wrappers {
        preBuildCleanup()
    }
}

// cleanup all files and directories in target directories, but only if the CLEANUP build parameter is set to 'true'
job('example-2') {
    wrappers {
        preBuildCleanup {
            includePattern('**/target/**')
            deleteDirectories()
            cleanupParameter('CLEANUP')
        }
    }
}
