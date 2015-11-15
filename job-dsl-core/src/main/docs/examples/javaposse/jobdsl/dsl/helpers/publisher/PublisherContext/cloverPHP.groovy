job('example') {
    publishers {
        cloverPHP('location') {
            publishHtmlReport {
                reportDir('html')
                disableArchiving()
            }
            healthyTarget(1, 2)
            unhealthyTarget(1, 2)
            failingTarget(1, 2)
        }
    }
}
