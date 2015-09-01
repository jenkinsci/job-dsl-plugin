// default configuration using the system wide definition
job('example-1') {
    wrappers {
        logSizeChecker()
    }
}

// using job specific configuration, setting the max log size to 10 MB and fail the build of the log file is larger.
job('example-2') {
    wrappers {
        logSizeChecker {
            maxSize(10)
            failBuild()
        }
    }
}
