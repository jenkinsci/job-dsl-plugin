categorizedJobsView('example') {
    jobs {
        regex(/configuration_.*/)
    }
    categorizationCriteria {
        regexGroupingRule(/^configuration_([^_]+).*$/)
    }
    columns {
        status()
        categorizedJob()
        buildButton()
    }
}
