package javaposse.jobdsl.dsl.helpers.publisher

import com.google.common.base.Preconditions
import groovy.transform.PackageScope
import javaposse.jobdsl.dsl.helpers.Context

class CoberturaContext implements Context {

    boolean onlyStable = false
    boolean failUnhealthy = false
    boolean failUnstable = false
    boolean autoUpdateHealth = false
    boolean autoUpdateStability = false
    boolean zoomCoverageChart = false
    boolean failNoReports = true

    enum TargetType {
        METHOD, LINE, CONDITIONAL, PACKAGES, FILES, CLASSES
    }
    def targets = [
        "METHOD": new CoberturaTarget(
            targetType: TargetType.METHOD,
            healthyTarget: 8000000,
            unhealthyTarget: 0,
            failingTarget: 0
        ),
        'LINE': new CoberturaTarget(
            targetType: TargetType.LINE,
            healthyTarget: 8000000,
            unhealthyTarget: 0,
            failingTarget: 0
        ),
        'CONDITIONAL': new CoberturaTarget(
            targetType: TargetType.CONDITIONAL,
            healthyTarget: 7000000,
            unhealthyTarget: 0,
            failingTarget: 0
        )
    ]

    String sourceEncoding = 'ASCII'

    /**
     * <project>
     *  <publishers>
     *   <hudson.plugins.cobertura.CoberturaPublisher>
     *    <onlyStable>false</onlyStable>
     *
     * @param onlyStable
     */
    void onlyStable(boolean onlyStable) {
        this.onlyStable = onlyStable
    }

    /**
     * <project>
     *  <publishers>
     *   <hudson.plugins.cobertura.CoberturaPublisher>
     *    <failUnhealthy>false</failUnhealthy>
     *
     * @param failUnhealthy
     */
    void failUnhealthy(boolean failUnhealthy) {
        this.failUnhealthy = failUnhealthy
    }

    /**
     * <project>
     *  <publishers>
     *   <hudson.plugins.cobertura.CoberturaPublisher>
     *    <failUnstable>false</failUnstable>
     *
     * @param failUnstable
     */
    void failUnstable(boolean failUnstable) {
        this.failUnstable = failUnstable
    }

    /**
     * <project>
     *  <publishers>
     *   <hudson.plugins.cobertura.CoberturaPublisher>
     *    <autoUpdateHealth>false</autoUpdateHealth>
     *
     * @param autoUpdateHealth
     */
    void autoUpdateHealth(boolean autoUpdateHealth) {
        this.autoUpdateHealth = autoUpdateHealth
    }

    /**
     * <project>
     *  <publishers>
     *   <hudson.plugins.cobertura.CoberturaPublisher>
     *    <autoUpdateStability>false</autoUpdateStability>
     *
     * @param autoUpdateStability
     */
    void autoUpdateStability(boolean autoUpdateStability) {
        this.autoUpdateStability = autoUpdateStability
    }

    /**
     * <project>
     *  <publishers>
     *   <hudson.plugins.cobertura.CoberturaPublisher>
     *    <zoomCoverageChart>false</failNoReports>
     *
     * @param zoomCoverageChart
     */
    void zoomCoverageChart(boolean zoomCoverageChart) {
        this.zoomCoverageChart = zoomCoverageChart
    }

    /**
     * <project>
     *  <publishers>
     *   <hudson.plugins.cobertura.CoberturaPublisher>
     *    <failNoReports>true</failNoReports>
     *
     * @param failNoReports
     */
    void failNoReports(boolean failNoReports) {
        this.failNoReports = failNoReports
    }

    /**
     * @see target('METHOD')
     *
     * @param healthy
     * @param unhealthy
     * @param failing
     */
    void methodTarget(Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        this.target(TargetType.METHOD.name(), healthy, unhealthy, failing)
    }

    /**
     * @see target('LINE')
     *
     * @param healthy
     * @param unhealthy
     * @param failing
     */
    void lineTarget(Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        this.target(TargetType.LINE.name(), healthy, unhealthy, failing)
    }

    /**
     * @see target('CONDITIONAL')
     *
     * @param healthy
     * @param unhealthy
     * @param failing
     */
    void conditionalTarget(Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        this.target(TargetType.CONDITIONAL.name(), healthy, unhealthy, failing)
    }

