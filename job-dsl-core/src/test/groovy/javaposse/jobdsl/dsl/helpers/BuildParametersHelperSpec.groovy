package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import spock.lang.Specification

class BuildParametersHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    BuildParametersContextHelper helper = new BuildParametersContextHelper(mockActions, JobType.Freeform)
    BuildParametersContext context = new BuildParametersContext()

    def 'base booleanParam usage'() {
        when:
        context.booleanParam('myParameterName', true, 'myBooleanParameterDescription')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultValue.text() == 'true'
        context.buildParameterNodes['myParameterName'].description.text() == 'myBooleanParameterDescription'
    }

    def 'simplified booleanParam usage'() {
        when:
        context.booleanParam('myParameterName', true)

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultValue.text() == 'true'
        context.buildParameterNodes['myParameterName'].description.text() == ''
    }

    def 'simplest booleanParam usage'() {
        when:
        context.booleanParam('myParameterName')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultValue.text() == 'false'
        context.buildParameterNodes['myParameterName'].description.text() == ''
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
        context.buildParameterNodes['myFirstBooleanParameter'].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes['myFirstBooleanParameter'].name.text() == 'myFirstBooleanParameter'
        context.buildParameterNodes['myFirstBooleanParameter'].defaultValue.text() == 'false'
        context.buildParameterNodes['myFirstBooleanParameter'].description.text() == ''
        context.buildParameterNodes['mySecondBooleanParameter'].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes['mySecondBooleanParameter'].name.text() == 'mySecondBooleanParameter'
        context.buildParameterNodes['mySecondBooleanParameter'].defaultValue.text() == 'false'
        context.buildParameterNodes['mySecondBooleanParameter'].description.text() == ''
    }

    def 'booleanParam already defined'() {
        when:
        context.booleanParam('one')
        context.booleanParam('one')

        then:
        thrown(IllegalArgumentException)
    }

    def 'base listTagsParam usage'() {
        when:
        context.listTagsParam('myParameterName', 'http://kenai.com/svn/myProject/tags', '^mytagsfilterregex', true,
                true, 'maximumNumberOfTagsToDisplay', 'theDefaultValue', 'myListTagsParameterDescription')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'hudson.scm.listtagsparameter.ListSubversionTagsParameterDefinition'
            name.text() == 'myParameterName'
            defaultValue.text() == 'theDefaultValue'
            tagsDir.text() == 'http://kenai.com/svn/myProject/tags'
            tagsFilter.text() == '^mytagsfilterregex'
            reverseByDate.text() == 'true'
            reverseByName.text() == 'true'
            maxTags.text() == 'maximumNumberOfTagsToDisplay'
            description.text() == 'myListTagsParameterDescription'
        }
    }

    def 'simplified listTagsParam usage'() {
        when:
        context.listTagsParam('myParameterName', 'http://kenai.com/svn/myProject/tags', '^mytagsfilterregex', true,
                true)

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'hudson.scm.listtagsparameter.ListSubversionTagsParameterDefinition'
            name.text() == 'myParameterName'
            tagsDir.text() == 'http://kenai.com/svn/myProject/tags'
            tagsFilter.text() == '^mytagsfilterregex'
            reverseByDate.text() == 'true'
            reverseByName.text() == 'true'
            maxTags.text() == 'all'
        }
    }

    def 'simplest listTagsParam usage'() {
        when:
        context.listTagsParam('myParameterName', 'http://kenai.com/svn/myProject/tags', '^mytagsfilterregex')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'hudson.scm.listtagsparameter.ListSubversionTagsParameterDefinition'
            name.text() == 'myParameterName'
            tagsDir.text() == 'http://kenai.com/svn/myProject/tags'
            tagsFilter.text() == '^mytagsfilterregex'
            reverseByDate.text() == 'false'
            reverseByName.text() == 'false'
            maxTags.text() == 'all'
        }
    }

    def 'listTagsParam name argument cant be null'() {
        when:
        context.listTagsParam(null, 'http://kenai.com/svn/myProject/tags', '^mytagsfilterregex')

        then:
        thrown(NullPointerException)
    }

    def 'listTagsParam name argument cant be empty'() {
        when:
        context.listTagsParam('', 'http://kenai.com/svn/myProject/tags', '^mytagsfilterregex')

        then:
        thrown(IllegalArgumentException)
    }

    def 'listTagsParam scmUrl argument cant be null'() {
        when:
        context.listTagsParam('myParameterName', null, '^mytagsfilterregex')

        then:
        thrown(NullPointerException)
    }

    def 'listTagsParam scmUrl argument cant be empty'() {
        when:
        context.listTagsParam('myParameterName', '', '^mytagsfilterregex')

        then:
        thrown(IllegalArgumentException)
    }

    def 'listTagsParam tagFilterRegex argument cant be null'() {
        when:
        context.listTagsParam('myParameterName', 'http://kenai.com/svn/myProject/tags', null)

        then:
        thrown(NullPointerException)
    }

    def 'listTagsParam tagFilterRegex argument cant be empty'() {
        when:
        context.listTagsParam('myParameterName', 'http://kenai.com/svn/myProject/tags', '')

        then:
        thrown(IllegalArgumentException)
    }

    def 'listTagsParam already defined'() {
        when:
        context.booleanParam('one')
        context.listTagsParam('one', 'http://kenai.com/svn/myProject/tags', '')

        then:
        thrown(IllegalArgumentException)
    }

    def 'base choiceParam usage'() {
        when:
        context.choiceParam('myParameterName', ['option 1 (default)', 'option 2'], 'myChoiceParamDescription')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.ChoiceParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].description.text() == 'myChoiceParamDescription'
        context.buildParameterNodes['myParameterName'].choices.size() == 1
        context.buildParameterNodes['myParameterName'].choices[0].attribute('class') == 'java.util.Arrays$ArrayList'
        context.buildParameterNodes['myParameterName'].choices[0].a.size() == 1
        context.buildParameterNodes['myParameterName'].choices[0].a[0].attribute('class') == 'string-array'
        context.buildParameterNodes['myParameterName'].choices[0].a[0].string.size() == 2
        context.buildParameterNodes['myParameterName'].choices[0].a[0].string[0].text() == 'option 1 (default)'
        context.buildParameterNodes['myParameterName'].choices[0].a[0].string[1].text() == 'option 2'
    }

    def 'simplified choiceParam usage'() {
        when:
        context.choiceParam('myParameterName', ['option 1 (default)', 'option 2', 'option 3'])

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.ChoiceParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].description.text() == ''
        context.buildParameterNodes['myParameterName'].choices.size() == 1
        context.buildParameterNodes['myParameterName'].choices[0].attribute('class') == 'java.util.Arrays$ArrayList'
        context.buildParameterNodes['myParameterName'].choices[0].a.size() == 1
        context.buildParameterNodes['myParameterName'].choices[0].a[0].attribute('class') == 'string-array'
        context.buildParameterNodes['myParameterName'].choices[0].a[0].string.size() == 3
        context.buildParameterNodes['myParameterName'].choices[0].a[0].string[0].text() == 'option 1 (default)'
        context.buildParameterNodes['myParameterName'].choices[0].a[0].string[1].text() == 'option 2'
        context.buildParameterNodes['myParameterName'].choices[0].a[0].string[2].text() == 'option 3'
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

    def 'choiceParam already defined'() {
        when:
        context.booleanParam('one')
        context.choiceParam('one', ['foo', 'bar'])

        then:
        thrown(IllegalArgumentException)
    }

    def 'base fileParam usage'() {
        when:
        context.fileParam('test/upload.zip', 'myFileParamDescription')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['test/upload.zip'].name() == 'hudson.model.FileParameterDefinition'
        context.buildParameterNodes['test/upload.zip'].name.text() == 'test/upload.zip'
        context.buildParameterNodes['test/upload.zip'].description.text() == 'myFileParamDescription'
    }

    def 'simplified fileParam usage'() {
        when:
        context.fileParam('test/upload.zip')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['test/upload.zip'].name() == 'hudson.model.FileParameterDefinition'
        context.buildParameterNodes['test/upload.zip'].name.text() == 'test/upload.zip'
        context.buildParameterNodes['test/upload.zip'].description.text() == ''
    }

    def 'fileParam fileLocation argument cant be null'() {
        when:
        context.fileParam(null)

        then:
        thrown(NullPointerException)
    }

    def 'fileParam fileLocation argument cant be empty'() {
        when:
        context.fileParam('')

        then:
        thrown(IllegalArgumentException)
    }

    def 'fileParam already defined'() {
        when:
        context.booleanParam('one')
        context.fileParam('one')

        then:
        thrown(IllegalArgumentException)
    }

    def 'base runParam usage'() {
        when:
        context.runParam('myParameterName', 'myJobName', 'myRunParamDescription')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.RunParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].projectName.text() == 'myJobName'
        context.buildParameterNodes['myParameterName'].description.text() == 'myRunParamDescription'
    }

    def 'simplest runParam usage'() {
        when:
        context.runParam('myParameterName', 'myJobName')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.RunParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].projectName.text() == 'myJobName'
        context.buildParameterNodes['myParameterName'].description.text() == ''
    }

    def 'fullest runParam usage'() {
        when:
        context.runParam('myParameterName', 'myJobName', 'my description with spaces', 'SUCCESSFUL')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.RunParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].projectName.text() == 'myJobName'
        context.buildParameterNodes['myParameterName'].description.text() == 'my description with spaces'
        context.buildParameterNodes['myParameterName'].filter.text() == 'SUCCESSFUL'
    }

    def 'runParam name argument cant be null'() {
        when:
        context.runParam(null, null)

        then:
        thrown(NullPointerException)
    }

    def 'runParam name argument cant be empty'() {
        when:
        context.runParam('', '')

        then:
        thrown(IllegalArgumentException)
    }

    def 'runParam jobToRun argument cant be null'() {
        when:
        context.runParam('myParameterName', null)

        then:
        thrown(NullPointerException)
    }

    def 'runParam jobToRun argument cant be empty'() {
        when:
        context.runParam('myParameterName', '')

        then:
        thrown(IllegalArgumentException)
    }

    def 'runParam already defined'() {
        when:
        context.booleanParam('one')
        context.runParam('one', 'job')

        then:
        thrown(IllegalArgumentException)
    }

    def 'base stringParam usage'() {
        when:
        context.stringParam('myParameterName', 'my default stringParam value', 'myStringParameterDescription')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.StringParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultValue.text() == 'my default stringParam value'
        context.buildParameterNodes['myParameterName'].description.text() == 'myStringParameterDescription'
    }

    def 'simplified stringParam usage'() {
        when:
        context.stringParam('myParameterName', 'my default stringParam value')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.StringParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultValue.text() == 'my default stringParam value'
        context.buildParameterNodes['myParameterName'].description.text() == ''
    }

    def 'simplest stringParam usage'() {
        when:
        context.stringParam('myParameterName')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.StringParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultValue.text() == ''
        context.buildParameterNodes['myParameterName'].description.text() == ''
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

    def 'stringParam already defined'() {
        when:
        context.booleanParam('one')
        context.stringParam('one')

        then:
        thrown(IllegalArgumentException)
    }

    def 'base textParam usage'() {
        when:
        context.textParam('myParameterName', 'my default textParam value', 'myTextParameterDescription')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.TextParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultValue.text() == 'my default textParam value'
        context.buildParameterNodes['myParameterName'].description.text() == 'myTextParameterDescription'
    }

    def 'simplified textParam usage'() {
        when:
        context.textParam('myParameterName', 'my default textParam value')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.TextParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultValue.text() == 'my default textParam value'
        context.buildParameterNodes['myParameterName'].description.text() == ''
    }

    def 'simplest textParam usage'() {
        when:
        context.textParam('myParameterName')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'hudson.model.TextParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultValue.text() == ''
        context.buildParameterNodes['myParameterName'].description.text() == ''
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

    def 'textParam already defined'() {
        when:
        context.booleanParam('one')
        context.textParam('one')

        then:
        thrown(IllegalArgumentException)
    }

    def 'nodeParam base usage'() {
        when:
        context.nodeParam('myParameterName')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'org.jvnet.jenkins.plugins.nodelabelparameter.NodeParameterDefinition'
            children().size() == 9
            name[0].value() == 'myParameterName'
            allowedSlaves[0].children().size() == 0
            defaultSlaves[0].children().size() == 0
            description[0].value() == null
            triggerIfResult[0].value() == 'multiSelectionDisallowed'
            nodeEligibility[0].attribute('class') ==
                    'org.jvnet.jenkins.plugins.nodelabelparameter.node.AllNodeEligibility'
            allowMultiNodeSelection[0].value() == false
            triggerConcurrentBuilds[0].value() == false
            ignoreOfflineNodes[0].value() == false
        }
    }

    def 'nodeParam fullest usage'() {
        when:
        context.nodeParam('myParameterName') {
            description('myRunParamDescription')
            allowedNodes(['myNode', 'myNode2'])
            defaultNodes(['myNode'])
            trigger('multiSelectionDisallowed')
            eligibility('IgnoreOfflineNodeEligibility')
        }

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'org.jvnet.jenkins.plugins.nodelabelparameter.NodeParameterDefinition'
            children().size() == 9
            name[0].value() == 'myParameterName'
            allowedSlaves[0].string.size() == 2
            allowedSlaves[0].string[0].value() == 'myNode'
            allowedSlaves[0].string[1].value() == 'myNode2'
            defaultSlaves[0].string.size() == 1
            defaultSlaves[0].string[0].value() == 'myNode'
            description[0].value() == 'myRunParamDescription'
            triggerIfResult[0].value() == 'multiSelectionDisallowed'
            nodeEligibility[0].attribute('class') ==
                 'org.jvnet.jenkins.plugins.nodelabelparameter.node.IgnoreOfflineNodeEligibility'
            allowMultiNodeSelection[0].value() == false
            triggerConcurrentBuilds[0].value() == false
            ignoreOfflineNodes[0].value() == false
        }
    }

    def 'nodeParam name argument cant be null'() {
        when:
        context.nodeParam(null, null)

        then:
        thrown(NullPointerException)
    }

    def 'nodeParam invalid trigger'() {
        when:
        context.nodeParam('myParamName') {
            trigger('invalid trigger')
        }

        then:
        thrown(IllegalArgumentException)
    }

    def 'nodeParam no name'() {
        when:
        context.nodeParam('')

        then:
        thrown(IllegalArgumentException)

        when:
        context.nodeParam(null)

        then:
        thrown(NullPointerException)
    }

    def 'nodeParam already defined'() {
        when:
        context.booleanParam('one')
        context.nodeParam('one')

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
        context.buildParameterNodes['myFirstBooleanParameter'].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes['myFirstBooleanParameter'].name.text() == 'myFirstBooleanParameter'
        context.buildParameterNodes['myFirstBooleanParameter'].defaultValue.text() == 'false'
        context.buildParameterNodes['myFirstBooleanParameter'].description.text() == ''
        context.buildParameterNodes['myFirstTextParam'].name() == 'hudson.model.TextParameterDefinition'
        context.buildParameterNodes['myFirstTextParam'].name.text() == 'myFirstTextParam'
        context.buildParameterNodes['myFirstTextParam'].defaultValue.text() == ''
        context.buildParameterNodes['myFirstTextParam'].description.text() == ''
        context.buildParameterNodes['mySecondBooleanParameter'].name() == 'hudson.model.BooleanParameterDefinition'
        context.buildParameterNodes['mySecondBooleanParameter'].name.text() == 'mySecondBooleanParameter'
        context.buildParameterNodes['mySecondBooleanParameter'].defaultValue.text() == 'false'
        context.buildParameterNodes['mySecondBooleanParameter'].description.text() == ''
    }

    def 'call parameters via helper'() {
        when:
        helper.parameters {
            booleanParam('myBooleanParam', true)
            choiceParam('myChoiceParam', ['option 1', 'option 2'])
            stringParam('myStringParam', 'foo')
            textParam('myTextParam', 'bar')
        }

        then:
        1 * mockActions.add(_)
    }
}
