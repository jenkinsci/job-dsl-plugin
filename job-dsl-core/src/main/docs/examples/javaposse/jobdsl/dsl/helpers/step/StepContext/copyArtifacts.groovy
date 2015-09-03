job('example') {
    steps {
        copyArtifacts('upstream') {
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
