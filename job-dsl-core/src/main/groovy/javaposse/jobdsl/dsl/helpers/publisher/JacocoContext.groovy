package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class JacocoContext implements Context {
    String execPattern = "**/target/**.exec"
    String classPattern = "**/classes"
    String sourcePattern = "**/src/main/java"
    String inclusionPattern = "**/*.class"
    String exclusionPattern = "**/*Test*"
    String minimumInstructionCoverage = "0"
    String minimumBranchCoverage = "0"
    String minimumComplexityCoverage = "0"
    String minimumLineCoverage = "0"
    String minimumMethodCoverage = "0"
    String minimumClassCoverage = "0"
    String maximumInstructionCoverage = "0"
    String maximumBranchCoverage  = "0"
    String maximumComplexityCoverage = "0"
    String maximumLineCoverage  = "0"
    String maximumMethodCoverage = "0"
    String maximumClassCoverage = "0"
    Boolean changeBuildStatus = null

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

    void changeBuildStatus(boolean change = true) {
        this.changeBuildStatus = change
    }
}