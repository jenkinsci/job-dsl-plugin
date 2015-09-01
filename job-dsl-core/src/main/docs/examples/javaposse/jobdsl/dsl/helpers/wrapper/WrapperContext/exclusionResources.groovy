job('example') {
    wrappers {
        exclusionResources('first', 'second')
    }
    steps {
        criticalBlock {
            shell('echo Hello World!')
        }
    }
}
