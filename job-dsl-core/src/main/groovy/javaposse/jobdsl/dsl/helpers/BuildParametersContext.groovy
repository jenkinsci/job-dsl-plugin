package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkNotNull
import static java.util.UUID.randomUUID

class BuildParametersContext extends AbstractContext {
    Map<String, Node> buildParameterNodes = [:]

    BuildParametersContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void booleanParam(String parameterName, boolean defaultValue = false, String description = null) {
        simpleParam('hudson.model.BooleanParameterDefinition', parameterName, defaultValue, description)
    }

    @RequiresPlugin(id = 'subversion')
    void listTagsParam(String parameterName, String scmUrl, String tagFilterRegex, boolean sortNewestFirst = false,
                      boolean sortZtoA = false, String maxTagsToDisplay = 'all', String defaultValue = null,
                      String description = null) {
        checkArgument(!buildParameterNodes.containsKey(parameterName), 'parameter $parameterName already defined')
        checkNotNull(parameterName, 'parameterName cannot be null')
        checkArgument(parameterName.length() > 0)
        checkNotNull(scmUrl, 'scmUrl cannot be null')
        checkArgument(scmUrl.length() > 0)

        Node definitionNode = new Node(null, 'hudson.scm.listtagsparameter.ListSubversionTagsParameterDefinition')
        definitionNode.appendNode('name', parameterName)
        definitionNode.appendNode('tagsDir', scmUrl)
        definitionNode.appendNode('tagsFilter', tagFilterRegex ?: '')
        definitionNode.appendNode('reverseByDate', sortNewestFirst)
        definitionNode.appendNode('reverseByName', sortZtoA)
        definitionNode.appendNode('maxTags', maxTagsToDisplay)
        if (defaultValue != null) {
            definitionNode.appendNode('defaultValue', defaultValue)
        }
        if (description != null) {
            definitionNode.appendNode('description', description)
        }
        definitionNode.appendNode('uuid', randomUUID())

        buildParameterNodes[parameterName] = definitionNode
    }

    void choiceParam(String parameterName, List<String> options, String description = null) {
        checkArgument(!buildParameterNodes.containsKey(parameterName), 'parameter $parameterName already defined')
        checkNotNull(parameterName, 'parameterName cannot be null')
        checkArgument(parameterName.length() > 0)
        checkNotNull(options, 'options cannot be null')
        checkArgument(options.size() > 0, 'at least one option must be specified')

        Node definitionNode = NodeBuilder.newInstance().'hudson.model.ChoiceParameterDefinition' {
            choices(class: 'java.util.Arrays\$ArrayList') {
                a(class: 'string-array') {
                    options.each {
                        string(it)
                    }
                }
            }
        }
        definitionNode.appendNode('name', parameterName)
        if (description != null) {
            definitionNode.appendNode('description', description)
        }

        buildParameterNodes[parameterName] = definitionNode
    }

    void fileParam(String fileLocation, String description = null) {
        checkArgument(!buildParameterNodes.containsKey(fileLocation), 'parameter $fileLocation already defined')
        checkNotNull(fileLocation, 'fileLocation cannot be null')
        checkArgument(fileLocation.length() > 0)

        Node definitionNode = new Node(null, 'hudson.model.FileParameterDefinition')
        definitionNode.appendNode('name', fileLocation)
        if (description != null) {
            definitionNode.appendNode('description', description)
        }

        buildParameterNodes[fileLocation] = definitionNode
    }

    void runParam(String parameterName, String jobToRun, String description = null, String filter = null) {
        checkArgument(!buildParameterNodes.containsKey(parameterName), 'parameter $parameterName already defined')
        checkNotNull(parameterName, 'parameterName cannot be null')
        checkArgument(parameterName.length() > 0)
        checkNotNull(jobToRun, 'jobToRun cannot be null')
        checkArgument(jobToRun.length() > 0)

        Node definitionNode = new Node(null, 'hudson.model.RunParameterDefinition')
        definitionNode.appendNode('name', parameterName)
        definitionNode.appendNode('projectName', jobToRun)
        if (description != null) {
            definitionNode.appendNode('description', description)
        }
        if (filter != null) {
            definitionNode.appendNode('filter', filter)
        }

        buildParameterNodes[parameterName] = definitionNode
    }

