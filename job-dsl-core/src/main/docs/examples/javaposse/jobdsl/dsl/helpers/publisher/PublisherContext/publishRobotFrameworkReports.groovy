job('example') {
    publishers {
        publishRobotFrameworkReports {
            passThreshold(80.0)
            unstableThreshold(20.0)
            onlyCritical()
        }
    }
}
