sectionedView('example') {
    sections {
        testResult {
            name('project-A')
            width('HALF')
            alignment('LEFT')
            jobs {
                regex('project-A-.*')
            }
        }
    }
}
