job('example') {
    steps {
        vSpherePowerOff('vsphere.acme.org', 'foo')
    }
}
