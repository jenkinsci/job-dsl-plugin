package javaposse.jobdsl.dsl.helpers

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

        /**
         * ...
         *
         * @param parameterName name of the parameter
         * @param defaultValue "false" if not specified
         * @param description (optional)
         * @return
         */
        def booleanParam(String parameterName, boolean defaultValue = false, String description) {

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
         * ...
         *
         * @param parameterName
         * @param defaultValue (optional)
         * @param description (optional)
         * @return
         */
        def stringParam(String parameterName, String defaultValue, String description) {

        }

        /**
         * ...
         *
         * @param parameterName
         * @param defaultValue (optional)
         * @param description (optional)
         * @return
         */
        def textParam(String parameterName, String defaultValue, String description) {

        }
    }
}

