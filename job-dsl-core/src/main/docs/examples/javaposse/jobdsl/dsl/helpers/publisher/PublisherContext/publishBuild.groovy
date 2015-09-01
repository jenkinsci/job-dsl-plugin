job('example') {
    publishers {
        publishBuild {
            discardOldBuilds(7, 10)
        }
    }
}
