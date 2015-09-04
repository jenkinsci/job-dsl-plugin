listView('project-A') {
    description('All unstable jobs for project A')
    filterBuildQueue()
    filterExecutors()
    jobs {
        name('release-projectA')
        regex(/project-A-.+/)
    }
    jobFilters {
        status {
            status(Status.UNSTABLE)
        }
    }
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
