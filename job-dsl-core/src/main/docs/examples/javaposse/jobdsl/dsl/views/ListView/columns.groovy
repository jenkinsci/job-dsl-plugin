listView('project-A') {
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}

listView('project-B') {
    columns {
        name()
        claim()
        unclaimedTestFailures()
    }
}
