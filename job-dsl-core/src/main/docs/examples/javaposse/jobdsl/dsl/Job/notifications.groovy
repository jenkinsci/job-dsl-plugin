job('example') {
    notifications {
        endpoint('http://example.com:8080/monitor')
        endpoint('10.100.2.3:3434', 'TCP', 'XML') {
            event('started')
            timeout(60000)
            logLines(100)
        }
    }
}
