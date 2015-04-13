package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class CopyArtifactSelectorContext implements Context {
    Node selector

    CopyArtifactSelectorContext() {
        latestSuccessful()
    }

    /**
     * Upstream build that triggered this job.
     *
     * @param fallback Use "Last successful build" as fallback
     */
    void upstreamBuild(boolean fallback = false) {
        createSelectorNode('TriggeredBuild') {
            if (fallback) {
                fallbackToLastSuccessful(true)
            }
        }
    }

    /**
     * Latest successful build.
     */
    void latestSuccessful(boolean stable = false) {
        createSelectorNode('StatusBuild') {
            if (stable) {
                delegate.stable(true)
            }
        }
    }

    /**
     * Latest saved build (marked "keep forever").
     */
    void latestSaved() {
        createSelectorNode('SavedBuild')
    }

    /**
     * Specified by permalink.
     *
     * @param linkName Values like lastBuild, lastStableBuild
     */
    void permalink(String linkName) {
        createSelectorNode('PermalinkBuild') {
            id(linkName)
        }
    }

    /**
     * Specific build.
     */
    void buildNumber(int buildNumber) {
        this.buildNumber(Integer.toString(buildNumber))
    }

    /**
     * @since 1.22
     */
    void buildNumber(String buildNumber) {
        createSelectorNode('SpecificBuild') {
            delegate.buildNumber(buildNumber)
        }
    }

    /**
     * Copy from workspace of latest completed build.
     */
    void workspace() {
        createSelectorNode('Workspace')
    }

    /**
     * Specified by build parameter.
     */
    void buildParameter(String parameterName) {
        createSelectorNode('ParameterizedBuild') {
            delegate.parameterName(parameterName)
        }
    }

    private void createSelectorNode(String type, Closure nodeBuilder = null) {
        selector = new NodeBuilder().'selector'(class: "hudson.plugins.copyartifact.${type}Selector", nodeBuilder)
    }
}
