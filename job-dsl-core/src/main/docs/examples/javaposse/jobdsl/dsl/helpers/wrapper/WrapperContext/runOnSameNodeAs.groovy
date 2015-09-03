job('example') {
    wrappers {
        runOnSameNodeAs('project-a', true)
    }
}
