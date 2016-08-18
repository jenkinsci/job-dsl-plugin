package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

class CloverPhpContext implements Context {
    final CloverPhpHtmlReportContext publishHtmlReportContext = new CloverPhpHtmlReportContext()
    boolean publishHtmlReport
    String reportDirectory
    String xmlLocation
    Integer healthyMethodCoverage = 70
    Integer healthyStatementCoverage = 80
    Integer unhealthyMethodCoverage
    Integer unhealthyStatementCoverage
    Integer unstableMethodCoverage
    Integer unstableStatementCoverage

    /**
     * Publishes a HTML coverage report.
     */
    void publishHtmlReport(String reportDirectory, @DslContext(CloverPhpHtmlReportContext) Closure closure = null) {
        checkNotNullOrEmpty(reportDirectory, 'reportDirectory must be specified')

        this.publishHtmlReport = true
        this.reportDirectory = reportDirectory
        ContextHelper.executeInContext(closure, publishHtmlReportContext)
    }

    /**
     * Reports health as 100% if method coverage is greater than specified. Defaults to {@code '70'}.
     */
    void healthyMethodCoverage(Integer healthyMethodCoverage) {
        this.healthyMethodCoverage = healthyMethodCoverage
    }

    /**
     * Reports health as 100% if statement coverage is greater than specified. Defaults to {@code '80'}.
     */
    void healthyStatementCoverage(Integer healthyStatementCoverage) {
        this.healthyStatementCoverage = healthyStatementCoverage
    }

    /**
     * Reports health as 0% if method coverage is less than specified. Unset by default.
     */
    void unhealthyMethodCoverage(Integer unhealthyMethodCoverage) {
        this.unhealthyMethodCoverage = unhealthyMethodCoverage
    }

    /**
     * Reports health as 0% if statement coverage is less than specified. Unset by default.
     */
    void unhealthyStatementCoverage(Integer unhealthyStatementCoverage) {
        this.unhealthyStatementCoverage = unhealthyStatementCoverage
    }

    /**
     * Marks the build as unstable if method coverage is less than specified. Unset by default.
     */
    void unstableMethodCoverage(Integer unstableMethodCoverage) {
        this.unstableMethodCoverage = unstableMethodCoverage
    }

    /**
     * Marks the build as unstable if statement coverage is less than specified. Unset by default.
     */
    void unstableStatementCoverage(Integer unstableStatementCoverage) {
        this.unstableStatementCoverage = unstableStatementCoverage
    }
}
