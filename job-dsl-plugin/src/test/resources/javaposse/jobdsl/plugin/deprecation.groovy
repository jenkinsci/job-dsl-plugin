multiJob('a') {
    steps {
        phase('b') {
            job('c')
        }
    }
}
