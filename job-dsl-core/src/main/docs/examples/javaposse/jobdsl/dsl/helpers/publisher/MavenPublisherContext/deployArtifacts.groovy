mavenJob('example-1') {
    publishers {
        deployArtifacts()
    }
}

mavenJob('example-2') {
    publishers {
        deployArtifacts {
            evenIfUnstable()
        }
    }
}
