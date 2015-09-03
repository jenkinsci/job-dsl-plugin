job('example') {
    steps {
        debianPackage('module') {
            generateChangelog()
        }
    }
}
