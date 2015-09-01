// checkout feature_branch1
job('example-1') {
    scm {
        hg('http://scm', 'feature_branch1')
    }
}

// clean checkout module1 from feature_branch1
job('example-2') {
    scm {
        hg('http://scm') {
            branch('feature_branch1')
            modules('module1')
            clean(true)
        }
    }
}
