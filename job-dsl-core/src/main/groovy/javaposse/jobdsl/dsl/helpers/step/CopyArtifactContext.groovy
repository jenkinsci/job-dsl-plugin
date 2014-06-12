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
    def upstreamBuild(boolean fallback = false) {
        ensureFirst()
        this.fallback = fallback
        selectedSelector = 'TriggeredBuild'
    }

    /**
     * Latest successful build
     * @return
     */
    def latestSuccessful(boolean stable = false) {
        ensureFirst()
        this.stable = stable
        selectedSelector = 'StatusBuild'
    }
    /**
     * Latest saved build (marked "keep forever")
     * @return
     */
    def latestSaved() {
        ensureFirst()
        selectedSelector = 'SavedBuild'
    }
    /**
     * Specified by permalink
     * @param linkName Values like lastBuild, lastStableBuild
     * @return
     */
    def permalink(String linkName) {
        ensureFirst()
        selectedSelector = 'PermalinkBuild'
        permalinkName = linkName
    }

    /**
     * Specific Build
     * @param buildNumber
     * @return
     */
    def buildNumber(int buildNumber) {
        this.buildNumber(Integer.toString(buildNumber))

    }

    def buildNumber(String buildNumber) {
        ensureFirst()
        selectedSelector = 'SpecificBuild'
        this.buildNumber = buildNumber
    }

    /**
     * Copy from WORKSPACE of latest completed build
     * @return
     */
    def workspace() {
        ensureFirst()
        selectedSelector = 'Workspace'
    }

    /**
     * Specified by build parameter
     * @param parameterName
     * @return
     */
    def buildParameter(String parameterName) {
        ensureFirst()
        selectedSelector = 'ParameterizedBuild'
        this.parameterName = parameterName
    }
}
