job('example') {
    publishers {
        irc {
            channel('#channel1', 'password1', true)
            channel(name: '#channel2', password: 'password2', notificationOnly: false)
            notifyScmCommitters()
            notifyScmCulprits()
            notifyUpstreamCommitters(false)
            notifyScmFixers()
            strategy('ALL')
            notificationMessage('SummaryOnly')
        }
    }
}
