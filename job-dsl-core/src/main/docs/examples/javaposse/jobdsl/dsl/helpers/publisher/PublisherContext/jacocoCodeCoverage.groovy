job('example') {
    publishers {
        jacocoCodeCoverage {
            minimumInstructionCoverage('20')
            minimumBranchCoverage('20')
            minimumComplexityCoverage('20')
            minimumLineCoverage('20')
            minimumMethodCoverage('20')
            minimumClassCoverage('20')
            maximumInstructionCoverage('80')
            maximumBranchCoverage('80')
            maximumComplexityCoverage('80')
            maximumLineCoverage('80')
            maximumMethodCoverage('80')
            maximumClassCoverage('80')
        }
    }
}
