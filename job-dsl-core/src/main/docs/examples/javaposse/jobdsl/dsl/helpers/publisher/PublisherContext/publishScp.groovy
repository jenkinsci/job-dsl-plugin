job('example') {
    publishers {
        publishScp('docs.acme.org') {
            entry('build/docs/**', 'project-a', true)
        }
    }
}
