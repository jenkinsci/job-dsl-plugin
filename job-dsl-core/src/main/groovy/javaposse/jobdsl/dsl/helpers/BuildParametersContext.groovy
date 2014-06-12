package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions

class BuildParametersContext implements Context {

    Map<String, Node> buildParameterNodes = [:]

    /**
     * <project>
     *     <properties>
     *         <hudson.model.ParametersDefinitionProperty>
     *             <parameterDefinitions>
     *                 <hudson.model.BooleanParameterDefinition>
     *                     <name>booleanValue</name>
     *                     <description>ths description of the boolean value</description>
     *                     <defaultValue>true</defaultValue>
     *                 </hudson.model.BooleanParameterDefinition>
     *
     * @param parameterName name of the parameter
     * @param defaultValue "false" if not specified
     * @param description (optional)
     * @return
     */
    def booleanParam(String parameterName, boolean defaultValue = false, String description = null) {
        simpleParam('hudson.model.BooleanParameterDefinition', parameterName, defaultValue, description)
    }

    /**
     * <project>
     *     <properties>
     *         <hudson.model.ParametersDefinitionProperty>
     *             <parameterDefinitions>
     *                 <hudson.scm.listtagsparameter.ListSubversionTagsParameterDefinition>
     *                     <name>listSvnTagsValue</name>
     *                     <description>Select a Subversion entry</description>
     *                     <tagsDir>http://kenai.com/svn</tagsDir>
     *                     <tagsFilter>theTagFilterRegex</tagsFilter>
     *                     <reverseByDate>true</reverseByDate>
     *                     <reverseByName>true</reverseByName>
     *                     <defaultValue>theDefaultValue</defaultValue>
     *                     <maxTags>maxTagsToDisplayValue</maxTags>
     *                     <uuid>e434beb2-10dd-4444-a054-44fec8c86ff8</uuid>
     *                 </hudson.scm.listtagsparameter.ListSubversionTagsParameterDefinition>
     *
     * @param parameterName
     * @param scmUrl
     * @param tagFilterRegex
     * @param sortNewestFirst (default = "false")
     * @param sortZtoA (default = "false")
     * @param maxTagsToDisplay (optional - "all" if not specified)
     * @param defaultValue (optional)
     * @param description (optional)
     * @return
     */
    def listTagsParam(String parameterName, String scmUrl, String tagFilterRegex, boolean sortNewestFirst = false, boolean sortZtoA = false, String maxTagsToDisplay = 'all', String defaultValue = null, String description = null) {
        Preconditions.checkArgument(!buildParameterNodes.containsKey(parameterName), 'parameter $parameterName already defined')
        Preconditions.checkNotNull(parameterName, 'parameterName cannot be null')
        Preconditions.checkArgument(parameterName.length() > 0)
        Preconditions.checkNotNull(scmUrl, 'scmUrl cannot be null')
        Preconditions.checkArgument(scmUrl.length() > 0)
        Preconditions.checkNotNull(tagFilterRegex, 'tagFilterRegex cannot be null')
        Preconditions.checkArgument(tagFilterRegex.length() > 0)

        Node definitionNode = new Node(null, 'hudson.scm.listtagsparameter.ListSubversionTagsParameterDefinition')
        definitionNode.appendNode('name', parameterName)
        definitionNode.appendNode('tagsDir', scmUrl)
        definitionNode.appendNode('tagsFilter', tagFilterRegex)
        definitionNode.appendNode('reverseByDate', sortNewestFirst)
        definitionNode.appendNode('reverseByName', sortZtoA)
        definitionNode.appendNode('maxTags', maxTagsToDisplay)
        if (defaultValue != null) {
            definitionNode.appendNode('defaultValue', defaultValue)
        }
        if (description != null) {
            definitionNode.appendNode('description', description)
        }
        definitionNode.appendNode('uuid', UUID.randomUUID())

        buildParameterNodes[parameterName] = definitionNode
    }

