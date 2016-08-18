job('example') {
    steps {
        copyS3Artifacts('upstream') {
            includePatterns('*.xml', '*.properties')
            excludePatterns('test.xml', 'test.properties')
            targetDirectory('files')
            flatten()
            optional()
            buildSelector {
                latestSuccessful(true)
            }
        }
    }
}
