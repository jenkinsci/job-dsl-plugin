package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

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

    void healthLimits(Integer healthy, Integer unHealthy) {
        this.healthy = healthy
        this.unHealthy = unHealthy
    }

    void canRunOnFailed(canRunOnFailed = true) {
        this.canRunOnFailed = canRunOnFailed
    }

    void thresholdLimit(String limit) {
        assert THRESHOLD_LIMITS.contains(limit),
                "thresholdLimit must be one of these values: ${THRESHOLD_LIMITS.join(',')}"
        this.thresholdLimit = limit
    }

    void defaultEncoding(String encoding) {
        this.defaultEncoding = encoding
    }

    void useStableBuildAsReference(boolean useStableBuildAsReference = true) {
        this.useStableBuildAsReference = useStableBuildAsReference
    }

    void useDeltaValues(boolean useDeltaValues = true) {
        this.useDeltaValues = useDeltaValues
    }

    void shouldDetectModules(boolean shouldDetectModules = true) {
        this.shouldDetectModules = shouldDetectModules
    }

    void computeNew(computeNew) {
        this.dontComputeNew = !computeNew
    }

    void thresholds(Map thresholdMap) {
        assert ALLOWED_THRESHOLDS.containsAll(thresholdMap.keySet()),
                "Only the thresholds ${ALLOWED_THRESHOLDS.join(',')} are allowed. You used ${thresholdMap}."
        assert ALLOWED_THRESHOLD_TYPES.containsAll(thresholdMap.values()*.keySet().flatten()),
                "Threshold only can use the types ${ALLOWED_THRESHOLD_TYPES.join(',')}. You used ${thresholdMap}."
        this.thresholdMap = thresholdMap
        this.dontComputeNew = !thresholdMap.keySet().find { it.contains('New') }
    }
}
