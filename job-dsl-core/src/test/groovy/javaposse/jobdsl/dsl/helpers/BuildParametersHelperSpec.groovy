package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import javaposse.jobdsl.dsl.helpers.BuildParametersContextHelper.BuildParametersContext
import spock.lang.Specification

public class BuildParametersHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    BuildParametersContextHelper helper = new BuildParametersContextHelper(mockActions)
    BuildParametersContext context = new BuildParametersContext()

    def 'base booleanParam usage'() {
        when:
        context.booleanParam("myParameterName", true, "myBooleanParameterDescription")

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.'hudson.model.ParametersPropertyDefinition'.parameterDefinitions.'hudson.model.BooleanParameterDefinition'[0].name.text() == "myParameterName"
        context.buildParameterNodes.'hudson.model.ParametersPropertyDefinition'.parameterDefinitions.'hudson.model.BooleanParameterDefinition'[0].defaultValue.text() == "true"
        context.buildParameterNodes.'hudson.model.ParametersPropertyDefinition'.parameterDefinitions.'hudson.model.BooleanParameterDefinition'[0].description.text() == "myBooleanParameterDescription"
    }

    def 'simplified booleanParam usage'() {
        when:
        context.booleanParam("myParameterName", true)

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.'hudson.model.ParametersPropertyDefinition'.parameterDefinitions.'hudson.model.BooleanParameterDefinition'[0].name.text() == 'myParameterName'
        context.buildParameterNodes.'hudson.model.ParametersPropertyDefinition'.parameterDefinitions.'hudson.model.BooleanParameterDefinition'[0].defaultValue.text() == 'true'
        context.buildParameterNodes.'hudson.model.ParametersPropertyDefinition'.parameterDefinitions.'hudson.model.BooleanParameterDefinition'[0].description.text() == ''
    }

    def 'simplest booleanParam usage'() {
        when:
        context.booleanParam("myParameterName")

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.'hudson.model.ParametersPropertyDefinition'.parameterDefinitions.'hudson.model.BooleanParameterDefinition'[0].name.text() == 'myParameterName'
        context.buildParameterNodes.'hudson.model.ParametersPropertyDefinition'.parameterDefinitions.'hudson.model.BooleanParameterDefinition'[0].defaultValue.text() == 'false'
        context.buildParameterNodes.'hudson.model.ParametersPropertyDefinition'.parameterDefinitions.'hudson.model.BooleanParameterDefinition'[0].description.text() == ''
    }

    def 'booleanParam name argument cant be null'() {
        when:
        context.booleanParam(null, false)

        then:
        thrown(NullPointerException)
    }

    def 'booleanParam name argument cant be empty'() {
        when:
        context.booleanParam('', false)

        then:
        thrown(IllegalStateException)
    }

    def 'base listTagsParam usage'() {
        when:
        context.listTagsParam("myParameterName", "http://mysvmurl", "my tag filer regex", true, false, "myDefaultValue", "myMaxTagsToDisplay", "myListTagsParamDescription")

        then:
        context.buildParameterNodes != null
//        context.buildParameterNodes.hudson.model.BooleanParameterDefinition[0].name.text() == "myParameterName"
//        context.scmNode.modules[0].text() == ''
    }

    def 'base choiceParam usage'() {
        when:
        context.choiceParam("myParameterName", ["option 1 (default)", "option 2", "option 3"], "myChoiceParamDescription")

        then:
        context.buildParameterNodes != null
//        context.buildParameterNodes.hudson.model.BooleanParameterDefinition[0].name.text() == "myParameterName"
//        context.scmNode.modules[0].text() == ''
    }

    def 'base fileParam usage'() {
        when:
        context.fileParam("myParameterName", "my/file/location.txt", "myFileParamDescription")

        then:
        context.buildParameterNodes != null
//        context.buildParameterNodes.hudson.model.BooleanParameterDefinition[0].name.text() == "myParameterName"
//        context.scmNode.modules[0].text() == ''
    }

    def 'base passwordParam usage'() {
        when:
        context.passwordParam("myParameterName", "myDefaultPassword", "myPasswordParamDescription")

        then:
        context.buildParameterNodes != null
//        context.buildParameterNodes.hudson.model.BooleanParameterDefinition[0].name.text() == "myParameterName"
//        context.scmNode.modules[0].text() == ''
    }

    def 'base runParam usage'() {
        when:
        context.runParam("myParameterName", "myJobToRun", "myRunParamDescription")

        then:
        context.buildParameterNodes != null
//        context.buildParameterNodes.hudson.model.BooleanParameterDefinition[0].name.text() == "myParameterName"
//        context.scmNode.modules[0].text() == ''
    }

    def 'base stringParam usage'() {
        when:
        context.stringParam("myParameterName", "my default stringParam value", "myStringParamDescription")

        then:
        context.buildParameterNodes != null
//        context.buildParameterNodes.hudson.model.BooleanParameterDefinition[0].name.text() == "myParameterName"
//        context.scmNode.modules[0].text() == ''
    }

    def 'base textParam usage'() {
        when:
        context.textParam("myParameterName", "my default textParam value", "myTextParamDescription")

        then:
        context.buildParameterNodes != null
//        context.buildParameterNodes.hudson.model.BooleanParameterDefinition[0].name.text() == "myParameterName"
//        context.scmNode.modules[0].text() == ''
    }
}
