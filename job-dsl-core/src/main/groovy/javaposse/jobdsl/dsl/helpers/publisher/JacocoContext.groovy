package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class JacocoContext extends AbstractContext {
    String execPattern = '**/target/**.exec'
    String classPattern = '**/classes'
    String sourcePattern = '**/src/main/java'
    String inclusionPattern = '**/*.class'
    String exclusionPattern = '**/*Test*'
    String minimumInstructionCoverage = '0'
    String minimumBranchCoverage = '0'
    String minimumComplexityCoverage = '0'
    String minimumLineCoverage = '0'
    String minimumMethodCoverage = '0'
    String minimumClassCoverage = '0'
    String maximumInstructionCoverage = '0'
    String maximumBranchCoverage = '0'
    String maximumComplexityCoverage = '0'
    String maximumLineCoverage = '0'
    String maximumMethodCoverage = '0'
    String maximumClassCoverage = '0'
    Boolean changeBuildStatus = null

    protected JacocoContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets the path to the exec files. Defaults to {@code '**&#47;target/**.exec'}.
     */
    void execPattern(String execPattern) {
        this.execPattern = execPattern
    }

    /**
     * Sets the path to the class directories. Defaults to {@code '**&#47;classes'}.
     */
    void classPattern(String classPattern) {
        this.classPattern = classPattern
    }

    /**
     * Sets the path to the source directories. Defaults to {@code '**&#47;src/main/java'}.
     */
    void sourcePattern(String sourcePattern) {
        this.sourcePattern = sourcePattern
    }

    /**
     * Allows to include certain classes. Defaults to {@code '**&#47;*.class'}.
     */
    void inclusionPattern(String inclusionPattern) {
        this.inclusionPattern = inclusionPattern
    }

    /**
     * Allows to exclude certain classes. Defaults to {@code '**&#47;*Test*'}.
     */
    void exclusionPattern(String exclusionPattern) {
        this.exclusionPattern = exclusionPattern
    }

    /**
     * Reports health as 0% if instruction coverage is less than specified. Defaults to {@code '0'}.
     */
    void minimumInstructionCoverage(String minimumInstructionCoverage) {
        this.minimumInstructionCoverage = minimumInstructionCoverage
    }

    /**
     * Reports health as 0% if branch coverage is less than specified. Defaults to {@code '0'}.
     */
    void minimumBranchCoverage(String minimumBranchCoverage) {
        this.minimumBranchCoverage = minimumBranchCoverage
    }

    /**
     * Reports health as 0% if complexity coverage is less than specified. Defaults to {@code '0'}.
     */
    void minimumComplexityCoverage(String minimumComplexityCoverage) {
        this.minimumComplexityCoverage = minimumComplexityCoverage
    }

    /**
     * Reports health as 0% if line coverage is less than specified. Defaults to {@code '0'}.
     */
    void minimumLineCoverage(String minimumLineCoverage) {
        this.minimumLineCoverage = minimumLineCoverage
    }

    /**
     * Reports health as 0% if method coverage is less than specified. Defaults to {@code '0'}.
     */
    void minimumMethodCoverage(String minimumMethodCoverage) {
        this.minimumMethodCoverage = minimumMethodCoverage
    }

    /**
     * Reports health as 0% if class coverage is less than specified. Defaults to {@code '0'}.
     */
    void minimumClassCoverage(String minimumClassCoverage) {
        this.minimumClassCoverage = minimumClassCoverage
    }

    /**
     * Reports health as 100% if instruction coverage is greater than specified. Defaults to {@code '0'}.
     */
    void maximumInstructionCoverage(String maximumInstructionCoverage) {
        this.maximumInstructionCoverage = maximumInstructionCoverage
    }

    /**
     * Reports health as 100% if branch coverage is greater than specified. Defaults to {@code '0'}.
     */
    void maximumBranchCoverage(String maximumBranchCoverage) {
        this.maximumBranchCoverage = maximumBranchCoverage
    }

    /**
     * Reports health as 100% if complexity coverage is greater than specified. Defaults to {@code '0'}.
     */
    void maximumComplexityCoverage(String maximumComplexityCoverage) {
        this.maximumComplexityCoverage = maximumComplexityCoverage
    }

    /**
     * Reports health as 100% if line coverage is greater than specified. Defaults to {@code '0'}.
     */
    void maximumLineCoverage(String maximumLineCoverage) {
        this.maximumLineCoverage = maximumLineCoverage
    }

    /**
     * Reports health as 100% if method coverage is greater than specified. Defaults to {@code '0'}.
     */
    void maximumMethodCoverage(String maximumMethodCoverage) {
        this.maximumMethodCoverage = maximumMethodCoverage
    }

    /**
     * Reports health as 100% if class coverage is greater than specified. Defaults to {@code '0'}.
     */
    void maximumClassCoverage(String maximumClassCoverage) {
        this.maximumClassCoverage = maximumClassCoverage
    }

    /**
     * If set, changes the build status according to the thresholds. Defaults to {@code false}.
     */
    @RequiresPlugin(id = 'jacoco', minimumVersion = '1.0.13')
    void changeBuildStatus(boolean change = true) {
        this.changeBuildStatus = change
    }
}
