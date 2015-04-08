package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class CopyArtifactSelectorContext implements Context {
    String selectedSelector
    boolean fallback
    boolean stable
    String permalinkName
    String buildNumber
    String parameterName

    private void ensureFirst() {
        if (selectedSelector != null) {
            throw new IllegalStateException('Only one selector can be chosen')
        }
    }

    /**
     * Upstream build that triggered this job.
     *
     * @param fallback Use "Last successful build" as fallback
     */
    void upstreamBuild(boolean fallback = false) {
        ensureFirst()
        this.fallback = fallback
        selectedSelector = 'TriggeredBuild'
    }

    /**
     * Latest successful build.
     */
    void latestSuccessful(boolean stable = false) {
        ensureFirst()
        this.stable = stable
        selectedSelector = 'StatusBuild'
    }

    /**
     * Latest saved build (marked "keep forever").
     */
    void latestSaved() {
        ensureFirst()
        selectedSelector = 'SavedBuild'
    }

    /**
     * Specified by permalink.
     *
     * @param linkName Values like lastBuild, lastStableBuild
     */
    void permalink(String linkName) {
        ensureFirst()
        selectedSelector = 'PermalinkBuild'
        permalinkName = linkName
    }

    /**
     * Specific Build.
     */
    void buildNumber(int buildNumber) {
        this.buildNumber(Integer.toString(buildNumber))
    }

    void buildNumber(String buildNumber) {
        ensureFirst()
        selectedSelector = 'SpecificBuild'
        this.buildNumber = buildNumber
    }

    /**
     * Copy from WORKSPACE of latest completed build.
     */
    void workspace() {
        ensureFirst()
        selectedSelector = 'Workspace'
    }

    /**
     * Specified by build parameter.
     */
    void buildParameter(String parameterName) {
        ensureFirst()
        selectedSelector = 'ParameterizedBuild'
        this.parameterName = parameterName
    }
}
