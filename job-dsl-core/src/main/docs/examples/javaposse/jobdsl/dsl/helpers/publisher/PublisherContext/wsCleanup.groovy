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

// always remove the 'output' directory and don't fail the build for any errors
job('example-3') {
    publishers {
        wsCleanup {
            includePattern('output/**')
            deleteDirectories(true)
            setFailBuild(false)
        }
    }
}
