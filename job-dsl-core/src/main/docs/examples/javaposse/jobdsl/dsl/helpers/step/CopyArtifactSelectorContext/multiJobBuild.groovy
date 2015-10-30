job('example') {
    steps {
        copyArtifacts('upstream') {
            buildSelector {
                multiJobBuild()
            }
        }
    }
}
