package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions

import javaposse.jobdsl.dsl.WithXmlAction

class BuildParametersContextHelper extends AbstractContextHelper<BuildParametersContext> {

    BuildParametersContextHelper(List<WithXmlAction> withXmlActions) {
        super(withXmlActions)
    }

    def parameters(Closure closure) {
        execute(closure, new BuildParametersContext())
    }

    Closure generateWithXmlClosure(BuildParametersContext context) {
        return { Node project ->
            def parameterDefinitions = project / 'properties' / 'hudson.model.ParametersDefinitionProperty' / parameterDefinitions
            context.buildParameterNodes.each {
                parameterDefinitions << it
            }
        }
    }

    static class BuildParametersContext implements Context {

        List<Node> buildParameterNodes = []

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
         * @param options{choiceA_Default, choiceB, choiceC}
         * @param description (optional)
         * @return
         */
        def choiceParam(String parameterName, List<String> options, String description = null) {
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

            buildParameterNodes << definitionNode
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

        private def simpleParam(String type, String parameterName, Object defaultValue = null, String description = null) {
            Preconditions.checkNotNull(parameterName, 'parameterName cannot be null')
            Preconditions.checkArgument(parameterName.length() > 0)

            Node definitionNode = new Node(null, type)
            definitionNode.appendNode('name', parameterName)
            definitionNode.appendNode('defaultValue', defaultValue)
            if (description != null) {
                definitionNode.appendNode('description', description)
            }

            buildParameterNodes << definitionNode
        }
    }
}