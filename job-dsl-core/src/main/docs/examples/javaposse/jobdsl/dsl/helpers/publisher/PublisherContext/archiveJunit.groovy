job('example-1') {
    publishers {
        archiveJunit('**/target/surefire-reports/*.xml')
    }
}

job('example-2') {
    publishers {
        archiveJunit('**/minitest-reports/*.xml') {
            allowEmptyResults()
            retainLongStdout()
            healthScaleFactor(1.5)
            testDataPublishers {
                publishTestStabilityData()
            }
        }
    }
}
