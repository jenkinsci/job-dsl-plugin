package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class DownstreamTriggerParameterContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    DownstreamTriggerParameterContext context = new DownstreamTriggerParameterContext(jobManagement, item)

    def 'boolean parameters'() {
        when:
        context.booleanParam('one')
        context.booleanParam('two', true)
        context.booleanParam('three', false)

        then:
        context.configs.size() == 1
        with(context.configs[0]) {
            name() == 'hudson.plugins.parameterizedtrigger.BooleanParameters'
            children().size() == 1
            configs[0].children().size() == 3
            configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[0].children().size() == 2
            configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[0].name.text() == 'one'
            configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[0].value.text() == 'false'
            configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[1].children().size() == 2
            configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[1].name.text() == 'two'
            configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[1].value.text() == 'true'
            configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[2].children().size() == 2
            configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[2].name.text() == 'three'
            configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[2].value.text() == 'false'
        }
    }

    def 'same node'() {
        when:
        context.sameNode()

        then:
        context.configs.size() == 1
        with(context.configs[0]) {
            name() == 'hudson.plugins.parameterizedtrigger.NodeParameters'
            children().size() == 0
        }
    }

    def 'current build'() {
        when:
        context.currentBuild()

        then:
        context.configs.size() == 1
        with(context.configs[0]) {
            name() == 'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'
            children().size() == 0
        }
    }

    def 'node label parameter'() {
        when:
        context.nodeLabel('foo', 'bar')

        then:
        context.configs.size() == 1
        with(context.configs[0]) {
            name() == 'org.jvnet.jenkins.plugins.nodelabelparameter.parameterizedtrigger.NodeLabelBuildParameter'
            children().size() == 2
            name[0].text() == 'foo'
            nodeLabel[0].text() == 'bar'
        }
        jobManagement.requirePlugin('nodelabelparameter')
    }

    def 'properties file'() {
        when:
        context.propertiesFile('foo')

        then:
        context.configs.size() == 1
        with(context.configs[0]) {
            name() == 'hudson.plugins.parameterizedtrigger.FileBuildParameters'
            children().size() == 2
            propertiesFile[0].text() == 'foo'
            failTriggerOnMissing[0].text() == 'false'
        }
    }

    def 'properties file with all arguments'() {
        when:
        context.propertiesFile('foo', value)

        then:
        context.configs.size() == 1
        with(context.configs[0]) {
            name() == 'hudson.plugins.parameterizedtrigger.FileBuildParameters'
            children().size() == 2
            propertiesFile[0].text() == 'foo'
            failTriggerOnMissing[0].value() == value
        }

        where:
        value << [true, false]
    }

    def 'git revision'() {
        when:
        context.gitRevision()

        then:
        context.configs.size() == 1
        with(context.configs[0]) {
            name() == 'hudson.plugins.git.GitRevisionBuildParameters'
            children().size() == 1
            combineQueuedCommits[0].text() == 'false'
        }
        1 * jobManagement.requireMinimumPluginVersion('git', '2.5.3')
    }

    def 'git revision with all arguments'() {
        when:
        context.gitRevision(value)

        then:
        context.configs.size() == 1
        with(context.configs[0]) {
            name() == 'hudson.plugins.git.GitRevisionBuildParameters'
            children().size() == 1
            combineQueuedCommits[0].value() == value
        }
        1 * jobManagement.requireMinimumPluginVersion('git', '2.5.3')

        where:
        value << [true, false]
    }

    def 'predefined properties'() {
        when:
        context.predefinedProp('one', 'two')
        context.predefinedProps(three: 'four', five: 'six')

        then:
        context.configs.size() == 1
        with(context.configs[0]) {
            name() == 'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'
            children().size() == 1
            properties[0].text() == 'one=two\nthree=four\nfive=six'
        }
    }

    def 'matrix subset'() {
        when:
        context.matrixSubset('foo')

        then:
        context.configs.size() == 1
        with(context.configs[0]) {
            name() == 'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters'
            children().size() == 1
            filter[0].text() == 'foo'
        }
    }

    def 'subversion revision'() {
        when:
        context.subversionRevision()

        then:
        context.configs.size() == 1
        with(context.configs[0]) {
            name() == 'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters'
            children().size() == 1
            includeUpstreamParameters[0].text() == 'false'
        }
    }

    def 'subversion revision with all arguments'() {
        when:
        context.subversionRevision(value)

        then:
        context.configs.size() == 1
        with(context.configs[0]) {
            name() == 'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters'
            children().size() == 1
            includeUpstreamParameters[0].value() == value
        }

        where:
        value << [true, false]
    }

    def 'extension point is called'() {
        setup:
        jobManagement.callExtension('foo', item, DownstreamTriggerParameterContext, 'bar') >>
                new Node(null, 'org.example.TestParameters')

        when:
        context.foo('bar')

        then:
        context.configs.size() == 1
        with(context.configs[0]) {
            name() == 'org.example.TestParameters'
            children().size() == 0
        }
    }
}
