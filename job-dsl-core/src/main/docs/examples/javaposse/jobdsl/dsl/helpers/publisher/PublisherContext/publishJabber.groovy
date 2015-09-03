job('example') {
    publishers {
        publishJabber('*room@example.org') {
            strategyName('STATECHANGE_ONLY')
            notifySuspects()
            channelNotificationName('BuildParameters')
        }
    }
}
