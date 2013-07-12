package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class JacocoContext implements Context {
    String execPattern
    String classPattern
    String sourcePattern
    String inclusionPattern
    String exclusionPattern
    String minimumInstructionCoverage
    String minimumBranchCoverage
    String minimumComplexityCoverage
    String minimumLineCoverage
    String minimumMethodCoverage 
    String minimumClassCoverage
    String maximumInstructionCoverage
    String maximumBranchCoverage 
    String maximumComplexityCoverage
    String maximumLineCoverage 
    String maximumMethodCoverage
    String maximumClassCoverage

    void execPattern(String execPattern) {
        this.execPattern = execPattern
    }

    void classPattern(String classPattern) {
        this.classPattern = classPattern
    }

    void sourcePattern(String sourcePattern) {
        this.sourcePattern = sourcePattern
    }

    void inclusionPattern(String inclusionPattern) {
        this.inclusionPattern = inclusionPattern
    }

    void exclusionPattern(String exclusionPattern) {
        this.exclusionPattern = exclusionPattern
    }

    void minimumInstructionCoverage(String minimumInstructionCoverage) {
        this.minimumInstructionCoverage = minimumInstructionCoverage
    }

    void minimumBranchCoverage(String minimumBranchCoverage) {
        this.minimumBranchCoverage = minimumBranchCoverage
    }

    void minimumComplexityCoverage(String minimumComplexityCoverage) {
        this.minimumComplexityCoverage = minimumComplexityCoverage
    }

    void minimumLineCoverage(String minimumLineCoverage) {
        this.minimumLineCoverage = minimumLineCoverage
    }

    void minimumMethodCoverage(String minimumMethodCoverage) {
        this.minimumMethodCoverage = minimumMethodCoverage
    }

    void minimumClassCoverage(String minimumClassCoverage) {
        this.minimumClassCoverage = minimumClassCoverage
    }

    void maximumInstructionCoverage(String maximumInstructionCoverage) {
        this.maximumInstructionCoverage = maximumInstructionCoverage
    }

    void maximumBranchCoverage(String maximumBranchCoverage) {
        this.maximumBranchCoverage = maximumBranchCoverage
    }

    void maximumComplexityCoverage(String maximumComplexityCoverage) {
        this.maximumComplexityCoverage = maximumComplexityCoverage
    }

    void maximumLineCoverage(String maximumLineCoverage) {
        this.maximumLineCoverage = maximumLineCoverage
    }

    void maximumMethodCoverage(String maximumMethodCoverage) {
        this.maximumMethodCoverage = maximumMethodCoverage
    }

    void maximumClassCoverage(String maximumClassCoverage) {
        this.maximumClassCoverage = maximumClassCoverage
    }
}