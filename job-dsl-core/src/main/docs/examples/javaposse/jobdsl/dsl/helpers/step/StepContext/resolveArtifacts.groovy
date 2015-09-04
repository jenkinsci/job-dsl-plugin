job('example') {
    steps {
        resolveArtifacts {
            failOnError()
            snapshotUpdatePolicy('always')
            targetDirectory('lib')
            artifact {
                groupId('org.slf4j')
                artifactId('slf4j-api')
                version('[1.7.5,1.7.6]')
            }
            artifact {
                groupId('ch.qos.logback')
                artifactId('logback-classic')
                version('1.1.1')
                classifier('sources')
            }
        }
    }
}
