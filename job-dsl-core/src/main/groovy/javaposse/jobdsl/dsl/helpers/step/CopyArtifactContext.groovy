package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context

class CopyArtifactContext implements Context {
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
     * Upstream build that triggered this job
     * @arg fallback Use "Last successful build" as fallback
     * @return
     */
    void upstreamBuild(boolean fallback = false) {
        ensureFirst()
        this.fallback = fallback
        selectedSelector = 'TriggeredBuild'
    }

    /**
     * Latest successful build
     * @return
     */
    void latestSuccessful(boolean stable = false) {
        ensureFirst()
        this.stable = stable
        selectedSelector = 'StatusBuild'
    }
    /**
     * Latest saved build (marked "keep forever")
     * @return
     */
    void latestSaved() {
        ensureFirst()
        selectedSelector = 'SavedBuild'
    }
    /**
     * Specified by permalink
     * @param linkName Values like lastBuild, lastStableBuild
     * @return
     */
    void permalink(String linkName) {
        ensureFirst()
        selectedSelector = 'PermalinkBuild'
        permalinkName = linkName
    }

    /**
     * Specific Build
     * @param buildNumber
     * @return
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
     * Copy from WORKSPACE of latest completed build
     * @return
     */
    void workspace() {
        ensureFirst()
        selectedSelector = 'Workspace'
    }

    /**
     * Specified by build parameter
     * @param parameterName
     * @return
     */
    void buildParameter(String parameterName) {
        ensureFirst()
        selectedSelector = 'ParameterizedBuild'
        this.parameterName = parameterName
    }
}
