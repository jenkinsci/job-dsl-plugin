job('example') {
    steps {
        vSphereDeployFromTemplate {
            server('vsphere.acme.org')
            template('template')
            clone('clone')
            cluster('cluster')
        }
    }
}
