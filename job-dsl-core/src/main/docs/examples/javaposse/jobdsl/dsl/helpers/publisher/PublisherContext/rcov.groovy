job('example') {
    publishers {
        rcov {
            reportDirectory('folder')
            totalCoverage(80, 50, 0)
            codeCoverage(80, 50, 0)
        }
    }
}
