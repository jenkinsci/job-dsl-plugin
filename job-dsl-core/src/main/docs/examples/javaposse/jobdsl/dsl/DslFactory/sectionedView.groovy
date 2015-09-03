sectionedView('project-summary') {
    filterBuildQueue()
    filterExecutors()
    sections {
        listView {
            name('Project A')
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
        listView {
            name('Project B')
            jobs {
                regex(/project-B-.*/)
            }
            jobFilters {
                regex {
                    matchValue(RegexMatchValue.DESCRIPTION)
                    regex(/.*-project-B-.*/)
                }
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }
    }
}