    /**
     * <project>
     *   <properties>
     *     <hudson.model.ParametersDefinitionProperty>
     *       <parameterDefinitions>
     *         <hudson.model.ChoiceParameterDefinition>
     *           <name>choice</name>
     *           <description>test</description>
     *           <choices class="java.util.Arrays$ArrayList">
     *             <a class="string-array">
     *               <string>one</string>
     *               <string>two</string>
     *               <string>three</string>
     *             </a>
     *           </choices>
     *         </hudson.model.ChoiceParameterDefinition>
     *
     * @param parameterName
     * @param options {choiceA_Default, choiceB, choiceC}
     * @param description (optional)
     * @return
     */
    def choiceParam(String parameterName, List<String> options, String description = null) {
        Preconditions.checkArgument(!buildParameterNodes.containsKey(parameterName), 'parameter $parameterName already defined')
        Preconditions.checkNotNull(parameterName, 'parameterName cannot be null')
        Preconditions.checkArgument(parameterName.length() > 0)
        Preconditions.checkNotNull(options, 'options cannot be null')
        Preconditions.checkArgument(options.size() > 0, 'at least one option must be specified')

        def definitionNode = NodeBuilder.newInstance().'hudson.model.ChoiceParameterDefinition' {
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
     * <project>
     *     <properties>
     *         <hudson.model.ParametersDefinitionProperty>
     *             <parameterDefinitions>
     *                 <hudson.model.FileParameterDefinition>
     *                     <name>test/upload.zip</name>
     *                     <description>lalala</description>
     *                 </hudson.model.FileParameterDefinition>
     *
     * @param parameterName
     * @param fileLocation_relativeToTheWorkspace
     * @param description (optional)
     * @return
     */
    def fileParam(String fileLocation, String description = null) {
        Preconditions.checkArgument(!buildParameterNodes.containsKey(fileLocation), 'parameter $fileLocation already defined')
        Preconditions.checkNotNull(fileLocation, 'fileLocation cannot be null')
        Preconditions.checkArgument(fileLocation.length() > 0)

        Node definitionNode = new Node(null, 'hudson.model.FileParameterDefinition')
        definitionNode.appendNode('name', fileLocation)
        if (description != null) {
            definitionNode.appendNode('description', description)
        }

        buildParameterNodes[fileLocation] = definitionNode
    }

    /**
     * <project>
     *     <properties>
     *         <hudson.model.ParametersDefinitionProperty>
     *             <hudson.model.RunParameterDefinition>
     *                 <name>runValue</name>
     *                 <projectName>2</projectName>
     *                 <description>the description of the run value</description>
     *                 <filter>SUCCESSFUL</filter>
     *         </hudson.model.RunParameterDefinition>
     *
     * @param parameterName
     * @param jobToRun
     * @param description (optional)
     * @param filter (optional, one of "ALL", "COMPLETED", "SUCCESSFUL" or "STABLE")
     * @return
     */
    def runParam(String parameterName, String jobToRun, String description = null, String filter = null) {
        Preconditions.checkArgument(!buildParameterNodes.containsKey(parameterName), 'parameter $parameterName already defined')
        Preconditions.checkNotNull(parameterName, 'parameterName cannot be null')
        Preconditions.checkArgument(parameterName.length() > 0)
        Preconditions.checkNotNull(jobToRun, 'jobToRun cannot be null')
        Preconditions.checkArgument(jobToRun.length() > 0)

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
     * <project>
     *     <properties>
     *         <hudson.model.ParametersDefinitionProperty>
     *             <parameterDefinitions>
     *                 <hudson.model.StringParameterDefinition>
     *                     <name>stringValue</name>
     *                     <description>the description of the string value</description>
     *                     <defaultValue>theDefaultStringValue</defaultValue>
     *                 </hudson.model.StringParameterDefinition>
     *
     * @param parameterName
     * @param defaultValue (optional)
     * @param description (optional)
     * @return
     */
    def stringParam(String parameterName, String defaultValue = null, String description = null) {
        simpleParam('hudson.model.StringParameterDefinition', parameterName, defaultValue, description)
    }

    /**
     * <project>
     *     <properties>
     *         <hudson.model.ParametersDefinitionProperty>
     *             <parameterDefinitions>
     *                 <hudson.model.TextParameterDefinition>
     *                     <name>textValue</name>
     *                     <description>the description of the text value</description>
     *                     <defaultValue>defaultTextValue</defaultValue>
     *                 </hudson.model.TextParameterDefinition>
     *
     * @param parameterName
     * @param defaultValue (optional)
     * @param description (optional)
     * @return
     */
    def textParam(String parameterName, String defaultValue = null, String description = null) {
        simpleParam('hudson.model.TextParameterDefinition', parameterName, defaultValue, description)
    }

    private simpleParam(String type, String parameterName, Object defaultValue = null, String description = null) {
        Preconditions.checkArgument(!buildParameterNodes.containsKey(parameterName), 'parameter $parameterName already defined')
        Preconditions.checkNotNull(parameterName, 'parameterName cannot be null')
        Preconditions.checkArgument(parameterName.length() > 0)

        Node definitionNode = new Node(null, type)
        definitionNode.appendNode('name', parameterName)
        definitionNode.appendNode('defaultValue', defaultValue)
        if (description != null) {
            definitionNode.appendNode('description', description)
        }

        buildParameterNodes[parameterName] = definitionNode
    }
}
