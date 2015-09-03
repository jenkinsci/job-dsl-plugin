job('example') {
    publishers {
        mavenDeploymentLinker('.*.tar.gz')
    }
}
