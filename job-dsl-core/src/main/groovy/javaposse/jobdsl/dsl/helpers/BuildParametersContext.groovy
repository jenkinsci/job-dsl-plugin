package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.parameter.AbstractActiveChoiceContext
import javaposse.jobdsl.dsl.helpers.parameter.ActiveChoiceContext
import javaposse.jobdsl.dsl.helpers.parameter.ActiveChoiceReactiveContext
import javaposse.jobdsl.dsl.helpers.parameter.ActiveChoiceReactiveReferenceContext
import javaposse.jobdsl.dsl.helpers.parameter.CredentialsParameterContext
import javaposse.jobdsl.dsl.helpers.parameter.ListTagsParamContext

import static java.util.UUID.randomUUID
import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNull
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

@ContextType('hudson.model.ParameterDefinition')
class BuildParametersContext extends AbstractExtensibleContext {
    Map<String, Node> buildParameterNodes = [:]

    BuildParametersContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * We expect any parameter definition node to have a <code>name</code> field containing
     * the name of the parameter so we can add it in the map of nodes.
     */
    @Override
    protected void addExtensionNode(Node node) {
        Object nameNodes = node.get('name')
        String name = (nameNodes instanceof NodeList && nameNodes.size() == 1) ? nameNodes.text() : null
        if (name) {
            buildParameterNodes[name] = node
        } else {
            throw new DslScriptException("Can only add nodes with a 'name' field.")
        }
    }

    /**
     * Defines a simple boolean parameter.
     */
    void booleanParam(String parameterName, boolean defaultValue = false, String description = null) {
        simpleParam('hudson.model.BooleanParameterDefinition', parameterName, defaultValue, description)
    }

    /**
     * Defines a parameter that allows to select a Subversion tag from which to create the working copy for the project.
     */
    @RequiresPlugin(id = 'subversion', minimumVersion = '2.1')
    void listTagsParam(String parameterName, String scmUrl, String tagFilterRegex, boolean sortNewestFirst = false,
                       boolean sortZtoA = false, String maxTagsToDisplay = 'all', String defaultValue = null,
                       String description = null) {
        listTagsParam(parameterName, scmUrl) {
            delegate.tagFilterRegex(tagFilterRegex)
            delegate.sortNewestFirst(sortNewestFirst)
            delegate.sortZtoA(sortZtoA)
            delegate.maxTagsToDisplay(maxTagsToDisplay)
            delegate.defaultValue(defaultValue)
            delegate.description(description)
        }
    }

