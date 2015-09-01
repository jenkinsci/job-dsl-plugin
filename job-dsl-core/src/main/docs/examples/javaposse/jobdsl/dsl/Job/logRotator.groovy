job('example-1') {
    logRotator(30, -1, 1, -1)
}

job('example-2') {
    logRotator {
        numToKeep(5)
        artifactNumToKeep(1)
    }
}
