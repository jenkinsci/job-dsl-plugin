package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.AbstractExtensibleContext

/**
 * @since 1.38
 */
@ContextType('hudson.plugins.parameterizedtrigger.AbstractBuildParameters')
class DownstreamTriggerParameterContext extends AbstractExtensibleContext {
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
    List<Node> extensionNodes = []

    DownstreamTriggerParameterContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        extensionNodes << node
    }

    /**
     * Adds a boolean parameter. Can be called multiple times to add more parameters.
     */
    void booleanParam(String name, boolean defaultValue = false) {
        this.booleanParams[name] = defaultValue
    }

    /**
     * Uses the same node for the triggered builds that was used for this build.
     */
    void sameNode() {
        this.sameNode = true
    }

    /**
     * Copies parameters from the current build, except for file parameters.
     */
    void currentBuild() {
        this.currentBuild = true
    }

    /**
     * Defines where the target job should be executed, the value must match either a label or a node name.
     */
    @RequiresPlugin(id = 'nodelabelparameter')
    void nodeLabel(String paramName, String nodeLabel) {
        this.nodeLabelParam = paramName
        this.nodeLabel = nodeLabel
    }

    /**
     * Reads parameters from a properties file.
     */
    void propertiesFile(String fileName, boolean failTriggerOnMissing = false) {
        this.propertiesFile = fileName
        this.failTriggerOnMissing = failTriggerOnMissing
    }

    /**
     * Passes the Git commit that was used in this build to the downstream builds.
     */
    @RequiresPlugin(id = 'git', minimumVersion = '2.5.3')
    void gitRevision(boolean combineQueuedCommits = false) {
        this.gitRevision = true
        this.combineQueuedCommits = combineQueuedCommits
    }

    /**
     * Adds a parameter. Can be called multiple times to add more parameters.
     */
    void predefinedProp(String key, String value) {
        this.predefinedProps << "${key}=${value}"
    }

    /**
     * Adds parameters. Can be called multiple times to add more parameters.
     */
    void predefinedProps(Map<String, String> predefinedPropsMap) {
        List<String> props = predefinedPropsMap.collect { "${it.key}=${it.value}" }
        this.predefinedProps.addAll(props)
    }

    /**
     * Specifies a Groovy filter expression that restricts the subset of combinations that the downstream project will
     * run.
     */
    void matrixSubset(String groovyFilter) {
        this.matrixSubsetFilter = groovyFilter
    }

    /**
     * Passes the Subversion revisions that were used in this build to the downstream builds.
     */
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
                delegate.properties(predefinedProps.join('\n'))
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

        result.addAll(extensionNodes)

        result
    }
}
