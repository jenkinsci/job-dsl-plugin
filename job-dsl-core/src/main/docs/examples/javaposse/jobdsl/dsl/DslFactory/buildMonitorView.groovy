buildMonitorView('project-A') {
    description('All jobs for project A')
    jobs {
        name('release-projectA')
        regex(/project-A-.+/)
    }
}
