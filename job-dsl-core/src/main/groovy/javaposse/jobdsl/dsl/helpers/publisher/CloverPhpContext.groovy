package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class CloverPhpContext implements Context {

    boolean publishHtmlReport = false
    String xmlLocation
    int healthyMethodCoverage = 70
    int healthyStatementCoverage = 80
    int unhealthyMethodCoverage = 0
    int unhealthyStatementCoverage = 0
    int failingMethodCoverage = 0
    int failingStatementCoverage = 0
    final PublishCloverHtmlReportContext publishHtmlReportContext = new PublishCloverHtmlReportContext()

    /**
     * Sets location of clover html report
     */
     void publishHtmlReport(@DslContext(PublishCloverHtmlReportContext) Closure closure) {
        publishHtmlReport = true
        ContextHelper.executeInContext(closure, publishHtmlReportContext)
     }

    /**
     * Sets the path to the xml files.
     */
    void xmlLocation(String xmlLocation) {
        this.xmlLocation = xmlLocation
    }

    /**
     * Reports health coverage is greather than specified.
     */
    void healthyTarget(int healthyMethodCoverage, int healthyStatementCoverage) {
        this.healthyMethodCoverage = healthyMethodCoverage
        this.healthyStatementCoverage = healthyStatementCoverage
    }

    /**
     * Reports unhealth coverage is less than specified.
     */
    void unhealthyTarget(int unhealthyMethodCoverage, int unhealthyStatementCoverage) {
        this.unhealthyMethodCoverage = unhealthyMethodCoverage
        this.unhealthyStatementCoverage = unhealthyStatementCoverage
    }

    /**
     * Reports failing coverage is less than specified.
     */
    void failingTarget(int failingMethodCoverage, int failingStatementCoverage) {
        this.failingMethodCoverage = failingMethodCoverage
        this.failingStatementCoverage = failingStatementCoverage
    }

    /**
     * Reports health as 0% if method coverage is greather than specified. Defaults to {@code '70'}.
     */
    void healthyMethodCoverage(int healthyMethodCoverage) {
        this.healthyMethodCoverage = healthyMethodCoverage
    }

    /**
     * Reports health as 0% if statement coverage is greather than specified. Defaults to {@code '80'}.
     */
    void healthyStatementCoverage(int healthyStatementCoverage) {
        this.healthyStatementCoverage = healthyStatementCoverage
    }

    /**
     * Reports unhealth as 0% if method coverage is less than specified.
     */
    void unhealthyMethodCoverage(int unhealthyMethodCoverage) {
        this.unhealthyMethodCoverage = unhealthyMethodCoverage
    }

    /**
     * Reports unhealth as 0% if statement coverage is less than specified.
     */
    void unhealthyStatementCoverage(int unhealthyStatementCoverage) {
        this.unhealthyStatementCoverage = unhealthyStatementCoverage
    }

    /**
     * Reports unstable as 0% if method coverage is less than specified.
     */
    void failingMethodCoverage(int failingMethodCoverage) {
        this.failingMethodCoverage = failingMethodCoverage
    }

    /**
     * Reports unstable as 0% if statement coverage is less than specified.
     */
    void failingStatementCoverage(int failingStatementCoverage) {
        this.failingStatementCoverage = failingStatementCoverage
    }

    /**
     * If set, publish Html Reports. Defaults to {@code false}.
     */
    void publishHtmlReport(boolean change = true) {
        this.publishHtmlReport = change
    }
}
