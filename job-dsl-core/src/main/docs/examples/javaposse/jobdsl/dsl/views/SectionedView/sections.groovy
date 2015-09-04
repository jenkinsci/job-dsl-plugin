sectionedView('example') {
    sections {
        listView {
            name('project-A')
            width('HALF')
            alignment('LEFT')
            jobs {
                regex('project-A-.*')
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
