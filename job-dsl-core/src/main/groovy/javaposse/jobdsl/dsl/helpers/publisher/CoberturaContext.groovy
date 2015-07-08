package javaposse.jobdsl.dsl.helpers.publisher

import groovy.transform.PackageScope
import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNull

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
    Map<String, CoberturaTarget> targets = [
        'METHOD': new CoberturaTarget(
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

    void onlyStable(boolean onlyStable) {
        this.onlyStable = onlyStable
    }

    void failUnhealthy(boolean failUnhealthy) {
        this.failUnhealthy = failUnhealthy
    }

    void failUnstable(boolean failUnstable) {
        this.failUnstable = failUnstable
    }

    void autoUpdateHealth(boolean autoUpdateHealth) {
        this.autoUpdateHealth = autoUpdateHealth
    }

    void autoUpdateStability(boolean autoUpdateStability) {
        this.autoUpdateStability = autoUpdateStability
    }

    void zoomCoverageChart(boolean zoomCoverageChart) {
        this.zoomCoverageChart = zoomCoverageChart
    }

    void failNoReports(boolean failNoReports) {
        this.failNoReports = failNoReports
    }

    void methodTarget(Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        this.target(TargetType.METHOD.name(), healthy, unhealthy, failing)
    }

    void lineTarget(Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        this.target(TargetType.LINE.name(), healthy, unhealthy, failing)
    }

    void conditionalTarget(Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        this.target(TargetType.CONDITIONAL.name(), healthy, unhealthy, failing)
    }

    void fileTarget(Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        this.target(TargetType.FILES.name(), healthy, unhealthy, failing)
    }

    void classTarget(Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        this.target(TargetType.CLASSES.name(), healthy, unhealthy, failing)
    }

    void packageTarget(Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        this.target(TargetType.PACKAGES.name(), healthy, unhealthy, failing)
    }

    @PackageScope
    void target(String targetType, Integer healthy = 8000000, Integer unhealthy = 0, Integer failing = 0) {
        checkArgument(TargetType.values().any { it.toString() == targetType },
                "Invalid target type: $targetType, available target types: ${TargetType.values()}")
        checkArgument((0..100).contains(healthy), 'Invalid healthyTarget treshold, percentage (0-100) expected')
        checkArgument((0..100).contains(unhealthy), 'Invalid unhealthyTarget treshold, percentage (0-100) expected')
        checkArgument((0..100).contains(failing), 'Invalid failingTarget treshold, percentage (0-100) expected')
        this.targets.put(targetType, new CoberturaTarget(
            targetType: targetType,
            healthyTarget: healthy * 100000,
            unhealthyTarget: unhealthy * 100000,
            failingTarget: failing * 100000
        ))
    }

    void sourceEncoding(String sourceEncoding) {
        checkNotNull(sourceEncoding, 'Source encoding must not be null!')
        this.sourceEncoding = sourceEncoding
    }

    static class CoberturaTarget {
        String targetType
        String healthyTarget
        String unhealthyTarget
        String failingTarget
    }
}
