package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class BuildParametersContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    BuildParametersContext context = new BuildParametersContext(jobManagement, item)

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
        thrown(DslScriptException)
    }

    def 'booleanParam name argument cant be empty'() {
        when:
        context.booleanParam('', false)

        then:
        thrown(DslScriptException)
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
        thrown(DslScriptException)
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
            children().size() == 10
            name.text() == 'myParameterName'
            defaultValue.text() == 'theDefaultValue'
            tagsDir.text() == 'http://kenai.com/svn/myProject/tags'
            tagsFilter.text() == '^mytagsfilterregex'
            reverseByDate.text() == 'true'
            reverseByName.text() == 'true'
            maxTags.text() == 'maximumNumberOfTagsToDisplay'
            description.text() == 'myListTagsParameterDescription'
            credentialsId.text().empty
            !uuid.text().empty
        }
        (1.._) * jobManagement.requireMinimumPluginVersion('subversion', '2.1')
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
            children().size() == 10
            name.text() == 'myParameterName'
            defaultValue.text().empty
            tagsDir.text() == 'http://kenai.com/svn/myProject/tags'
            tagsFilter.text() == '^mytagsfilterregex'
            reverseByDate.text() == 'true'
            reverseByName.text() == 'true'
            maxTags.text() == 'all'
            description.text().empty
            credentialsId.text().empty
            !uuid.text().empty
        }
        (1.._) * jobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'simplest listTagsParam usage'() {
        when:
        context.listTagsParam('myParameterName', 'http://kenai.com/svn/myProject/tags', '^mytagsfilterregex')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'hudson.scm.listtagsparameter.ListSubversionTagsParameterDefinition'
            children().size() == 10
            name.text() == 'myParameterName'
            defaultValue.text().empty
            tagsDir.text() == 'http://kenai.com/svn/myProject/tags'
            tagsFilter.text() == '^mytagsfilterregex'
            reverseByDate.text() == 'false'
            reverseByName.text() == 'false'
            maxTags.text() == 'all'
            description.text().empty
            credentialsId.text().empty
            !uuid.text().empty
        }
        (1.._) * jobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'simplest closure listTagsParam usage'() {
        when:
        context.listTagsParam('myParameterName', 'http://kenai.com/svn/myProject/tags') {
        }

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'hudson.scm.listtagsparameter.ListSubversionTagsParameterDefinition'
            children().size() == 10
            name.text() == 'myParameterName'
            defaultValue.text().empty
            tagsDir.text() == 'http://kenai.com/svn/myProject/tags'
            tagsFilter.text().empty
            reverseByDate.text() == 'false'
            reverseByName.text() == 'false'
            maxTags.text().empty
            description.text().empty
            credentialsId.text().empty
            !uuid.text().empty
        }
        (1.._) * jobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'listTagsParam with all closure options'() {
        when:
        context.listTagsParam('myParameterName', 'http://kenai.com/svn/myProject/tags') {
            description('lorem ipsum')
            tagFilterRegex(/myfilter/)
            sortNewestFirst()
            sortZtoA()
            maxTagsToDisplay('40')
            defaultValue('test')
            credentialsId('CREDENTIALS')
        }

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'hudson.scm.listtagsparameter.ListSubversionTagsParameterDefinition'
            children().size() == 10
            name.text() == 'myParameterName'
            defaultValue.text() == 'test'
            tagsDir.text() == 'http://kenai.com/svn/myProject/tags'
            tagsFilter.text() == 'myfilter'
            reverseByDate.text() == 'true'
            reverseByName.text() == 'true'
            maxTags.text() == '40'
            description.text() == 'lorem ipsum'
            credentialsId.text() == 'CREDENTIALS'
            !uuid.text().empty
        }
        (1.._) * jobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'listTagsParam with maxTagsToDisplay int argument'() {
        when:
        context.listTagsParam('myParameterName', 'http://kenai.com/svn/myProject/tags') {
            description('lorem ipsum')
            tagFilterRegex(/myfilter/)
            sortNewestFirst()
            sortZtoA()
            maxTagsToDisplay(40)
            defaultValue('test')
            credentialsId('CREDENTIALS')
        }

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'hudson.scm.listtagsparameter.ListSubversionTagsParameterDefinition'
            children().size() == 10
            name.text() == 'myParameterName'
            defaultValue.text() == 'test'
            tagsDir.text() == 'http://kenai.com/svn/myProject/tags'
            tagsFilter.text() == 'myfilter'
            reverseByDate.text() == 'true'
            reverseByName.text() == 'true'
            maxTags.text() == '40'
            description.text() == 'lorem ipsum'
            credentialsId.text() == 'CREDENTIALS'
            !uuid.text().empty
        }
        (1.._) * jobManagement.requireMinimumPluginVersion('subversion', '2.1')
    }

    def 'listTagsParam name argument cant be null'() {
        when:
        context.listTagsParam(null, 'http://kenai.com/svn/myProject/tags', '^mytagsfilterregex')

        then:
        thrown(DslScriptException)
    }

    def 'listTagsParam name argument cant be empty'() {
        when:
        context.listTagsParam('', 'http://kenai.com/svn/myProject/tags', '^mytagsfilterregex')

        then:
        thrown(DslScriptException)
    }

    def 'listTagsParam scmUrl argument cant be null'() {
        when:
        context.listTagsParam('myParameterName', null, '^mytagsfilterregex')

        then:
        thrown(DslScriptException)
    }

    def 'listTagsParam scmUrl argument cant be empty'() {
        when:
        context.listTagsParam('myParameterName', '', '^mytagsfilterregex')

        then:
        thrown(DslScriptException)
    }

    def 'listTagsParam tagFilterRegex argument can be null or empty'(String filter) {
        when:
        context.listTagsParam('myParameterName', 'http://kenai.com/svn/myProject/tags', filter)

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'hudson.scm.listtagsparameter.ListSubversionTagsParameterDefinition'
            children().size() == 10
            name.text() == 'myParameterName'
            tagsDir.text() == 'http://kenai.com/svn/myProject/tags'
            tagsFilter.text() == ''
            reverseByDate.text() == 'false'
            reverseByName.text() == 'false'
            maxTags.text() == 'all'
        }
        (1.._) * jobManagement.requireMinimumPluginVersion('subversion', '2.1')

        where:
        filter << [null, '']
    }

    def 'listTagsParam already defined'() {
        when:
        context.booleanParam('one')
        context.listTagsParam('one', 'http://kenai.com/svn/myProject/tags', '')

        then:
        thrown(DslScriptException)
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
        thrown(DslScriptException)
    }

    def 'choiceParam name argument cant be empty'() {
        when:
        context.choiceParam('', ['option 1'])

        then:
        thrown(DslScriptException)
    }

    def 'choiceParam options argument cant be null'() {
        when:
        context.choiceParam('myParameterName', null)

        then:
        thrown(DslScriptException)
    }

    def 'choiceParam options argument cant be empty'() {
        when:
        context.choiceParam('myParameterName', [])

        then:
        thrown(DslScriptException)
    }

    def 'choiceParam already defined'() {
        when:
        context.booleanParam('one')
        context.choiceParam('one', ['foo', 'bar'])

        then:
        thrown(DslScriptException)
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
        thrown(DslScriptException)
    }

    def 'fileParam fileLocation argument cant be empty'() {
        when:
        context.fileParam('')

        then:
        thrown(DslScriptException)
    }

    def 'fileParam already defined'() {
        when:
        context.booleanParam('one')
        context.fileParam('one')

        then:
        thrown(DslScriptException)
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
        thrown(DslScriptException)
    }

    def 'runParam name argument cant be empty'() {
        when:
        context.runParam('', '')

        then:
        thrown(DslScriptException)
    }

    def 'runParam jobToRun argument cant be null'() {
        when:
        context.runParam('myParameterName', null)

        then:
        thrown(DslScriptException)
    }

    def 'runParam jobToRun argument cant be empty'() {
        when:
        context.runParam('myParameterName', '')

        then:
        thrown(DslScriptException)
    }

    def 'runParam already defined'() {
        when:
        context.booleanParam('one')
        context.runParam('one', 'job')

        then:
        thrown(DslScriptException)
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
        thrown(DslScriptException)
    }

    def 'stringParam name argument cant be empty'() {
        when:
        context.stringParam('')

        then:
        thrown(DslScriptException)
    }

    def 'stringParam already defined'() {
        when:
        context.booleanParam('one')
        context.stringParam('one')

        then:
        thrown(DslScriptException)
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
        thrown(DslScriptException)
    }

    def 'textParam name argument cant be empty'() {
        when:
        context.textParam('')

        then:
        thrown(DslScriptException)
    }

    def 'textParam already defined'() {
        when:
        context.booleanParam('one')
        context.textParam('one')

        then:
        thrown(DslScriptException)
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
        1 * jobManagement.requirePlugin('nodelabelparameter')
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
        1 * jobManagement.requirePlugin('nodelabelparameter')
    }

    def 'nodeParam name argument cant be null'() {
        when:
        context.nodeParam(null, null)

        then:
        thrown(DslScriptException)
    }

    def 'nodeParam invalid trigger'() {
        when:
        context.nodeParam('myParamName') {
            trigger('invalid trigger')
        }

        then:
        thrown(DslScriptException)
    }

    def 'nodeParam no name'() {
        when:
        context.nodeParam('')

        then:
        thrown(DslScriptException)

        when:
        context.nodeParam(null)

        then:
        thrown(DslScriptException)
    }

    def 'nodeParam already defined'() {
        when:
        context.booleanParam('one')
        context.nodeParam('one')

        then:
        thrown(DslScriptException)
    }

    def 'labelParam base usage'() {
        when:
        context.labelParam('myParameterName') {
            defaultValue('myDefaultValue')
            description('myRunParamDescription')
        }

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'org.jvnet.jenkins.plugins.nodelabelparameter.LabelParameterDefinition'
            children().size() == 6
            name.text() == 'myParameterName'
            defaultValue.text() == 'myDefaultValue'
            description.text() == 'myRunParamDescription'
            allNodesMatchingLabel.text() == 'false'
            triggerIfResult.text() == 'allCases'
            nodeEligibility.size() == 1
            nodeEligibility[0].@class == 'org.jvnet.jenkins.plugins.nodelabelparameter.node.AllNodeEligibility'
        }
        1 * jobManagement.requirePlugin('nodelabelparameter')
    }

    def 'labelParam simplest usage'() {
        when:
        context.labelParam('myParameterName')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'org.jvnet.jenkins.plugins.nodelabelparameter.LabelParameterDefinition'
            children().size() == 6
            name.text() == 'myParameterName'
            defaultValue.text() == ''
            description.text() == ''
            allNodesMatchingLabel.text() == 'false'
            triggerIfResult.text() == 'allCases'
            nodeEligibility.size() == 1
            nodeEligibility[0].@class == 'org.jvnet.jenkins.plugins.nodelabelparameter.node.AllNodeEligibility'
        }
        1 * jobManagement.requirePlugin('nodelabelparameter')
    }

    def 'labelParam run on all nodes'() {
        when:
        context.labelParam('myParameterName') {
            allNodes()
        }

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'org.jvnet.jenkins.plugins.nodelabelparameter.LabelParameterDefinition'
            children().size() == 6
            name.text() == 'myParameterName'
            allNodesMatchingLabel.text() == 'true'
            defaultValue.text() == ''
            description.text() == ''
            triggerIfResult.text() == 'allCases'
            nodeEligibility.size() == 1
            nodeEligibility[0].@class == 'org.jvnet.jenkins.plugins.nodelabelparameter.node.AllNodeEligibility'
        }
        1 * jobManagement.requirePlugin('nodelabelparameter')
    }

    def 'labelParam allNodes accepts valid values only'(String trigger, String eligibility) {
        when:
        context.labelParam('myParameterName') {
            allNodes(trigger, eligibility)
        }

        then:
        thrown(DslScriptException)

        where:
        trigger    | eligibility
        'allCases' | 'what?'
        'what?'    | 'AllNodeEligibility?'
    }

    def 'labelParam fullest usage'() {
        when:
        context.labelParam('myParameterName') {
            defaultValue('myDefaultValue')
            description('myRunParamDescription')
            allNodes('success', 'IgnoreOfflineNodeEligibility')
        }

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['myParameterName']) {
            name() == 'org.jvnet.jenkins.plugins.nodelabelparameter.LabelParameterDefinition'
            children().size() == 6
            name.text() == 'myParameterName'
            defaultValue.text() == 'myDefaultValue'
            description.text() == 'myRunParamDescription'
            allNodesMatchingLabel.text() == 'true'
            triggerIfResult.text() == 'success'
            nodeEligibility.size() == 1
            nodeEligibility[0].@class ==
                    'org.jvnet.jenkins.plugins.nodelabelparameter.node.IgnoreOfflineNodeEligibility'
        }
        1 * jobManagement.requirePlugin('nodelabelparameter')
    }

    def 'labelParam name argument cant be null'() {
        when:
        context.labelParam(null)

        then:
        thrown(DslScriptException)
    }

    def 'labelParam name argument cant be empty'() {
        when:
        context.labelParam('')

        then:
        thrown(DslScriptException)
    }

    def 'labelParam already defined'() {
        when:
        context.booleanParam('one')
        context.labelParam('one')

        then:
        thrown(DslScriptException)
    }

    def 'gitParam already defined'() {
        when:
        context.stringParam('paramName')
        context.gitParam('paramName')

        then:
        thrown(DslScriptException)
    }

    def 'gitParam name argument cant be null'() {
        when:
        context.gitParam(null)

        then:
        thrown(DslScriptException)
    }

    def 'gitParam name argument cant be empty'() {
        when:
        context.gitParam('')

        then:
        thrown(DslScriptException)
    }

    def 'gitParam minimal options'() {
        when:
        context.gitParam('paramName')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['paramName']) {
            name() == 'net.uaznia.lukanus.hudson.plugins.gitparameter.GitParameterDefinition'
            children().size() == 8
            name.text() == 'paramName'
            description[0].value() == ''
            UUID.fromString(uuid[0].value() as String)
            type[0].value() == 'PT_TAG'
            branch[0].value() == ''
            tagFilter[0].value() == ''
            sortMode[0].value() == 'NONE'
            defaultValue[0].value() == ''
        }
        1 * jobManagement.requireMinimumPluginVersion('git-parameter', '0.4.0')
    }

    def 'gitParam all options'() {
        when:
        context.gitParam('sha') {
            description('Revision commit SHA')
            type('REVISION')
            branch('master')
            tagFilter('*')
            sortMode('ASCENDING_SMART')
            defaultValue('foo')
        }

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['sha']) {
            name() == 'net.uaznia.lukanus.hudson.plugins.gitparameter.GitParameterDefinition'
            children().size() == 8
            name.text() == 'sha'
            description[0].value() == 'Revision commit SHA'
            UUID.fromString(uuid[0].value() as String)
            type[0].value() == 'PT_REVISION'
            branch[0].value() == 'master'
            tagFilter[0].value() == '*'
            sortMode[0].value() == 'ASCENDING_SMART'
            defaultValue[0].value() == 'foo'
        }
        1 * jobManagement.requireMinimumPluginVersion('git-parameter', '0.4.0')
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

    def 'active choice param with no options'() {
        when:
        context.activeChoiceParam('activeChoiceGroovyParam') {
        }

        then:
        with(context.buildParameterNodes['activeChoiceGroovyParam']) {
            name() == 'org.biouno.unochoice.ChoiceParameter'
            children().size() == 6
            name.text() == 'activeChoiceGroovyParam'
            description.text() == ''
            randomName.text() =~ /choice-parameter-\d+/
            visibleItemCount.text() == '1'
            choiceType.text() == 'PT_SINGLE_SELECT'
            filterable.text() == 'false'
        }
        1 * jobManagement.requireMinimumPluginVersion('uno-choice', '1.2')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'active choice param with all options and groovy script'() {
        when:
        context.activeChoiceParam('activeChoiceGroovyParam') {
            description('Active choice param test/groovy script')
            filterable()
            choiceType('MULTI_SELECT')
            groovyScript {
                script('x1')
                fallbackScript('x2')
            }
        }

        then:
        with(context.buildParameterNodes['activeChoiceGroovyParam']) {
            name() == 'org.biouno.unochoice.ChoiceParameter'
            children().size() == 7
            name.text() == 'activeChoiceGroovyParam'
            description.text() == 'Active choice param test/groovy script'
            randomName.text() =~ /choice-parameter-\d+/
            visibleItemCount.text() == '1'
            choiceType.text() == 'PT_MULTI_SELECT'
            filterable.text() == 'true'
            with(script[0]) {
                attributes()['class'] == 'org.biouno.unochoice.model.GroovyScript'
                children().size() == 2
                script.text() == 'x1'
                fallbackScript.text() == 'x2'
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('uno-choice', '1.2')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'active choice param with all options and scriptler script'() {
        when:
        context.activeChoiceParam('activeChoiceScriptlerParam') {
            description('Active choice param test/scriptler script')
            filterable()
            choiceType('MULTI_SELECT')
            scriptlerScript('scriptler-1.groovy') {
                parameter('param1', 'x1')
                parameter('param2', 'x2')
            }
        }

        then:
        with(context.buildParameterNodes['activeChoiceScriptlerParam']) {
            name() == 'org.biouno.unochoice.ChoiceParameter'
            children().size() == 7
            name.text() == 'activeChoiceScriptlerParam'
            description.text() == 'Active choice param test/scriptler script'
            randomName.text() =~ /choice-parameter-\d+/
            visibleItemCount.text() == '1'
            choiceType.text() == 'PT_MULTI_SELECT'
            filterable.text() == 'true'
            with(script[0]) {
                attributes()['class'] == 'org.biouno.unochoice.model.ScriptlerScript'
                scriptlerScriptId.text() == 'scriptler-1.groovy'
                children().size() == 2
                with(parameters[0]) {
                    children().size() == 2
                    entry[0].string[0].text() == 'param1'
                    entry[0].string[1].text() == 'x1'
                    entry[1].string[0].text() == 'param2'
                    entry[1].string[1].text() == 'x2'
                }
            }
        }
        1 * jobManagement.requireMinimumPluginVersion('uno-choice', '1.2')
        1 * jobManagement.logDeprecationWarning()
    }

    def 'active choice reactive param without options'() {
        when:
        context.activeChoiceReactiveParam('activeChoiceReferenceParam') {
        }

        then:
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['activeChoiceReferenceParam']) {
            name() == 'org.biouno.unochoice.CascadeChoiceParameter'
            children().size() == 8
            parameters[0].attributes()['class'] == 'linked-hash-map'
            description.text() == ''
            randomName.text() =~ /choice-parameter-\d+/
            visibleItemCount.text() == '1'
            filterable.text() == 'false'
            choiceType.text() == 'PT_SINGLE_SELECT'
            script.text() == ''
            referencedParameters.text() == ''
        }
        1 * jobManagement.logDeprecationWarning()
    }

    def 'active choice reactive param with all options and groovy script'() {
        when:
        context.activeChoiceReactiveParam('activeChoiceReferenceScriptlerParam') {
            description('Active choice param test')
            filterable()
            choiceType('SINGLE_SELECT')
            groovyScript {
                script('x1')
                fallbackScript('x2')
            }
            referencedParameter('param1')
            referencedParameter('param2')
        }

        then:
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['activeChoiceReferenceScriptlerParam']) {
            children().size() == 9
            parameters[0].attributes()['class'] == 'linked-hash-map'
            name() == 'org.biouno.unochoice.CascadeChoiceParameter'
            description.text() == 'Active choice param test'
            randomName.text() =~ /choice-parameter-\d+/
            visibleItemCount.text() == '1'
            filterable.text() == 'true'
            choiceType.text() == 'PT_SINGLE_SELECT'
            with(script[0]) {
                attributes()['class'] == 'org.biouno.unochoice.model.GroovyScript'
                children().size() == 2
                script.text() == 'x1'
                fallbackScript.text() == 'x2'
            }
            referencedParameters.text() == 'param1, param2'
        }
        1 * jobManagement.logDeprecationWarning()
    }

    def 'active choice reactive reference param without options'() {
        when:
        context.activeChoiceReactiveReferenceParam('activeChoiceReactiveReferenceParam') {
        }

        then:
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['activeChoiceReactiveReferenceParam']) {
            name() == 'org.biouno.unochoice.DynamicReferenceParameter'
            children().size() == 8
            parameters[0].attributes()['class'] == 'linked-hash-map'
            description.text() == ''
            randomName.text() =~ /choice-parameter-\d+/
            visibleItemCount.text() == '1'
            choiceType.text() == 'ET_TEXT_BOX'
            script.text() == ''
            referencedParameters.text() == ''
            omitValueField.text() == 'false'
        }
        1 * jobManagement.logDeprecationWarning()
    }

    def 'active choice reactive reference param with all options and scriptler script'() {
        when:
        context.activeChoiceReactiveReferenceParam('activeChoiceReactiveReferenceScriptlerParam') {
            description('Active choice reactive reference param test with scriptler script')
            omitValueField()
            choiceType('FORMATTED_HIDDEN_HTML')
            scriptlerScript('scriptler-2.groovy') {
                parameter('script-param', 'x1')
            }
            referencedParameter('param3')
            referencedParameter('param4')
        }

        then:
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['activeChoiceReactiveReferenceScriptlerParam']) {
            children().size() == 9
            parameters[0].attributes()['class'] == 'linked-hash-map'
            name() == 'org.biouno.unochoice.DynamicReferenceParameter'
            description.text() == 'Active choice reactive reference param test with scriptler script'
            randomName.text() =~ /choice-parameter-\d+/
            visibleItemCount.text() == '1'
            choiceType.text() == 'ET_FORMATTED_HIDDEN_HTML'
            with(script[0]) {
                attributes()['class'] == 'org.biouno.unochoice.model.ScriptlerScript'
                scriptlerScriptId.text() == 'scriptler-2.groovy'
                children().size() == 2
                with(parameters[0]) {
                    children().size() == 1
                    entry[0].string[0].text() == 'script-param'
                    entry[0].string[1].text() == 'x1'
                }
            }
            referencedParameters.text() == 'param3, param4'
        }
        1 * jobManagement.logDeprecationWarning()
    }

    def 'credentials parameter with minimal options'() {
        when:
        context.credentialsParam('foo')

        then:
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['foo']) {
            name() == 'com.cloudbees.plugins.credentials.CredentialsParameterDefinition'
            children().size() == 5
            name.text() == 'foo'
            description.text() == ''
            defaultValue.text() == ''
            credentialType.text() == 'com.cloudbees.plugins.credentials.common.StandardCredentials'
            required.text() == 'false'
        }
    }

    def 'credentials parameter with all options'() {
        when:
        context.credentialsParam('foo') {
            type('test')
            defaultValue('bar')
            description('hello')
            required()
        }

        then:
        context.buildParameterNodes.size() == 1
        with(context.buildParameterNodes['foo']) {
            name() == 'com.cloudbees.plugins.credentials.CredentialsParameterDefinition'
            children().size() == 5
            name.text() == 'foo'
            description.text() == 'hello'
            defaultValue.text() == 'bar'
            credentialType.text() == 'test'
            required.text() == 'true'
        }
    }

    def 'credentials parameter with invalid type option'() {
        when:
        context.credentialsParam('foo') {
            type(value)
        }

        then:
        thrown(DslScriptException)

        where:
        value << [null, '']
    }

    def 'credentials parameter with invalid parameter name'() {
        when:
        context.credentialsParam(value)

        then:
        thrown(DslScriptException)

        where:
        value << [null, '']
    }

    def 'credentials parameter with duplicate parameter name'() {
        when:
        context.credentialsParam('foo')
        context.credentialsParam('foo')

        then:
        thrown(DslScriptException)
    }

    def 'base globalVariableParam usage'() {
        when:
        context.globalVariableParam('myParameterName', '${MY_DEFAULT_GLOBAL_VARIABLE}',
                'myGlobalVariableParameterDescription')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() ==
                'hudson.plugins.global__variable__string__parameter.GlobalVariableStringParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultValue.text() == '${MY_DEFAULT_GLOBAL_VARIABLE}'
        context.buildParameterNodes['myParameterName'].description.text() == 'myGlobalVariableParameterDescription'
    }

    def 'simplified globalVariableParam usage'() {
        when:
        context.globalVariableParam('myParameterName', '${MY_DEFAULT_GLOBAL_VARIABLE}')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() ==
                'hudson.plugins.global__variable__string__parameter.GlobalVariableStringParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultValue.text() == '${MY_DEFAULT_GLOBAL_VARIABLE}'
        context.buildParameterNodes['myParameterName'].description.text() == ''
    }

    def 'simplest globalVariableParam usage'() {
        when:
        context.globalVariableParam('myParameterName')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() ==
                'hudson.plugins.global__variable__string__parameter.GlobalVariableStringParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultValue.text() == ''
        context.buildParameterNodes['myParameterName'].description.text() == ''
    }

    def 'globalVariableParam name argument cant be null'() {
        when:
        context.globalVariableParam(null)

        then:
        thrown(DslScriptException)
    }

    def 'globalVariableParam name argument cant be empty'() {
        when:
        context.globalVariableParam('')

        then:
        thrown(DslScriptException)
    }

    def 'globalVariableParam already defined'() {
        when:
        context.stringParam('one')
        context.globalVariableParam('one')

        then:
        thrown(DslScriptException)
    }

    def 'base matrixCombinationsParam usage'() {
        when:
        context.matrixCombinationsParam('myParameterName', 'default_value', 'myGlobalVariableParameterDescription')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() ==
                'hudson.plugins.matrix__configuration__parameter.MatrixCombinationsParameterDefinition'
        context.buildParameterNodes['myParameterName'].children().size() == 3
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultCombinationFilter.text() == 'default_value'
        context.buildParameterNodes['myParameterName'].description.text() == 'myGlobalVariableParameterDescription'
        1 * jobManagement.requireMinimumPluginVersion('matrix-combinations-parameter', '1.0.9')
    }

    def 'simplified matrixCombinationsParam usage'() {
        when:
        context.matrixCombinationsParam('myParameterName', 'default_value')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() ==
                'hudson.plugins.matrix__configuration__parameter.MatrixCombinationsParameterDefinition'
        context.buildParameterNodes['myParameterName'].children().size() == 3
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].defaultCombinationFilter.text() == 'default_value'
        context.buildParameterNodes['myParameterName'].description.text() == ''
        1 * jobManagement.requireMinimumPluginVersion('matrix-combinations-parameter', '1.0.9')
    }

    def 'simplest matrixCombinationsParam usage'() {
        when:
        context.matrixCombinationsParam('myParameterName')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() ==
                'hudson.plugins.matrix__configuration__parameter.MatrixCombinationsParameterDefinition'
        context.buildParameterNodes['myParameterName'].children().size() == 2
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].description.text() == ''
        1 * jobManagement.requireMinimumPluginVersion('matrix-combinations-parameter', '1.0.9')
    }

    def 'matrixCombinationsParam name argument can not be null or empty'() {
        when:
        context.matrixCombinationsParam(name)

        then:
        thrown(DslScriptException)

        where:
        name << [null, '']
    }

    def 'matrixCombinationsParam already defined'() {
        when:
        context.stringParam('one')
        context.matrixCombinationsParam('one')

        then:
        thrown(DslScriptException)
    }

    def 'extension parameter'() {
        given:
        Node paramNode = new NodeBuilder().'my.custom.ParamDefinition' {
            name('myParameterName')
        }

        when:
        context.addExtensionNode(paramNode)

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].name() == 'my.custom.ParamDefinition'
        context.buildParameterNodes['myParameterName'].children().size() == 1
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
    }

    def 'extension parameter must have a name'() {
        given:
        Node paramNode = new NodeBuilder().'my.custom.ParamDefinition' {
        }

        when:
        context.addExtensionNode(paramNode)

        then:
        thrown(DslScriptException)
    }

    def 'base nonStoredPasswordParameter usage'() {
        when:
        context.nonStoredPasswordParam('myParameterName', 'my parameter description')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].children().size() == 2
        context.buildParameterNodes['myParameterName'].name() ==
                'com.michelin.cio.hudson.plugins.passwordparam.PasswordParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].description.text() == 'my parameter description'
    }

    def 'simplified nonStoredPasswordParameter usage'() {
        when:
        context.nonStoredPasswordParam('myParameterName')

        then:
        context.buildParameterNodes != null
        context.buildParameterNodes.size() == 1
        context.buildParameterNodes['myParameterName'].children().size() == 2
        context.buildParameterNodes['myParameterName'].name() ==
                'com.michelin.cio.hudson.plugins.passwordparam.PasswordParameterDefinition'
        context.buildParameterNodes['myParameterName'].name.text() == 'myParameterName'
        context.buildParameterNodes['myParameterName'].description.text() == ''
    }

    def 'nonStoredPasswordParameter name argument cant be null or empty'() {
        when:
        context.nonStoredPasswordParam(name)

        then:
        thrown(DslScriptException)

        where:
        name << [null, '']
    }

    def 'nonStoredPasswordParameter already defined'() {
        when:
        context.booleanParam('one')
        context.nonStoredPasswordParam('one')

        then:
        thrown(DslScriptException)
    }
}