    /**
     * @since 1.30
     */
    @RequiresPlugin(id = 'nodelabelparameter')
    void labelParam(String parameterName, @DslContext(LabelParamContext) Closure labelParamClosure = null) {
        checkArgument(!buildParameterNodes.containsKey(parameterName), 'parameter $parameterName already defined')
        checkNotNull(parameterName, 'parameterName cannot be null')
        checkArgument(parameterName.length() > 0)

        LabelParamContext context = new LabelParamContext()
        ContextHelper.executeInContext(labelParamClosure, context)

        buildParameterNodes[parameterName] = NodeBuilder.newInstance().
                'org.jvnet.jenkins.plugins.nodelabelparameter.LabelParameterDefinition' {
                    name(parameterName)
                    defaultValue(context.defaultValue ?: '')
                    description(context.description ?: '')
                    allNodesMatchingLabel(context.allNodes)
                    triggerIfResult(context.trigger)
                    nodeEligibility(class: "org.jvnet.jenkins.plugins.nodelabelparameter.node.${context.eligibility}")
                }
    }

    /**
     * @since 1.26
     */
    @RequiresPlugin(id = 'nodelabelparameter')
    void nodeParam(String parameterName, @DslContext(NodeParamContext) Closure nodeParamClosure = null) {
        checkArgument(!buildParameterNodes.containsKey(parameterName), 'parameter $parameterName already defined')
        checkNotNull(parameterName, 'parameterName cannot be null')
        checkArgument(parameterName.length() > 0)

        NodeParamContext context = new NodeParamContext()
        ContextHelper.executeInContext(nodeParamClosure, context)

        buildParameterNodes[parameterName] = NodeBuilder.newInstance().
                'org.jvnet.jenkins.plugins.nodelabelparameter.NodeParameterDefinition' {
                    name(parameterName)
                    description(context.description)
                    allowedSlaves {
                        context.allowedNodes.each { string(it) }
                    }
                    defaultSlaves {
                        context.defaultNodes.each { string(it) }
                    }
                    triggerIfResult(context.trigger)
                    allowMultiNodeSelection(context.allowMultiNodeSelection)
                    triggerConcurrentBuilds(context.triggerConcurrentBuilds)
                    ignoreOfflineNodes(false)
                    nodeEligibility(class: "org.jvnet.jenkins.plugins.nodelabelparameter.node.${context.eligibility}")
                }
    }

    /**
     * @since 1.31
     */
    @RequiresPlugin(id = 'git-parameter', minimumVersion = '0.4.0')
    void gitParam(String parameterName, @DslContext(GitParamContext) Closure closure = null) {
        checkArgument(!buildParameterNodes.containsKey(parameterName), 'parameter $parameterName already defined')
        checkNotNull(parameterName, 'parameterName cannot be null')
        checkArgument(parameterName.length() > 0)

        GitParamContext context = new GitParamContext()
        ContextHelper.executeInContext(closure, context)

        buildParameterNodes[parameterName] = NodeBuilder.newInstance().
                'net.uaznia.lukanus.hudson.plugins.gitparameter.GitParameterDefinition' {
                    name(parameterName)
                    description(context.description ?: '')
                    uuid(randomUUID().toString())
                    type("PT_$context.type")
                    branch(context.branch ?: '')
                    tagFilter(context.tagFilter ?: '')
                    sortMode(context.sortMode)
                    defaultValue(context.defaultValue ?: '')
                }
    }

    void stringParam(String parameterName, String defaultValue = null, String description = null) {
        simpleParam('hudson.model.StringParameterDefinition', parameterName, defaultValue, description)
    }

    void textParam(String parameterName, String defaultValue = null, String description = null) {
        simpleParam('hudson.model.TextParameterDefinition', parameterName, defaultValue, description)
    }

    private simpleParam(String type, String parameterName, Object defaultValue = null, String description = null) {
        checkArgument(!buildParameterNodes.containsKey(parameterName), 'parameter $parameterName already defined')
        checkNotNull(parameterName, 'parameterName cannot be null')
        checkArgument(parameterName.length() > 0)

        Node definitionNode = new Node(null, type)
        definitionNode.appendNode('name', parameterName)
        definitionNode.appendNode('defaultValue', defaultValue)
        if (description != null) {
            definitionNode.appendNode('description', description)
        }

        buildParameterNodes[parameterName] = definitionNode
    }
}
