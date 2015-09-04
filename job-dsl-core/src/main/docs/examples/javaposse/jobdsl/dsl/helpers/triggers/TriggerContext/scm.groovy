job('example-1') {
    triggers {
        scm('@daily')
    }
}

job('example-2') {
    triggers {
        scm('@midnight') {
            ignorePostCommitHooks()
        }
    }
}
