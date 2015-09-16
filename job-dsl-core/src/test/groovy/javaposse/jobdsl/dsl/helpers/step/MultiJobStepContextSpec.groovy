package javaposse.jobdsl.dsl.helpers.step

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class MultiJobStepContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    MultiJobStepContext context = new MultiJobStepContext(jobManagement, item)

    def 'call phases with minimal arguments'() {
        when:
        context.phase('First')

        then:
        def phaseNode = context.stepNodes[0]
        phaseNode.name() == 'com.tikal.jenkins.plugins.multijob.MultiJobBuilder'
        phaseNode.phaseName[0].value() == 'First'
        phaseNode.continuationCondition[0].value() == 'SUCCESSFUL'

        when:
        context.phase {
            phaseName('Second')
            phaseJob('JobA')
        }

        then:
        def phaseNode2 = context.stepNodes[1]
        phaseNode2.phaseName[0].value() == 'Second'
        def jobNode = phaseNode2.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNode.children().size() == 4
        jobNode.jobName[0].value() == 'JobA'
        jobNode.currParams[0].value() == true
        jobNode.exposedSCM[0].value() == true
        jobNode.configs[0].attribute('class') == 'java.util.Collections$EmptyList'
    }

    def 'call phases with minimal arguments and plugin version 1.11'() {
        setup:
        jobManagement.getPluginVersion('jenkins-multijob-plugin') >> new VersionNumber('1.11')

        when:
        context.phase {
            phaseName('Second')
            phaseJob('JobA')
        }

        then:
        def phaseNode = context.stepNodes[0]
        phaseNode.phaseName[0].value() == 'Second'
        def jobNode = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNode.children().size() == 6
        jobNode.jobName[0].value() == 'JobA'
        jobNode.currParams[0].value() == true
        jobNode.exposedSCM[0].value() == true
        jobNode.configs[0].attribute('class') == 'java.util.Collections$EmptyList'
        jobNode.disableJob[0].value() == false
        jobNode.killPhaseOnJobResultCondition[0].value() == 'FAILURE'
    }

    def 'call phases with multiple jobs'() {
        when:
        context.phase('Third') {
            job('JobA')
            job('JobB')
            job('JobC')
        }

        then:
        def phaseNode = context.stepNodes[0]
        def jobNodeA = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNodeA.jobName[0].value() == 'JobA'
        def jobNodeB = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[1]
        jobNodeB.jobName[0].value() == 'JobB'
        def jobNodeC = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[2]
        jobNodeC.jobName[0].value() == 'JobC'
    }

    def 'call phases with jobs with complex parameters using deprecated methods'() {
        when:
        context.phase('Fourth') {
            job('JobA', false, true) {
                boolParam('aParam')
                boolParam('bParam', false)
                boolParam('cParam', true)
                fileParam('my.properties')
                sameNode()
                matrixParam('it.name=="hello"')
                subversionRevision()
                gitRevision()
                prop('prop1', 'value1')
                prop('prop2', 'value2')
                props([
                        prop3: 'value3',
                        prop4: 'value4'
                ])
                nodeLabel('nodeParam', 'node_label')
                configure { phaseJobConfig ->
                    phaseJobConfig / customConfig << 'foobar'
                }
            }
        }

        then:
        def phaseNode = context.stepNodes[0]
        def jobNode = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNode.currParams[0].value() == false
        jobNode.exposedSCM[0].value() == true

        def customConfigNode = jobNode.customConfig[0]
        customConfigNode.value() == 'foobar'

        def configsNode = jobNode.configs[0]
        def boolParams = configsNode.'hudson.plugins.parameterizedtrigger.BooleanParameters'[0].configs[0]
        boolParams.children().size() == 3
        def boolNode = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[0]
        boolNode.name[0].value() == 'aParam'
        boolNode.value[0].value() == false
        def boolNode1 = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[1]
        boolNode1.name[0].value() == 'bParam'
        boolNode1.value[0].value() == false
        def boolNode2 = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[2]
        boolNode2.name[0].value() == 'cParam'
        boolNode2.value[0].value() == true

        def fileNode = configsNode.'hudson.plugins.parameterizedtrigger.FileBuildParameters'[0]
        fileNode.propertiesFile[0].value() == 'my.properties'
        fileNode.failTriggerOnMissing[0].value() == false

        def nodeNode = configsNode.'hudson.plugins.parameterizedtrigger.NodeParameters'[0]
        nodeNode != null

        def matrixNode = configsNode.'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters'[0]
        matrixNode.filter[0].value() == 'it.name=="hello"'

        def svnNode = configsNode.'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters'[0]
        svnNode.includeUpstreamParameters[0].value() == false

        def gitNode = configsNode.'hudson.plugins.git.GitRevisionBuildParameters'[0]
        gitNode.combineQueuedCommits[0].value() == false

        def propNode = configsNode.'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'[0]
        def propStr = propNode.'properties'[0].value()
        propStr.contains('prop1=value1')
        propStr.contains('prop2=value2')
        propStr.contains('prop3=value3')
        propStr.contains('prop4=value4')

        def nodeLabel = configsNode.
            'org.jvnet.jenkins.plugins.nodelabelparameter.parameterizedtrigger.NodeLabelBuildParameter'[0]
        nodeLabel.name[0].value() == 'nodeParam'
        nodeLabel.nodeLabel[0].value() == 'node_label'

        1 * jobManagement.logPluginDeprecationWarning('git', '2.2.6')
    }

    def 'call phases with jobs with complex parameters'() {
        when:
        context.phase('Fourth') {
            phaseJob('JobA') {
                currentJobParameters(false)
                parameters {
                    booleanParam('aParam')
                    booleanParam('bParam', false)
                    booleanParam('cParam', true)
                    propertiesFile('my.properties')
                    sameNode()
                    matrixSubset('it.name=="hello"')
                    subversionRevision()
                    gitRevision()
                    predefinedProp('prop1', 'value1')
                    predefinedProp('prop2', 'value2')
                    predefinedProps([
                            prop3: 'value3',
                            prop4: 'value4'
                    ])
                    nodeLabel('nodeParam', 'node_label')
                }
                configure { phaseJobConfig ->
                    phaseJobConfig / customConfig << 'foobar'
                }
            }
        }

        then:
        def phaseNode = context.stepNodes[0]
        def jobNode = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNode.currParams[0].value() == false
        jobNode.exposedSCM[0].value() == true

        def customConfigNode = jobNode.customConfig[0]
        customConfigNode.value() == 'foobar'

        def configsNode = jobNode.configs[0]
        def boolParams = configsNode.'hudson.plugins.parameterizedtrigger.BooleanParameters'[0].configs[0]
        boolParams.children().size() == 3
        def boolNode = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[0]
        boolNode.name[0].value() == 'aParam'
        boolNode.value[0].value() == false
        def boolNode1 = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[1]
        boolNode1.name[0].value() == 'bParam'
        boolNode1.value[0].value() == false
        def boolNode2 = boolParams.'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[2]
        boolNode2.name[0].value() == 'cParam'
        boolNode2.value[0].value() == true

        def fileNode = configsNode.'hudson.plugins.parameterizedtrigger.FileBuildParameters'[0]
        fileNode.propertiesFile[0].value() == 'my.properties'
        fileNode.failTriggerOnMissing[0].value() == false

        def nodeNode = configsNode.'hudson.plugins.parameterizedtrigger.NodeParameters'[0]
        nodeNode != null

        def matrixNode = configsNode.'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters'[0]
        matrixNode.filter[0].value() == 'it.name=="hello"'

        def svnNode = configsNode.'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters'[0]
        svnNode.includeUpstreamParameters[0].value() == false

        def gitNode = configsNode.'hudson.plugins.git.GitRevisionBuildParameters'[0]
        gitNode.combineQueuedCommits[0].value() == false

        def propNode = configsNode.'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'[0]
        def propStr = propNode.'properties'[0].value()
        propStr.contains('prop1=value1')
        propStr.contains('prop2=value2')
        propStr.contains('prop3=value3')
        propStr.contains('prop4=value4')

        def nodeLabel = configsNode.
        'org.jvnet.jenkins.plugins.nodelabelparameter.parameterizedtrigger.NodeLabelBuildParameter'[0]
        nodeLabel.name[0].value() == 'nodeParam'
        nodeLabel.nodeLabel[0].value() == 'node_label'

        1 * jobManagement.logPluginDeprecationWarning('git', '2.2.6')
        1 * jobManagement.requirePlugin('parameterized-trigger')
        1 * jobManagement.logPluginDeprecationWarning('parameterized-trigger', '2.26')
    }

    def 'call phases with plugin version 1.11 options'() {
        setup:
        jobManagement.getPluginVersion('jenkins-multijob-plugin') >> new VersionNumber('1.11')

        when:
        context.phase {
            phaseName 'Second'
            phaseJob('JobA') {
                disableJob()
                abortAllJobs()
                killPhaseCondition('UNSTABLE')
            }
        }

        then:
        def phaseNode = context.stepNodes[0]
        phaseNode.phaseName[0].value() == 'Second'
        def jobNode = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNode.children().size() == 6
        jobNode.jobName[0].value() == 'JobA'
        jobNode.currParams[0].value() == true
        jobNode.exposedSCM[0].value() == true
        jobNode.configs[0].attribute('class') == 'java.util.Collections$EmptyList'
        jobNode.disableJob[0].value() == true
        jobNode.killPhaseOnJobResultCondition[0].value() == 'UNSTABLE'
    }

    def 'call phases with plugin version 1.14 options'() {
        setup:
        jobManagement.getPluginVersion('jenkins-multijob-plugin') >> new VersionNumber('1.14')

        when:
        context.phase {
            phaseName 'Second'
            phaseJob('JobA') {
                disableJob()
                abortAllJobs()
                killPhaseCondition('UNSTABLE')
            }
        }

        then:
        def phaseNode = context.stepNodes[0]
        def jobNode = phaseNode.phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]
        jobNode.children().size() == 7
        jobNode.abortAllJob[0].value() == true
        jobNode.disableJob[0].value() == true
    }

    def 'call killPhaseCondition with invalid argument'() {
        when:
        context.phase {
            phaseName 'Second'
            phaseJob('JobA') {
                killPhaseCondition('UNKNOWN')
            }
        }

        then:
        thrown(DslScriptException)
    }

    def 'call phase with unsupported condition'(String condition, String version) {
        setup:
        jobManagement.getPluginVersion('jenkins-multijob-plugin') >> new VersionNumber(version)

        when:
        context.phase('test', condition) {
        }

        then:
        thrown(DslScriptException)

        where:
        condition | version
        'FAILURE' | '1.10'
        'ALWAYS'  | '1.15'
    }

    def 'call phase with supported condition'(String condition, String version) {
        setup:
        jobManagement.getPluginVersion('jenkins-multijob-plugin') >> new VersionNumber(version)

        when:
        context.phase('test', condition) {
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'com.tikal.jenkins.plugins.multijob.MultiJobBuilder'
            children().size() == 3
            phaseName[0].value() == 'test'
            continuationCondition[0].value() == condition
        }

        where:
        condition | version
        'FAILURE' | '1.11'
        'ALWAYS'  | '1.16'
    }

    def 'phase works inside conditionalSteps'() {
        when:
        context.conditionalSteps {
            condition {
                alwaysRun()
            }
            runner('Fail')
            steps {
                phase {
                    phaseName('Second')
                    phaseJob('JobA')
                }
            }
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'org.jenkinsci.plugins.conditionalbuildstep.ConditionalBuilder'
            children().size() == 3
            runCondition[0].children().size() == 0
            runCondition[0] != null
            runner[0].attribute('class') == 'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail'
            with(conditionalbuilders[0]) {
                children().size() == 1
                with(children()[0]) {
                    name() == 'com.tikal.jenkins.plugins.multijob.MultiJobBuilder'
                    with(phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]) {
                        children().size() == 4
                        jobName[0].value() == 'JobA'
                    }
                }
            }
        }
        1 * jobManagement.requirePlugin('conditional-buildstep')
    }
}
