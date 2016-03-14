job('example') {
    publishers {
        cloverPHP('coverage.xml') {
            publishHtmlReport('reports') {
                disableArchiving()
            }
            healthyMethodCoverage(90)
            healthyStatementCoverage(80)
            unhealthyMethodCoverage(60)
            unhealthyStatementCoverage(50)
            unstableMethodCoverage(50)
            unstableStatementCoverage(40)
        }
    }
}
