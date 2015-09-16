package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

abstract class AbstractDownstreamTriggerContext extends AbstractContext {
    DownstreamTriggerParameterContext parameterContext

    protected AbstractDownstreamTriggerContext(JobManagement jobManagement) {
        super(jobManagement)
        parameterContext = new DownstreamTriggerParameterContext(jobManagement)
    }

    /**
     * Adds parameter values for the projects to trigger.
     *
     * @since 1.38
     */
    void parameters(@DslContext(DownstreamTriggerParameterContext) Closure closure) {
        ContextHelper.executeInContext(closure, parameterContext)
    }

    /**
     * Copies parameters from the current build, except for file parameters.
     */
    @Deprecated
    void currentBuild() {
        jobManagement.logDeprecationWarning()
        parameterContext.currentBuild()
    }

    /**
     * Reads parameters from a properties file.
     */
    @Deprecated
    void propertiesFile(String propFile, boolean failTriggerOnMissing = false) {
        jobManagement.logDeprecationWarning()
        parameterContext.propertiesFile(propFile, failTriggerOnMissing)
    }

    /**
     * Passes the Git commit that was used in this build to the downstream builds.
     */
    @Deprecated
    void gitRevision(boolean combineQueuedCommits = false) {
        jobManagement.logDeprecationWarning()
        parameterContext.gitRevision(combineQueuedCommits)
    }

    /**
     * Adds a parameter. Can be called multiple times to add more parameters.
     */
    @Deprecated
    void predefinedProp(String key, String value) {
        jobManagement.logDeprecationWarning()
        parameterContext.predefinedProp(key, value)
    }

    /**
     * Adds parameters. Can be called multiple times to add more parameters.
     */
    @Deprecated
    void predefinedProps(Map<String, String> predefinedPropsMap) {
        jobManagement.logDeprecationWarning()
        parameterContext.predefinedProps(predefinedPropsMap)
    }

    /**
     * Adds parameters. Can be called multiple times to add more parameters.
     */
    @Deprecated
    void predefinedProps(String predefinedProps) {
        jobManagement.logDeprecationWarning()
        parameterContext.predefinedProps(predefinedProps)
    }

    /**
     * Specifies a Groovy filter expression that restricts the subset of combinations that the downstream project will
     * run.
     */
    @Deprecated
    void matrixSubset(String groovyFilter) {
        jobManagement.logDeprecationWarning()
        parameterContext.matrixSubset(groovyFilter)
    }

    /**
     * Passes the Subversion revisions that were used in this build to the downstream builds.
     */
    @Deprecated
    void subversionRevision(boolean includeUpstreamParameters = false) {
        jobManagement.logDeprecationWarning()
        parameterContext.subversionRevision(includeUpstreamParameters)
    }

    /**
     * Adds a boolean parameter. Can be called multiple times to add more parameters.
     */
    @Deprecated
    void boolParam(String paramName, boolean defaultValue = false) {
        jobManagement.logDeprecationWarning()
        parameterContext.booleanParam(paramName, defaultValue)
    }

    /**
     * Uses the same node for the triggered builds that was used for this build.
     */
    @Deprecated
    void sameNode(boolean sameNode = true) {
        jobManagement.logDeprecationWarning()
        parameterContext.sameNode = sameNode
    }

    /**
     * Defines where the target job should be executed, the value must match either a label or a node name.
     *
     * @since 1.26
     */
    @Deprecated
    void nodeLabel(String paramName, String nodeLabel) {
        jobManagement.logDeprecationWarning()
        parameterContext.nodeLabel(paramName, nodeLabel)
    }
}
