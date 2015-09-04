job('example') {
    publishers {
        retryBuild {
            rerunIfUnstable()
            retryLimit(3)
            progressiveDelay(60, 600)
        }
    }
}
