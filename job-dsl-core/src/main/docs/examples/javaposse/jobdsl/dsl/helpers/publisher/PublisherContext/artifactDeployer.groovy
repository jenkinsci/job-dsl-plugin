job('example') {
    publishers {
        artifactDeployer {
            artifactsToDeploy {
                includes('*.jar')
                baseDir('target')
                remoteFileLocation('jars/${BUILD_NUMBER}')
                deleteRemoteArtifacts()
            }
        }
    }
}
