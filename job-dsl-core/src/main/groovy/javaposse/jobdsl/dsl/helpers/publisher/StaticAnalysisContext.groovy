package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class StaticAnalysisContext implements Context {

    private static final List<String> THRESHOLD_LIMITS = ['low', 'normal', 'high']
    private static final ALLOWED_THRESHOLDS = ['unstableTotal', 'failedTotal', 'unstableNew', 'failedNew']
    private static final ALLOWED_THRESHOLD_TYPES = THRESHOLD_LIMITS + 'all'

    Integer healthy
    Integer unHealthy
    boolean canRunOnFailed = false
    String thresholdLimit = 'low'
    String defaultEncoding = ''
    boolean useStableBuildAsReference = false
    boolean useDeltaValues = false
    boolean shouldDetectModules = false
    boolean dontComputeNew = true
    boolean doNotResolveRelativePaths = true
    Map thresholdMap = [:]

    def healthLimits(Integer healthy, Integer unHealthy) {
        this.healthy = healthy
        this.unHealthy = unHealthy
    }

    def canRunOnFailed(canRunOnFailed = true) {
        this.canRunOnFailed = canRunOnFailed
    }

    def thresholdLimit(String limit) {
        assert THRESHOLD_LIMITS.contains(limit), "thresholdLimit must be one of these values: ${THRESHOLD_LIMITS.join(',')}"
        this.thresholdLimit = limit
    }

    def defaultEncoding(String encoding) {
        this.defaultEncoding = encoding
    }

    def useStableBuildAsReference(boolean useStableBuildAsReference = true) {
        this.useStableBuildAsReference = useStableBuildAsReference
    }

    def useDeltaValues(boolean useDeltaValues = true) {
        this.useDeltaValues = useDeltaValues
    }

    def shouldDetectModules(boolean shouldDetectModules = true) {
        this.shouldDetectModules = shouldDetectModules
    }

    def computeNew(computeNew) {
        this.dontComputeNew = !computeNew
    }

    def thresholds(Map thresholdMap) {
        assert ALLOWED_THRESHOLDS.containsAll(thresholdMap.keySet()),
                "Only the thresholds ${ALLOWED_THRESHOLDS.join(',')} are allowed. You used ${thresholdMap}."
        assert ALLOWED_THRESHOLD_TYPES.containsAll(thresholdMap.values()*.keySet().flatten()),
                "Threshold only can use the types ${ALLOWED_THRESHOLD_TYPES.join(',')}. You used ${thresholdMap}."
        this.thresholdMap = thresholdMap
        this.dontComputeNew = ! thresholdMap.keySet().find { it.contains('New')}
    }
}
