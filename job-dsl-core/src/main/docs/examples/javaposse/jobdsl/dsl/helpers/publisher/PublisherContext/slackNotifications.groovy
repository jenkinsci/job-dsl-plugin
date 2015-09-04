job('example') {
    publishers {
        slackNotifications {
            projectChannel('Dev Team A')
            notifyAborted()
            notifyFailure()
            notifyNotBuilt()
            notifyUnstable()
            notifyBackToNormal()
        }
    }
}
