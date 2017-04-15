package javaposse.jobdsl.dsl.helpers.step

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
        with(context.stepNodes[0]) {
            name() == 'com.tikal.jenkins.plugins.multijob.MultiJobBuilder'
            children().size() == 4
            phaseName[0].value() == 'First'
            continuationCondition[0].value() == 'SUCCESSFUL'
            executionType[0].value() == 'PARALLEL'
            phaseJobs[0].value().empty
        }

        when:
        context.phase {
            phaseName('Second')
            phaseJob('JobA')
        }

        then:
        with(context.stepNodes[1]) {
            name() == 'com.tikal.jenkins.plugins.multijob.MultiJobBuilder'
            children().size() == 4
            phaseName[0].value() == 'Second'
            continuationCondition[0].value() == 'SUCCESSFUL'
            executionType[0].value() == 'PARALLEL'
            phaseJobs[0].children().size() == 1
            with(phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]) {
                children().size() == 7
                jobName[0].value() == 'JobA'
                currParams[0].value() == true
                exposedSCM[0].value() == true
                configs[0].attribute('class') == 'java.util.Collections$EmptyList'
                disableJob[0].value() == false
                abortAllJob[0].value() == false
                killPhaseOnJobResultCondition[0].value() == 'FAILURE'
            }
        }
    }

    def 'call phases with multiple jobs'() {
        when:
        context.phase('Third') {
            phaseJob('JobA')
            phaseJob('JobB')
            phaseJob('JobC')
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'com.tikal.jenkins.plugins.multijob.MultiJobBuilder'
            children().size() == 4
            phaseName[0].value() == 'Third'
            continuationCondition[0].value() == 'SUCCESSFUL'
            executionType[0].value() == 'PARALLEL'
            phaseJobs[0].children().size() == 3
            phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0].jobName[0].value() == 'JobA'
            phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[1].jobName[0].value() == 'JobB'
            phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[2].jobName[0].value() == 'JobC'
        }
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
        with(context.stepNodes[0]) {
            children().size() == 4
            phaseName[0].value() == 'Fourth'
            continuationCondition[0].value() == 'SUCCESSFUL'
            executionType[0].value() == 'PARALLEL'
            phaseJobs[0].children().size() == 1
            with(phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]) {
                children().size() == 8
                jobName[0].value() == 'JobA'
                currParams[0].value() == false
                exposedSCM[0].value() == true
                disableJob[0].value() == false
                abortAllJob[0].value() == false
                killPhaseOnJobResultCondition[0].value() == 'FAILURE'
                customConfig[0].value() == 'foobar'
                configs[0].children().size() == 8
                with(configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameters'[0]) {
                    children().size() == 1
                    configs[0].children().size() == 3
                    with(configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[0]) {
                        children().size() == 2
                        name[0].value() == 'aParam'
                        value[0].value() == false
                    }
                    with(configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[1]) {
                        children().size() == 2
                        name[0].value() == 'bParam'
                        value[0].value() == false
                    }
                    with(configs[0].'hudson.plugins.parameterizedtrigger.BooleanParameterConfig'[2]) {
                        children().size() == 2
                        name[0].value() == 'cParam'
                        value[0].value() == true
                    }
                }
                with(configs[0].'hudson.plugins.parameterizedtrigger.FileBuildParameters'[0]) {
                    children().size() == 2
                    propertiesFile[0].value() == 'my.properties'
                    failTriggerOnMissing[0].value() == false
                }
                with(configs[0].'hudson.plugins.parameterizedtrigger.NodeParameters'[0]) {
                    children().size() == 0
                }
                with(configs[0].'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters'[0]) {
                    children().size() == 1
                    filter[0].value() == 'it.name=="hello"'
                }
                with(configs[0].'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters'[0]) {
                    children().size() == 1
                    includeUpstreamParameters[0].value() == false
                }
                with(configs[0].'hudson.plugins.git.GitRevisionBuildParameters'[0]) {
                    children().size() == 1
                    combineQueuedCommits[0].value() == false
                }
                with(configs[0].'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'[0]) {
                    children().size() == 1
                    children()[0].name() == 'properties'
                    children()[0].value() == 'prop1=value1\nprop2=value2\nprop3=value3\nprop4=value4'
                }
                with(configs[0].
                       'org.jvnet.jenkins.plugins.nodelabelparameter.parameterizedtrigger.NodeLabelBuildParameter'[0]) {
                    children().size() == 2
                    name[0].value() == 'nodeParam'
                    nodeLabel[0].value() == 'node_label'
                }
            }
        }

        1 * jobManagement.requireMinimumPluginVersion('git', '2.5.3')
        1 * jobManagement.requireMinimumPluginVersion('parameterized-trigger', '2.26')
    }

    def 'call phases with plugin version 1.14 options'() {
        when:
        context.phase {
            phaseName('Second')
            phaseJob('JobA') {
                disableJob()
                abortAllJobs()
                killPhaseCondition('UNSTABLE')
            }
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'com.tikal.jenkins.plugins.multijob.MultiJobBuilder'
            children().size() == 4
            phaseName[0].value() == 'Second'
            continuationCondition[0].value() == 'SUCCESSFUL'
            executionType[0].value() == 'PARALLEL'
            phaseJobs[0].children().size() == 1
            with(phaseJobs[0].'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig'[0]) {
                children().size() == 7
                jobName[0].value() == 'JobA'
                currParams[0].value() == true
                exposedSCM[0].value() == true
                configs[0].attribute('class') == 'java.util.Collections$EmptyList'
                disableJob[0].value() == true
                abortAllJob[0].value() == true
                killPhaseOnJobResultCondition[0].value() == 'UNSTABLE'
            }
        }
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

    def 'call phase with unsupported condition'() {
        when:
        context.phase('test', 'FOO') {
        }

        then:
        thrown(DslScriptException)
    }

    def 'call phase with unsupported execution type'() {
        when:
        context.phase('test') {
            executionType('FOO')
        }

        then:
        thrown(DslScriptException)
    }

    def 'call phase with supported condition'(String condition) {
        when:
        context.phase('test', condition) {
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'com.tikal.jenkins.plugins.multijob.MultiJobBuilder'
            children().size() == 4
            phaseName[0].value() == 'test'
            continuationCondition[0].value() == condition
            executionType[0].value() == 'PARALLEL'
            phaseJobs[0].value().empty
        }

        where:
        condition << ['FAILURE', 'ALWAYS']
    }

    def 'call phase with supported execution type'(String execution) {
        when:
        context.phase('test') {
            executionType(execution)
        }

        then:
        with(context.stepNodes[0]) {
            name() == 'com.tikal.jenkins.plugins.multijob.MultiJobBuilder'
            children().size() == 4
            phaseName[0].value() == 'test'
            continuationCondition[0].value() == 'SUCCESSFUL'
            executionType[0].value() == execution
            phaseJobs[0].value().empty
        }

        where:
        execution << ['PARALLEL', 'SEQUENTIALLY']
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
                        children().size() == 7
                        jobName[0].value() == 'JobA'
                        abortAllJob[0].value() == false
                    }
                }
            }
        }
        1 * jobManagement.requirePlugin('conditional-buildstep')
    }
}
