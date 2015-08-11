package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

class DownstreamTriggerContext extends AbstractContext {
    DownstreamTriggerParameterContext parameterContext

    DownstreamTriggerContext(JobManagement jobManagement) {
        super(jobManagement)
        parameterContext = new DownstreamTriggerParameterContext(jobManagement)
    }

    void parameters(@DslContext(DownstreamTriggerParameterContext) Closure closure) {
        ContextHelper.executeInContext(closure, parameterContext)
    }

    @Deprecated
    void currentBuild() {
        jobManagement.logDeprecationWarning()
        parameterContext.currentBuild()
    }

    @Deprecated
    void propertiesFile(String propFile, boolean failTriggerOnMissing = false) {
        jobManagement.logDeprecationWarning()
        parameterContext.propertiesFile(propFile, failTriggerOnMissing)
    }

    @Deprecated
    void gitRevision(boolean combineQueuedCommits = false) {
        jobManagement.logDeprecationWarning()
        parameterContext.gitRevision(combineQueuedCommits)
    }

    @Deprecated
    void predefinedProp(String key, String value) {
        jobManagement.logDeprecationWarning()
        parameterContext.predefinedProp(key, value)
    }

    @Deprecated
    void predefinedProps(Map<String, String> predefinedPropsMap) {
        jobManagement.logDeprecationWarning()
        parameterContext.predefinedProps(predefinedPropsMap)
    }

    @Deprecated
    void predefinedProps(String predefinedProps) {
        jobManagement.logDeprecationWarning()
        parameterContext.predefinedProps(predefinedProps)
    }

    @Deprecated
    void matrixSubset(String groovyFilter) {
        jobManagement.logDeprecationWarning()
        parameterContext.matrixSubset(groovyFilter)
    }

    @Deprecated
    void subversionRevision(boolean includeUpstreamParameters = false) {
        jobManagement.logDeprecationWarning()
        parameterContext.subversionRevision(includeUpstreamParameters)
    }

    @Deprecated
    void boolParam(String paramName, boolean defaultValue = false) {
        jobManagement.logDeprecationWarning()
        parameterContext.booleanParam(paramName, defaultValue)
    }

    @Deprecated
    void sameNode(boolean sameNode = true) {
        jobManagement.logDeprecationWarning()
        parameterContext.sameNode = sameNode
    }

    /**
     * @since 1.26
     */
    @Deprecated
    void nodeLabel(String paramName, String nodeLabel) {
        jobManagement.logDeprecationWarning()
        parameterContext.nodeLabel(paramName, nodeLabel)
    }
}
