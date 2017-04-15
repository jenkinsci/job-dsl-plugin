package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

@ContextType('hudson.plugins.copyartifact.BuildSelector')
class CopyArtifactSelectorContext extends AbstractExtensibleContext {
    Node selector

    CopyArtifactSelectorContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
        latestSuccessful()
    }

    @Override
    protected void addExtensionNode(Node node) {
        selector = ContextHelper.toNamedNode('selector', node)
    }

    /**
     * Selects the upstream build that triggered this job.
     *
     * @param fallback Use "Last successful build" as fallback
     */
    void upstreamBuild(boolean fallback = false) {
        upstreamBuild {
            fallbackToLastSuccessful(fallback)
        }
    }

    /**
     * Selects the upstream build that triggered this job.
     *
     * @since 1.40
     */
    void upstreamBuild(@DslContext(CopyArtifactUpstreamBuildSelectorContext) Closure closure) {
        CopyArtifactUpstreamBuildSelectorContext context = new CopyArtifactUpstreamBuildSelectorContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        createSelectorNode('TriggeredBuild') {
            if (context.fallbackToLastSuccessful) {
                fallbackToLastSuccessful(true)
            }
            if (context.allowUpstreamDependencies) {
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
    @RequiresPlugin(id = 'jenkins-multijob-plugin', minimumVersion = '1.22')
    void multiJobBuild() {
        selector = new NodeBuilder().'selector'(class: 'com.tikal.jenkins.plugins.multijob.MultiJobBuildSelector')
    }

    private void createSelectorNode(String type, Closure nodeBuilder = null) {
        selector = new NodeBuilder().'selector'(class: "hudson.plugins.copyartifact.${type}Selector", nodeBuilder)
    }
}
