// delete all files after a build
job('example-1') {
    publishers {
        wsCleanup()
    }
}

// delete all 'src' directories in the directory tree
job('example-2') {
    publishers {
        wsCleanup {
            includePattern('**/src/**')
            deleteDirectories(true)
        }
    }
}