    /**
     * @see target('FILES')
     *
     * @param healthy
     * @param unhealthy
     * @param failing
     */
    void fileTarget(Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        this.target(TargetType.FILES.name(), healthy, unhealthy, failing)
    }

    /**
     * @see target('CLASSES')
     *
     * @param healthy
     * @param unhealthy
     * @param failing
     */
    void classTarget(Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        this.target(TargetType.CLASSES.name(), healthy, unhealthy, failing)
    }

    /**
     * @see target('PACKAGES')
     *
     * @param healthy
     * @param unhealthy
     * @param failing
     */
    void packageTarget(Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        this.target(TargetType.PACKAGES.name(), healthy, unhealthy, failing)
    }

    /**
     * <project>
     *  <publishers>
     *   <hudson.plugins.cobertura.CoberturaPublisher>
     *    <healthyTarget>
     *     <targets class="enum-map" enum-type="hudson.plugins.cobertura.targets.CoverageMetric">
     *     <entry>
     *      <hudson.plugins.cobertura.targets.CoverageMetric>METHOD</hudson.plugins.cobertura.targets.CoverageMetric>
     *        METHOD
     *      </hudson.plugins.cobertura.targets.CoverageMetric>METHOD</hudson.plugins.cobertura.targets.CoverageMetric>
     *      <int>8000000</int>
     *     </entry>
     *     <targets>
     *    </healthyTarget>
     *    <unhealthyTarget>
     *     <targets class="enum-map" enum-type="hudson.plugins.cobertura.targets.CoverageMetric">
     *     <entry>
     *      <hudson.plugins.cobertura.targets.CoverageMetric>METHOD</hudson.plugins.cobertura.targets.CoverageMetric>
     *        LINE
     *      </hudson.plugins.cobertura.targets.CoverageMetric>METHOD</hudson.plugins.cobertura.targets.CoverageMetric>
     *      <int>8000000</int>
     *     </entry>
     *     <targets>
     *    </unhealthyTarget>
     *    <failingTarget>
     *     <targets class="enum-map" enum-type="hudson.plugins.cobertura.targets.CoverageMetric">
     *     <entry>
     *      <hudson.plugins.cobertura.targets.CoverageMetric>METHOD</hudson.plugins.cobertura.targets.CoverageMetric>
     *        CONDITIONAL
     *      </hudson.plugins.cobertura.targets.CoverageMetric>METHOD</hudson.plugins.cobertura.targets.CoverageMetric>
     *      <int>7000000</int>
     *     </entry>
     *     <targets>
     *    </failingTarget>
     *
     * @param targetType
     * @param healthy
     * @param unhealthy
     * @param failing
     */
    @PackageScope
    void target(String targetType, Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        Preconditions.checkArgument(
            TargetType.values().any { it.toString() == targetType }, "Invalid target type: $targetType " +
            'Available target types: ' + TargetType.values())
        Preconditions.checkArgument((0..100).contains(healthy), 'Invalid healthyTarget treshold, percentage (0-100) expected')
        Preconditions.checkArgument((0..100).contains(unhealthy), 'Invalid unhealthyTarget treshold, percentage (0-100) expected')
        Preconditions.checkArgument((0..100).contains(failing), 'Invalid failingTarget treshold, percentage (0-100) expected')
        this.targets.put(targetType, new CoberturaTarget(
            targetType: targetType,
            healthyTarget: healthy * 100000,
            unhealthyTarget: unhealthy * 100000,
            failingTarget: failing * 100000
        ))
    }

    /**
     * <project>
     *  <publishers>
     *   <hudson.plugins.cobertura.CoberturaPublisher>
     *    <sourceEncoding>UTF-8</sourceEncoding>
     *
     * @param sourceEncoding
     */
    void sourceEncoding(String sourceEncoding) {
        Preconditions.checkNotNull(sourceEncoding, 'Source encoding must not be null!')
        this.sourceEncoding = sourceEncoding
    }

    static class CoberturaTarget {
        String targetType
        String healthyTarget
        String unhealthyTarget
        String failingTarget
    }
}
