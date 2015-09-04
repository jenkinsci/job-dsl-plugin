job('example') {
    publishers {
        postBuildTask {
            task('BUILD SUCCESSFUL', 'git clean -fdx')
        }
    }
}
