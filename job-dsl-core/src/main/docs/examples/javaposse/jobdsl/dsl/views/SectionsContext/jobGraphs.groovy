sectionedView('example') {
    sections {
        jobGraphs {
            name('project-A')
            width('HALF')
            alignment('LEFT')
            jobs {
                regex('project-A-.*')
            }
        }
    }
}