    /**
     * Defines a parameter that allows to select a Subversion tag from which to create the working copy for the project.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'subversion', minimumVersion = '2.1')
    void listTagsParam(String parameterName, String scmUrl, @DslContext(ListTagsParamContext) Closure closure = null) {
        checkParameterName(parameterName)
        checkNotNullOrEmpty(scmUrl, 'scmUrl cannot be null or empty')

        ListTagsParamContext context = new ListTagsParamContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        buildParameterNodes[parameterName] = NodeBuilder.newInstance().
                'hudson.scm.listtagsparameter.ListSubversionTagsParameterDefinition' {
                    name(parameterName)
                    tagsDir(scmUrl)
                    tagsFilter(context.tagFilterRegex ?: '')
                    reverseByDate(context.sortNewestFirst)
                    reverseByName(context.sortZtoA)
                    maxTags(context.maxTagsToDisplay ?: '')
                    defaultValue(context.defaultValue ?: '')
                    description(context.description ?: '')
                    credentialsId(context.credentialsId ?: '')
                    uuid(randomUUID())
                }
    }

    /**
     * Defines a simple string parameter, which can be selected from a list.
     */
    void choiceParam(String parameterName, List<String> options, String description = null) {
        checkParameterName(parameterName)
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

    /**
     * Defines a parameters that accepts a file submission.
     */
    void fileParam(String fileLocation, String description = null) {
        checkParameterName(fileLocation)

        Node definitionNode = new Node(null, 'hudson.model.FileParameterDefinition')
        definitionNode.appendNode('name', fileLocation)
        if (description != null) {
            definitionNode.appendNode('description', description)
        }

        buildParameterNodes[fileLocation] = definitionNode
    }

    /**
     * Defines a run parameter, where users can pick a single run of a certain project.
     */
    void runParam(String parameterName, String jobToRun, String description = null, String filter = null) {
        checkParameterName(parameterName)
        checkNotNullOrEmpty(jobToRun, 'jobToRun cannot be null or empty')

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
     * Defines a parameter to select a label used to identify/restrict the node where this job should run on.
     *
     * @since 1.30
     */
    @RequiresPlugin(id = 'nodelabelparameter')
    void labelParam(String parameterName, @DslContext(LabelParamContext) Closure labelParamClosure = null) {
        checkParameterName(parameterName)

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
     * Defines a parameter to select a list of nodes where the job could potentially be executed on.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'nodelabelparameter')
    void nodeParam(String parameterName, @DslContext(NodeParamContext) Closure nodeParamClosure = null) {
        checkParameterName(parameterName)

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
     * Defines a parameter that allows select a Git tag (or revision number).
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'git-parameter', minimumVersion = '0.4.0')
    void gitParam(String parameterName, @DslContext(GitParamContext) Closure closure = null) {
        checkParameterName(parameterName)

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

    /**
     * Defines a simple text parameter, where users can enter a string value.
     */
    void stringParam(String parameterName, String defaultValue = null, String description = null) {
        simpleParam('hudson.model.StringParameterDefinition', parameterName, defaultValue, description)
    }

    /**
     * Defines a simple text parameter, where users can enter a multi-line string value.
     */
    void textParam(String parameterName, String defaultValue = null, String description = null) {
        simpleParam('hudson.model.TextParameterDefinition', parameterName, defaultValue, description)
    }

    private simpleParam(String type, String parameterName, Object defaultValue = null, String description = null) {
        checkParameterName(parameterName)

        Node definitionNode = new Node(null, type)
        definitionNode.appendNode('name', parameterName)
        definitionNode.appendNode('defaultValue', defaultValue)
        if (description != null) {
            definitionNode.appendNode('description', description)
        }

        buildParameterNodes[parameterName] = definitionNode
    }

    /**
     * Defines a parameter that dynamically generates a list of value options for a build parameter using a Groovy
     * script or a script from the Scriptler catalog.
     *
     * @since 1.36
     */
    @RequiresPlugin(id = 'uno-choice', minimumVersion = '1.2')
    @Deprecated
    void activeChoiceParam(String parameterName, @DslContext(ActiveChoiceContext) Closure closure) {
        ActiveChoiceContext context = new ActiveChoiceContext()
        ContextHelper.executeInContext(closure, context)

        buildParameterNodes[parameterName] = createActiveChoiceNode(
                'org.biouno.unochoice.ChoiceParameter', parameterName, context
        )
    }

    /**
     * Defines a parameter that dynamically generates a list of value options for a build parameter using a Groovy
     * script or a script from the Scriptler catalog and that dynamically updates when the value of other job UI
     * controls change.
     *
     * @since 1.38
     */
    @RequiresPlugin(id = 'uno-choice', minimumVersion = '1.2')
    @Deprecated
    void activeChoiceReactiveParam(String parameterName,
                                   @DslContext(ActiveChoiceReactiveContext) Closure closure = null) {
        ActiveChoiceReactiveContext context = new ActiveChoiceReactiveContext()
        ContextHelper.executeInContext(closure, context)

        Node node = createActiveChoiceNode('org.biouno.unochoice.CascadeChoiceParameter', parameterName, context)
        node.appendNode('referencedParameters', context.referencedParameters.join(', '))
        node.appendNode('parameters', [class: 'linked-hash-map'])

        buildParameterNodes[parameterName] = node
    }

    /**
     * Defines a parameter that dynamically generates a list of value options for a build parameter using a Groovy
     * script or a script from the Scriptler catalog and that dynamically updates when the value of other job UI
     * controls change.
     *
     * @since 1.38
     */
    @RequiresPlugin(id = 'uno-choice', minimumVersion = '1.2')
    @Deprecated
    void activeChoiceReactiveReferenceParam(String parameterName,
                                            @DslContext(ActiveChoiceReactiveReferenceContext) Closure closure = null) {
        ActiveChoiceReactiveReferenceContext context = new ActiveChoiceReactiveReferenceContext()
        ContextHelper.executeInContext(closure, context)

        Node node = createAbstractActiveChoiceNode(
                'org.biouno.unochoice.DynamicReferenceParameter', parameterName, context
        )
        node.appendNode('referencedParameters', context.referencedParameters.join(', '))
        node.appendNode('choiceType', "ET_${context.choiceType}")
        node.appendNode('omitValueField', context.omitValueField)
        node.appendNode('parameters', [class: 'linked-hash-map'])

        buildParameterNodes[parameterName] = node
    }

    /**
     * Defines a credentials parameter. The string value will be the UUID of the credential.
     *
     * @since 1.38
     */
    @RequiresPlugin(id = 'credentials', minimumVersion = '1.22')
    void credentialsParam(String paramName, @DslContext(CredentialsParameterContext) Closure closure = null) {
        checkParameterName(paramName)

        CredentialsParameterContext context = new CredentialsParameterContext()
        ContextHelper.executeInContext(closure, context)

        Node node = new NodeBuilder().'com.cloudbees.plugins.credentials.CredentialsParameterDefinition' {
            name(paramName)
            description(context.description ?: '')
            defaultValue(context.defaultValue ?: '')
            credentialType(context.type)
            required(context.required)
        }
        buildParameterNodes[paramName] = node
    }

    /**
     * Defines a parameter that references a global variable.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'global-variable-string-parameter', minimumVersion = '1.2')
    void globalVariableParam(String parameterName, String defaultValue = null, String description = null) {
        simpleParam('hudson.plugins.global__variable__string__parameter.GlobalVariableStringParameterDefinition',
                parameterName, defaultValue, description)
    }

    /**
     * Defines a parameter that allows to choose which matrix combinations to run.
     *
     * @since 1.40
     */
    @RequiresPlugin(id = 'matrix-combinations-parameter', minimumVersion = '1.0.9')
    void matrixCombinationsParam(String parameterName, String defaultValue = null, String description = null) {
        checkParameterName(parameterName)

        buildParameterNodes[parameterName] = new NodeBuilder().
                'hudson.plugins.matrix__configuration__parameter.MatrixCombinationsParameterDefinition' {
                    name(parameterName)
                    delegate.description(description ?: '')
                    if (defaultValue) {
                        defaultCombinationFilter(defaultValue)
                    }
                }
    }

    /**
     * Defines a parameter that allows to take in a user's password.
     *
     * @since 1.44
     */
    @RequiresPlugin(id = 'mask-passwords', minimumVersion = '2.6')
    void nonStoredPasswordParam(String parameterName, String description = null) {
        checkParameterName(parameterName)

        buildParameterNodes[parameterName] = new NodeBuilder().
                'com.michelin.cio.hudson.plugins.passwordparam.PasswordParameterDefinition' {
                    name(parameterName)
                    delegate.description(description ?: '')
                }
    }

    private checkParameterName(String name) {
        checkNotNullOrEmpty(name, 'parameterName cannot be null')
        checkArgument(!buildParameterNodes.containsKey(name), "parameter ${name} already defined")
    }

    private Node createActiveChoiceNode(String type, String paramName, ActiveChoiceContext context) {
        Node node = createAbstractActiveChoiceNode(type, paramName, context)
        node.appendNode('filterable', context.filterable)
        node.appendNode('choiceType', "PT_${context.choiceType}")
        node
    }

    private Node createAbstractActiveChoiceNode(String type, String paramName, AbstractActiveChoiceContext context) {
        checkParameterName(paramName)

        Node node = new NodeBuilder()."${type}" {
            name(paramName)
            description(context.description ?: '')
            randomName("choice-parameter-${System.nanoTime()}")
            visibleItemCount(1)
        }
        if (context.script) {
            node.children().add(context.script)
        }
        node
    }
}
