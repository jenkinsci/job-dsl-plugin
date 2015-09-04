job('example') {
    triggers {
        upstream('other', 'UNSTABLE')
    }
}
