job('example') {
    publishers {
        slocCount {
            pattern('build/result.xml')
            encoding('UTF-8')
            commentIsCode()
            buildsInGraph(2)
            ignoreBuildFailure()
        }
    }
}
