listView('example') {
    jobs {
        name('build')
        name('test')
        names('compile', 'deploy')
        regex('project-A-.+')
    }
}
