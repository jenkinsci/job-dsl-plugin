// marks a build as unstable if "[ERROR]" has been in found in any log file
job('example') {
    publishers {
        textFinder(/[ERROR]/, '**/*.log', false, false, true)
    }
}
