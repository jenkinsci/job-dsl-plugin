job('example') {
    steps {
        artifactDeployer {
            includes('*.jar')
            baseDir('target')
            remoteFileLocation('jars/${BUILD_NUMBER}')
            deleteRemoteArtifacts()
        }
    }
}
