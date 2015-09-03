// aggregates test results from project-A and project-B and includes failed builds in the results
job('example-1') {
    publishers {
        aggregateDownstreamTestResults('project-A, project-B', true)
    }
}

// aggregates test results from all downstream jobs and ignores failed builds
job('example-2') {
    publishers {
        aggregateDownstreamTestResults()
    }
}
