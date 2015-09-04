job('example') {
    publishers {
        downstream('project-a', 'UNSTABLE')
    }
}
