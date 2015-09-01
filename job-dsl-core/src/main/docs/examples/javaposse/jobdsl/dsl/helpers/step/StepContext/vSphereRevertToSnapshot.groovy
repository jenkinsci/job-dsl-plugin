job('example') {
    steps {
        vSphereRevertToSnapshot('vsphere.acme.org', 'foo', 'clean')
    }
}
