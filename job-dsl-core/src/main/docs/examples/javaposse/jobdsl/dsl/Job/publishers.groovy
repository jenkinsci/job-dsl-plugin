job('example') {
    publishers {
        archiveArtifacts('build/test-output/**/*.html')
        archiveJunit('**/target/surefire-reports/*.xml')
    }
}
