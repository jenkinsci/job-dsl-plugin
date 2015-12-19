buildFlowJob('example-1') {
    buildFlow('build("job1")')
    buildNeedsWorkspace()
}
