job('example-1') {
    publishers {
        archiveTestNG()
    }
}

job('example-2') {
    publishers {
        archiveTestNG('**/target/test-reports/*.xml')
    }
}

job('example-3') {
    publishers {
        archiveTestNG('**/target/test-reports/*.xml') {
            escapeTestDescription()
            escapeExceptionMessages(false)
            showFailedBuildsInTrendGraph()
            markBuildAsUnstableOnSkippedTests(false)
            markBuildAsFailureOnFailedConfiguration()
        }
    }
}
