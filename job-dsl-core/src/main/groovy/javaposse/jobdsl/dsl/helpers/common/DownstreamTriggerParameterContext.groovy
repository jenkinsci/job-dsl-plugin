package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

/**
 * @since 1.38
 */
class DownstreamTriggerParameterContext extends AbstractContext {
    Map<String, Boolean> booleanParams = [:]
    boolean sameNode
    boolean currentBuild
    String nodeLabelParam
    String nodeLabel
    String propertiesFile
    boolean failTriggerOnMissing
    boolean gitRevision
    boolean combineQueuedCommits
    List<String> predefinedProps = []
    String matrixSubsetFilter
    boolean subversionRevision
    boolean includeUpstreamParameters

    DownstreamTriggerParameterContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    @Deprecated
    void boolParam(String name, boolean defaultValue = false) {
        jobManagement.logDeprecationWarning()
        booleanParam(name, defaultValue)
    }

    void booleanParam(String name, boolean defaultValue = false) {
        this.booleanParams[name] = defaultValue
    }

    void sameNode() {
        this.sameNode = true
    }

    @Deprecated
    void sameNode(boolean sameNode) {
        jobManagement.logDeprecationWarning()
        this.sameNode = sameNode
    }

    void currentBuild() {
        this.currentBuild = true
    }

    @RequiresPlugin(id = 'nodelabelparameter')
    void nodeLabel(String paramName, String nodeLabel) {
        this.nodeLabelParam = paramName
        this.nodeLabel = nodeLabel
    }

    void propertiesFile(String fileName, boolean failTriggerOnMissing = false) {
        this.propertiesFile = fileName
        this.failTriggerOnMissing = failTriggerOnMissing
    }

    @RequiresPlugin(id = 'git')
    void gitRevision(boolean combineQueuedCommits = false) {
        jobManagement.logPluginDeprecationWarning('git', '2.2.6')

        this.gitRevision = true
        this.combineQueuedCommits = combineQueuedCommits
    }

    void predefinedProp(String key, String value) {
        this.predefinedProps << "${key}=${value}"
    }

    void predefinedProps(Map<String, String> predefinedPropsMap) {
        List<String> props = predefinedPropsMap.collect { "${it.key}=${it.value}" }
        this.predefinedProps.addAll(props)
    }

    @Deprecated
    void predefinedProps(String predefinedProps) {
        jobManagement.logDeprecationWarning()
        this.predefinedProps.addAll(predefinedProps.split('\n'))
    }

    void matrixSubset(String groovyFilter) {
        this.matrixSubsetFilter = groovyFilter
    }

    void subversionRevision(boolean includeUpstreamParameters = false) {
        this.includeUpstreamParameters = includeUpstreamParameters
        this.subversionRevision = true
    }

    List<Node> getConfigs() {
        List<Node> result = []

        if (booleanParams) {
            result << new NodeBuilder().'hudson.plugins.parameterizedtrigger.BooleanParameters' {
                delegate.configs {
                    booleanParams.each { k, v ->
                        'hudson.plugins.parameterizedtrigger.BooleanParameterConfig' {
                            name(k)
                            value(v)
                        }
                    }
                }
            }
        }

        if (sameNode) {
            result << new NodeBuilder().'hudson.plugins.parameterizedtrigger.NodeParameters'()
        }

        if (currentBuild) {
            result << new NodeBuilder().'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'()
        }

        if (nodeLabelParam) {
            result << new NodeBuilder().
                    'org.jvnet.jenkins.plugins.nodelabelparameter.parameterizedtrigger.NodeLabelBuildParameter' {
                        delegate.name(nodeLabelParam)
                        delegate.nodeLabel(nodeLabel)
                    }
        }

        if (propertiesFile) {
            result << new NodeBuilder().'hudson.plugins.parameterizedtrigger.FileBuildParameters' {
                delegate.propertiesFile(propertiesFile)
                delegate.failTriggerOnMissing(failTriggerOnMissing)
            }
        }

        if (gitRevision) {
            result << new NodeBuilder().'hudson.plugins.git.GitRevisionBuildParameters' {
                delegate.combineQueuedCommits(combineQueuedCommits)
            }
        }

        if (predefinedProps) {
            result << new NodeBuilder().'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters' {
                'properties'(predefinedProps.join('\n'))
            }
        }

        if (matrixSubsetFilter) {
            result << new NodeBuilder().'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters' {
                filter(matrixSubsetFilter)
            }
        }

        if (subversionRevision) {
            result << new NodeBuilder().'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters' {
                delegate.includeUpstreamParameters(includeUpstreamParameters)
            }
        }

        result
    }
}
