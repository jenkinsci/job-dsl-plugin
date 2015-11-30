job('example') {
    steps {
        vSphereDeployFromTemplate('vsphere.acme.org', 'template', 'clone', 'cluster')
    }
}
