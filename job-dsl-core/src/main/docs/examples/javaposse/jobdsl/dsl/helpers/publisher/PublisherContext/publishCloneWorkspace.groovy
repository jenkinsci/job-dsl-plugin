job('example') {
    publishers {
        publishCloneWorkspace('build/libs/**')
    }
}
