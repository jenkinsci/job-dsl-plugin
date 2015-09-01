job('example') {
    steps {
        prerequisite('project-a, project-b')
    }
}
