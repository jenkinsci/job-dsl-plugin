job('example') {
    publishers {
        analysisCollector {
            checkstyle()
            dry()
            findbugs()
            pmd()
            tasks()
            warnings()
            thresholds(
                    unstableTotal: [all: 1, high: 2, normal: 3, low: 4]
            )
        }
    }
}
