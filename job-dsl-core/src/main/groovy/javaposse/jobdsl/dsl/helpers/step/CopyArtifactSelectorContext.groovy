package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class CopyArtifactSelectorContext extends AbstractContext {
    Node selector

    CopyArtifactSelectorContext(JobManagement jobManagement) {
        super(jobManagement)
        latestSuccessful()
    }

    /**
     * Selects the upstream build that triggered this job.
     *
     * @param fallback Use "Last successful build" as fallback
     * @param allowUpstream Allow upstream build whose artifacts feed into this build
     */
    void upstreamBuild(boolean fallback = false, boolean allowUpstream = false) {
        createSelectorNode('TriggeredBuild') {
            if (fallback) {
                fallbackToLastSuccessful(true)
            }
            if (allowUpstream) {
                allowUpstreamDependencies(true)
            }
        }
    }

    /**
     * Selects the latest successful build. This is the default selector.
     */
    void latestSuccessful(boolean stable = false) {
        createSelectorNode('StatusBuild') {
            if (stable) {
                delegate.stable(true)
            }
        }
    }

    /**
     * Selects the latest saved build (marked "keep forever").
     */
    void latestSaved() {
        createSelectorNode('SavedBuild')
    }

    /**
     * Selects a build by permalink.
     *
     * @param linkName Values like lastBuild, lastStableBuild
     */
    void permalink(String linkName) {
        createSelectorNode('PermalinkBuild') {
            id(linkName)
        }
    }

    /**
     * Selects a specific build.
     */
    void buildNumber(int buildNumber) {
        this.buildNumber(Integer.toString(buildNumber))
    }

    /**
     * Selects a specific build.
     *
     * @since 1.22
     */
    void buildNumber(String buildNumber) {
        createSelectorNode('SpecificBuild') {
            delegate.buildNumber(buildNumber)
        }
    }

    /**
     * Copies from workspace of latest completed build.
     */
    void workspace() {
        createSelectorNode('Workspace')
    }

    /**
     * Selects a build by parameter.
     */
    void buildParameter(String parameterName) {
        createSelectorNode('ParameterizedBuild') {
            delegate.parameterName(parameterName)
        }
    }

    /**
     * Selects a build triggered by the current MultiJob build.
     *
     * @since 1.40
     */
    @RequiresPlugin(id = 'jenkins-multijob-plugin', minimumVersion = '1.17')
    void multiJobBuild() {
        selector = new NodeBuilder().'selector'(class: 'com.tikal.jenkins.plugins.multijob.MultiJobBuildSelector')
    }

    private void createSelectorNode(String type, Closure nodeBuilder = null) {
        selector = new NodeBuilder().'selector'(class: "hudson.plugins.copyartifact.${type}Selector", nodeBuilder)
    }
}
