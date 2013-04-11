package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions

import javaposse.jobdsl.dsl.WithXmlAction

class BuildParametersContextHelper extends AbstractContextHelper<BuildParametersContext> {

    BuildParametersContextHelper(List<WithXmlAction> withXmlActions) {
        super(withXmlActions)
    }

    Closure generateWithXmlClosure(BuildParametersContext context) {
        return { Node project ->
            // TODO This will create it if it doesn't exist, seems like we wouldn't need to do this, but dealing with NodeList is a pain
            def parameters = project/properties/hudson.model.ParametersDefinitionProperty/parameterDefinitions

            project << context.buildParametersNodes
        }
    }

    static class BuildParametersContext implements Context {

        List<Node> buildParameterNodes = []

        BuildParametersContext() {}

        BuildParametersContext(List<Node> buildParameterNodes) {
            this.buildParameterNodes = buildParameterNodes
        }

        def getListOfBuildParameterNodes() {
            def nodeBuilder = new NodeBuilder()

            return nodeBuilder.properties {
                'hudson.model.ParametersPropertyDefinition' {
                    parameterDefinitions { }
                }
            }
        }

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
        def booleanParam(String parameterName, boolean defaultValue = false, Closure configure = null) {
            booleanParam(parameterName, defaultValue, '')
        }
        def booleanParam(String parameterName, boolean defaultValue = false, String description, Closure configure = null) {
            Preconditions.checkNotNull(parameterName, 'parameterName cannot be null')
            Preconditions.checkState(parameterName.length() > 0)

            Node buildParametersNode = getListOfBuildParameterNodes()

            buildParametersNode.appendNode('hudson.model.ParametersPropertyDefinition').appendNode('parameterDefinitions').appendNode('hudson.model.BooleanParameterDefinition')
            buildParametersNode.appendNode('hudson.model.ParametersPropertyDefinition').appendNode('parameterDefinitions').appendNode('hudson.model.BooleanParameterDefinition').appendNode('name', parameterName)
            buildParametersNode.appendNode('hudson.model.ParametersPropertyDefinition').appendNode('parameterDefinitions').appendNode('hudson.model.BooleanParameterDefinition').appendNode('defaultValue', defaultValue)
            buildParametersNode.appendNode('hudson.model.ParametersPropertyDefinition').appendNode('parameterDefinitions').appendNode('hudson.model.BooleanParameterDefinition').appendNode('description', description)

            // Apply Context
            if (configure) {
                WithXmlAction action = new WithXmlAction(configure)
                action.execute(buildParametersNode)
            }
            buildParameterNodes << buildParametersNode
        }

        /**
         * ...
         *
         * @param parameterName
         * @param scmUrl
         * @param tagFilterRegex
         * @param sortNewestFirst (default = "false")
         * @param sortZtoA (default = "false")
         * @param defaultValue (optional)
         * @param maxTagsToDisplay (optional - "all" if not specified)
         * @param description (optional)
         * @return
         */
        def listTagsParam(String parameterName, String scmUrl, String tagFilterRegex, boolean sortNewestFirst = false, boolean sortZtoA = false, String defaultValue, String maxTagsToDisplay = "all", String description) {

        }

        /**
         * ...
         *
         * @param parameterName
         * @param options {choiceA_Default, choiceB, choiceC}
         * @param description (optional)
         * @return
         */
        def choiceParam (String parameterName, List<String> options , String description) {

        }

        /**
         * ...
         *
         * @param parameterName
         * @param fileLocation_relativeToTheWorkspace
         * @param description (optional)
         * @return
         */
        def fileParam (String parameterName, String fileLocation_relativeToTheWorkspace, String description) {

        }

        /**
         * WARNING - the current implementation stores the password in the DSL script in CLEAR TEXT
         *
         * @param parameterName
         * @param defaultValue
         * @param description (optional)
         * @return
         */
        def passwordParam (String parameterName, String defaultValue, String description) {

        }

        /**
         * ...
         *
         * @param parameterName
         * @param jobToRun
         * @param description (optional)
         * @return
         */
        def runParam(String parameterName, String jobToRun, String description) {

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
        def stringParam(String parameterName, Closure configure = null) {
            stringParam(parameterName, '', '')
        }
        def stringParam(String parameterName, String defaultValue, Closure configure = null) {
            stringParam(parameterName, defaultValue, '')
        }
        def stringParam(String parameterName, String defaultValue, String description, Closure configure = null) {
            Preconditions.checkNotNull(parameterName, 'parameterName cannot be null')
            Preconditions.checkState(parameterName.length() > 0)

            Node buildParametersNode = getListOfBuildParameterNodes()

            buildParametersNode.appendNode('hudson.model.ParametersPropertyDefinition').appendNode('parameterDefinitions').appendNode('hudson.model.StringParameterDefinition')
            buildParametersNode.appendNode('hudson.model.ParametersPropertyDefinition').appendNode('parameterDefinitions').appendNode('hudson.model.StringParameterDefinition').appendNode('name', parameterName)
            buildParametersNode.appendNode('hudson.model.ParametersPropertyDefinition').appendNode('parameterDefinitions').appendNode('hudson.model.StringParameterDefinition').appendNode('defaultValue', defaultValue)
            buildParametersNode.appendNode('hudson.model.ParametersPropertyDefinition').appendNode('parameterDefinitions').appendNode('hudson.model.StringParameterDefinition').appendNode('description', description)

            // Apply Context
            if (configure) {
                WithXmlAction action = new WithXmlAction(configure)
                action.execute(buildParametersNode)
            }
            buildParameterNodes << buildParametersNode
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
        def textParam(String parameterName, Closure configure = null) {
            textParam(parameterName, '', '')
        }
        def textParam(String parameterName, String defaultValue, Closure configure = null) {
            textParam(parameterName, defaultValue, '')
        }
        def textParam(String parameterName, String defaultValue, String description, Closure configure = null) {
            Preconditions.checkNotNull(parameterName, 'parameterName cannot be null')
            Preconditions.checkState(parameterName.length() > 0)

            Node buildParametersNode = getListOfBuildParameterNodes()

            buildParametersNode.appendNode('hudson.model.ParametersPropertyDefinition').appendNode('parameterDefinitions').appendNode('hudson.model.TextParameterDefinition')
            buildParametersNode.appendNode('hudson.model.ParametersPropertyDefinition').appendNode('parameterDefinitions').appendNode('hudson.model.TextParameterDefinition').appendNode('name', parameterName)
            buildParametersNode.appendNode('hudson.model.ParametersPropertyDefinition').appendNode('parameterDefinitions').appendNode('hudson.model.TextParameterDefinition').appendNode('defaultValue', defaultValue)
            buildParametersNode.appendNode('hudson.model.ParametersPropertyDefinition').appendNode('parameterDefinitions').appendNode('hudson.model.TextParameterDefinition').appendNode('description', description)

            // Apply Context
            if (configure) {
                WithXmlAction action = new WithXmlAction(configure)
                action.execute(buildParametersNode)
            }
            buildParameterNodes << buildParametersNode
        }
    }
}