job('example') {
    publishers {
        emma('coverage-results/coverage.xml') {
            minClass(20)
            maxClass(80)
            minMethod(20)
            maxMethod(50)
            minBlock(30)
            maxBlock(70)
            minLine(30)
            maxLine(70)
            minCondition(30)
            maxCondition(90)
        }
    }
}
