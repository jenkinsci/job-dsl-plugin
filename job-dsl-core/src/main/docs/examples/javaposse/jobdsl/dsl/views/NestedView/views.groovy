nestedView('project-a') {
    views {
        listView('overview') {
            jobs {
                regex(/project-A-.*/)
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }
        buildPipelineView('pipeline') {
            selectedJob('project-a-compile')
        }
    }
}
