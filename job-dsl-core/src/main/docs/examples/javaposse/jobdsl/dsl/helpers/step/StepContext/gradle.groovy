job('example') {
    steps {
        gradle('check')
        gradle {
            tasks('clean')
            tasks('check')
            switches('--info')
        }
    }
}
