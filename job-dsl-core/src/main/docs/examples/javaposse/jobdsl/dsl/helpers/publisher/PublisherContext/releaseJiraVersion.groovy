job('example') {
    publishers {
        releaseJiraVersion {
            projectKey('PROJECT')
            release('$JiraBuild')
        }
    }
}
