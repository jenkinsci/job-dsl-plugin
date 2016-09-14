job('example') {
    wrappers {
        exclusionResources('FIRST', 'SECOND')
    }
    steps {
        criticalBlock {
            shell('echo Hello World!')
        }
    }
}
