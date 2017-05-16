job('example') {
    wrappers {
        versionNumber('1.0.0.${BUILDS_ALL_TIME}', 'VERSION') {
            startDate()
            prefixVariable('')
            buildsToday()
            buildsThisWeek()
            buildsThisMonth()
            buildsThisYear()
            buildsAllTime()
            skipFailedBuilds(false)
            displayBuildName(true)
        }
    }
}
