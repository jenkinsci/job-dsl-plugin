job('example') {
    publishers {
        hipChat {
            rooms('Dev Team A', 'QA')
            notifyAborted()
            notifyNotBuilt()
            notifyUnstable()
            notifyFailure()
            notifyBackToNormal()
        }
    }
}
