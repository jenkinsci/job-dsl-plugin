package javaposse.jobdsl.dsl.helpers
import javaposse.jobdsl.dsl.WithXmlAction
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
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes[0].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes[0].name.text() == 'myParameterName'
        context.buildParameterNodes[0].defaultValue.text() == 'true'
        context.buildParameterNodes[0].description.text() == 'myBooleanParameterDescription'
    }

    def 'simplified booleanParam usage'() {
        when:
        context.booleanParam("myParameterName", true)

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes[0].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes[0].name.text() == 'myParameterName'
        context.buildParameterNodes[0].defaultValue.text() == 'true'
        context.buildParameterNodes[0].description.text() == ''
    }

    def 'simplest booleanParam usage'() {
        when:
        context.booleanParam("myParameterName")

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes[0].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes[0].name.text() == 'myParameterName'
        context.buildParameterNodes[0].defaultValue.text() == 'false'
        context.buildParameterNodes[0].description.text() == ''
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
        thrown(IllegalArgumentException)
    }

    def 'multiple booleanParams is just fine'() {
        when:
        context.booleanParam('myFirstBooleanParameter')
        context.booleanParam('mySecondBooleanParameter')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 2
        context.buildParameterNodes[0].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes[0].name.text() == 'myFirstBooleanParameter'
        context.buildParameterNodes[0].defaultValue.text() == 'false'
        context.buildParameterNodes[0].description.text() == ''
        context.buildParameterNodes[1].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes[1].name.text() == 'mySecondBooleanParameter'
        context.buildParameterNodes[1].defaultValue.text() == 'false'
        context.buildParameterNodes[1].description.text() == ''
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
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes[0].name() == 'hudson.model.ChoiceParameterDefinition'
        context.buildParameterNodes[0].name.text() == 'myParameterName'
        context.buildParameterNodes[0].description.text() == 'myChoiceParamDescription'
        context.buildParameterNodes[0].choices.size() == 1
        context.buildParameterNodes[0].choices[0].attribute('class') == 'java.util.Arrays$ArrayList'
        context.buildParameterNodes[0].choices[0].a.size() == 1
        context.buildParameterNodes[0].choices[0].a[0].attribute('class') == 'string-array'
        context.buildParameterNodes[0].choices[0].a[0].string.size() == 3
        context.buildParameterNodes[0].choices[0].a[0].string[0].text() == 'option 1 (default)'
        context.buildParameterNodes[0].choices[0].a[0].string[1].text() == 'option 2'
        context.buildParameterNodes[0].choices[0].a[0].string[2].text() == 'option 3'
    }

    def 'simplified choiceParam usage'() {
        when:
        context.choiceParam("myParameterName", ["option 1 (default)", "option 2", "option 3"])

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes[0].name() == 'hudson.model.ChoiceParameterDefinition'
        context.buildParameterNodes[0].name.text() == 'myParameterName'
        context.buildParameterNodes[0].description.text() == ''
        context.buildParameterNodes[0].choices.size() == 1
        context.buildParameterNodes[0].choices[0].attribute('class') == 'java.util.Arrays$ArrayList'
        context.buildParameterNodes[0].choices[0].a.size() == 1
        context.buildParameterNodes[0].choices[0].a[0].attribute('class') == 'string-array'
        context.buildParameterNodes[0].choices[0].a[0].string.size() == 3
        context.buildParameterNodes[0].choices[0].a[0].string[0].text() == 'option 1 (default)'
        context.buildParameterNodes[0].choices[0].a[0].string[1].text() == 'option 2'
        context.buildParameterNodes[0].choices[0].a[0].string[2].text() == 'option 3'
    }

    def 'choiceParam name argument cant be null'() {
        when:
        context.choiceParam(null, ['option 1'])

        then:
        thrown(NullPointerException)
    }

    def 'choiceParam name argument cant be empty'() {
        when:
        context.choiceParam('', ['option 1'])

        then:
        thrown(IllegalArgumentException)
    }

    def 'choiceParam options argument cant be null'() {
        when:
        context.choiceParam('myParameterName', null)

        then:
        thrown(NullPointerException)
    }

    def 'choiceParam options argument cant be empty'() {
        when:
        context.choiceParam('myParameterName', [])

        then:
        thrown(IllegalArgumentException)
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
        context.stringParam("myParameterName", "my default stringParam value", "myStringParameterDescription")

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes[0].name() == 'hudson.model.StringParameterDefinition'
        context.buildParameterNodes[0].name.text() == 'myParameterName'
        context.buildParameterNodes[0].defaultValue.text() == 'my default stringParam value'
        context.buildParameterNodes[0].description.text() == 'myStringParameterDescription'
    }

    def 'simplified stringParam usage'() {
        when:
        context.stringParam("myParameterName", "my default stringParam value")

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes[0].name() == 'hudson.model.StringParameterDefinition'
        context.buildParameterNodes[0].name.text() == 'myParameterName'
        context.buildParameterNodes[0].defaultValue.text() == 'my default stringParam value'
        context.buildParameterNodes[0].description.text() == ''
    }

    def 'simplest stringParam usage'() {
        when:
        context.stringParam("myParameterName")

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes[0].name() == 'hudson.model.StringParameterDefinition'
        context.buildParameterNodes[0].name.text() == 'myParameterName'
        context.buildParameterNodes[0].defaultValue.text() == ''
        context.buildParameterNodes[0].description.text() == ''
    }

    def 'stringParam name argument cant be null'() {
        when:
        context.stringParam(null)

        then:
        thrown(NullPointerException)
    }

    def 'stringParam name argument cant be empty'() {
        when:
        context.stringParam('')

        then:
        thrown(IllegalArgumentException)
    }

    def 'base textParam usage'() {
        when:
        context.textParam("myParameterName", "my default textParam value", "myTextParameterDescription")

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes[0].name() == 'hudson.model.TextParameterDefinition'
        context.buildParameterNodes[0].name.text() == 'myParameterName'
        context.buildParameterNodes[0].defaultValue.text() == 'my default textParam value'
        context.buildParameterNodes[0].description.text() == 'myTextParameterDescription'
    }

    def 'simplified textParam usage'() {
        when:
        context.textParam("myParameterName", "my default textParam value")

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes[0].name() == 'hudson.model.TextParameterDefinition'
        context.buildParameterNodes[0].name.text() == 'myParameterName'
        context.buildParameterNodes[0].defaultValue.text() == 'my default textParam value'
        context.buildParameterNodes[0].description.text() == ''
    }

    def 'simplest textParam usage'() {
        when:
        context.textParam("myParameterName")

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes[0].name() == 'hudson.model.TextParameterDefinition'
        context.buildParameterNodes[0].name.text() == 'myParameterName'
        context.buildParameterNodes[0].defaultValue.text() == ''
        context.buildParameterNodes[0].description.text() == ''
    }

    def 'textParam name argument cant be null'() {
        when:
        context.textParam(null)

        then:
        thrown(NullPointerException)
    }

    def 'textParam name argument cant be empty'() {
        when:
        context.textParam('')

        then:
        thrown(IllegalArgumentException)
    }

    def 'multiple mixed Param types is just fine'() {
        when:
        context.booleanParam('myFirstBooleanParameter')
        context.textParam('myFirstTextParam')
        context.booleanParam('mySecondBooleanParameter')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 3
        context.buildParameterNodes[0].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes[0].name.text() == 'myFirstBooleanParameter'
        context.buildParameterNodes[0].defaultValue.text() == 'false'
        context.buildParameterNodes[0].description.text() == ''
        context.buildParameterNodes[1].name() == 'hudson.model.TextParameterDefinition'
        context.buildParameterNodes[1].name.text() == 'myFirstTextParam'
        context.buildParameterNodes[1].defaultValue.text() == ''
        context.buildParameterNodes[1].description.text() == ''
        context.buildParameterNodes[2].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes[2].name.text() == 'mySecondBooleanParameter'
        context.buildParameterNodes[2].defaultValue.text() == 'false'
        context.buildParameterNodes[2].description.text() == ''
    }
}
