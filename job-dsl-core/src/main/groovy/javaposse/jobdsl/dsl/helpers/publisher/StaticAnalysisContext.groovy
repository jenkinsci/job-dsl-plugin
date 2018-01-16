package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class StaticAnalysisContext implements Context {
    private static final List<String> THRESHOLD_LIMITS = ['low', 'normal', 'high']
    private static final Set<String> ALLOWED_THRESHOLDS = ['unstableTotal', 'failedTotal', 'unstableNew', 'failedNew']
    private static final Set<String> ALLOWED_THRESHOLD_TYPES = THRESHOLD_LIMITS + 'all'

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

    /**
     * Specifies the thresholds for the build health.
     */
    void healthLimits(Integer healthy, Integer unhealthy) {
        this.healthy = healthy
        this.unHealthy = unhealthy
    }

    /**
     * If set, also runs when the build has failed. Defaults to {@code false}.
     */
    void canRunOnFailed(Object canRunOnFailed = true) {
        this.canRunOnFailed = canRunOnFailed
    }

    /**
     * Determines which warning priorities should be considered when evaluating the build health.
     * Must be one of {@code 'low'} (default), {@code 'normal'} or {@code 'high'}.
     */
    void thresholdLimit(String limit) {
        Preconditions.checkArgument(
                THRESHOLD_LIMITS.contains(limit),
                "thresholdLimit must be one of these values: ${THRESHOLD_LIMITS.join(',')}"
        )
        this.thresholdLimit = limit
    }

    /**
     * Sets the encoding for parsing or showing files.
     */
    void defaultEncoding(String encoding) {
        this.defaultEncoding = encoding
    }

    /**
     * If set, uses the last stable build as the reference to compute the number of new warnings against. Defaults to
     * {@code false}.
     */
    void useStableBuildAsReference(boolean useStableBuildAsReference = true) {
        this.useStableBuildAsReference = useStableBuildAsReference
    }

    /**
     * If set, computes the number of new warnings by subtracting the total number of warnings of the reference build
     * from the total number of warnings of the current build. Defaults to {@code false}.
     */
    void useDeltaValues(boolean useDeltaValues = true) {
        this.useDeltaValues = useDeltaValues
    }

    /**
     * If set, detects  Ant or Maven modules for all files that contain warnings.
     */
    void shouldDetectModules(boolean shouldDetectModules = true) {
        this.shouldDetectModules = shouldDetectModules
    }

    /**
     * If set, computes new warnings based on the last successful build.
     * This is set automatically if the {@code unstableNew} or {@code failedNew} thresholds are used.
     */
    void computeNew(Object computeNew) {
        this.dontComputeNew = !computeNew
    }

    /**
     * Sets the thresholds for considering a build as unstable or failed.
     *
     * The parameter is a mapping from build status to a mapping from priority to threshold. The keys of the outer map
     * must be one or more of {@code 'unstableTotal'}, {@code 'failedTotal'}, {@code 'unstableNew'} and
     * {@code 'failedNew'}. The keys of the inner map must be one or more of {@code 'all'}, {@code 'low'},
     * {@code 'normal'} and {@code 'high'}.
     */
    void thresholds(Map thresholdMap) {
        Preconditions.checkArgument(
                ALLOWED_THRESHOLDS.containsAll(thresholdMap.keySet()),
                "Only the thresholds ${ALLOWED_THRESHOLDS.join(',')} are allowed. You used ${thresholdMap}."
        )
        Preconditions.checkArgument(
                ALLOWED_THRESHOLD_TYPES.containsAll(thresholdMap.values()*.keySet().flatten()),
                "Threshold only can use the types ${ALLOWED_THRESHOLD_TYPES.join(',')}. You used ${thresholdMap}."
        )
        this.thresholdMap = thresholdMap
        this.dontComputeNew = !thresholdMap.keySet().find { it.contains('New') }
    }
}
