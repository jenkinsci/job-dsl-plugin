package javaposse.jobdsl.dsl

import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

class ProjectSpec extends Specification {
    private final File resourcesDir = new File(getClass().getResource('/simple.dsl').toURI()).parentFile
    private final JobManagement jobManagement = Mock(JobManagement)
    private Project job = new TestProject(jobManagement)

    def setup() {
        XMLUnit.ignoreWhitespace = true
    }

    def 'run engine and ensure canRoam values'() {
        setup:
        JobManagement jm = new FileJobManagement(resourcesDir)
        Project job = new TestProject(jm)

        when:
        job.label('Ubuntu')

        then:
        job.node.canRoam[0].value() == false
    }

    def 'call scm'() {
        when:
        job.scm {
            git {
            }
        }

        then:
        job.node.scm[0].configVersion[0].text() == '2'
    }

    def 'duplicate scm calls allowed with multiscm'() {
        when:
        job.multiscm {
            git('git://github.com/jenkinsci/jenkins.git')
            git('git://github.com/jenkinsci/job-dsl-plugin.git')
        }

        then:
        noExceptionThrown()
        job.node.scm[0].scms[0].scm.size() == 2
        1 * jobManagement.requirePlugin('multiple-scms')
    }

    def 'duplicate scm calls not allowed'() {
        when:
        job.scm {
            git('git://github.com/jenkinsci/jenkins.git')
            git('git://github.com/jenkinsci/job-dsl-plugin.git')
        }

        then:
        thrown(DslScriptException)
    }

    def 'call wrappers'() {
        when:
        job.wrappers {
            maskPasswords()
        }

        then:
        job.node.buildWrappers[0].children()[0].name() ==
            'com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsBuildWrapper'
    }

    def 'call steps'() {
        when:
        job.steps {
            shell('ls')
        }

        then:
        job.node.builders[0].'hudson.tasks.Shell'[0].command[0].text() == 'ls'
    }

    def 'call publishers'() {
        when:
        job.publishers {
            chucknorris()
        }

        then:
        job.node.publishers[0].'hudson.plugins.chucknorris.CordellWalkerRecorder'[0].factGenerator[0].text() == ''
    }

    def 'label constructs xml'() {
        when:
        job.label('FullTools')

        then:
        job.node.assignedNode[0].value() == 'FullTools'
        job.node.canRoam[0].value() == false
    }

    def 'without label leaves canRoam as true'() {
        when:
        job.label()

        then:
        job.node.assignedNode[0].value() == ''
        job.node.canRoam[0].value() == true
    }

    def 'authenticationToken constructs xml'() {
        when:
        job.authenticationToken('secret')

        then:
        job.node.authToken[0].value() == 'secret'
    }

    def 'lockable resources simple'() {
        when:
        job.lockableResources('lock-resource')

        then:
        with(job.node.properties[0].'org.jenkins.plugins.lockableresources.RequiredResourcesProperty'[0]) {
            children().size() == 1
            resourceNames[0].value() == 'lock-resource'
        }
        1 * jobManagement.requireMinimumPluginVersion('lockable-resources', '1.7')
    }

    def 'lockable resources with all parameters'() {
        when:
        job.lockableResources('res0 res1 res2') {
            resourcesVariable('RESOURCES')
            resourceNumber(1)
            label('foo')
        }

        then:
        with(job.node.properties[0].'org.jenkins.plugins.lockableresources.RequiredResourcesProperty'[0]) {
            children().size() == 4
            resourceNames[0].value() == 'res0 res1 res2'
            resourceNamesVar[0].value() == 'RESOURCES'
            resourceNumber[0].value() == 1
            labelName[0].value() == 'foo'
        }
        1 * jobManagement.requireMinimumPluginVersion('lockable-resources', '1.7')
    }

    def 'lockable resources with label only'() {
        when:
        job.lockableResources {
            label('HEAVY_RESOURCE')
        }

        then:
        with(job.node.properties[0].'org.jenkins.plugins.lockableresources.RequiredResourcesProperty'[0]) {
            children().size() == 1
            labelName[0].value() == 'HEAVY_RESOURCE'
        }
        2 * jobManagement.requireMinimumPluginVersion('lockable-resources', '1.7')
    }

    def 'lockable resources resource or label have to be defined'() {
        when:
        job.lockableResources {
        }

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ /Either resource or label have to be specified/
    }

    def 'heavy job weight'() {
        when:
        job.weight(42)

        then:
        with(job.node.properties[0].'hudson.plugins.heavy__job.HeavyJobProperty'[0]) {
            children().size() == 1
            weight[0].value() == 42
        }
        1 * jobManagement.requireMinimumPluginVersion('heavy-job', '1.1')
    }

    def 'can run jdk'() {
        when:
        job.jdk('JDK1.8.0_212')

        then:
        job.node.jdk[0].value() == 'JDK1.8.0_212'
    }

    def 'can run jdk twice'() {
        when:
        job.jdk('JDK1.8.0_211')

        then:
        job.node.jdk[0].value() == 'JDK1.8.0_211'

        when:
        job.jdk('JDK1.8.0_212')

        then:
        job.node.jdk.size() == 1
        job.node.jdk[0].value() == 'JDK1.8.0_212'
    }

    def 'add SCM retry count'() {
        when:
        job.checkoutRetryCount()

        then:
        job.node.scmCheckoutRetryCount[0].value() == 3

        when:
        job.checkoutRetryCount(6)

        then:
        job.node.scmCheckoutRetryCount[0].value() == 6
    }

    def 'add custom workspace'() {
        when:
        job.customWorkspace('/var/lib/jenkins/foobar')

        then:
        job.node.customWorkspace[0].value() == '/var/lib/jenkins/foobar'
    }

    def 'add block for up and downstream projects'() {
        when:
        job.blockOnUpstreamProjects()

        then:
        job.node.blockBuildWhenUpstreamBuilding[0].value() == true

        when:
        job.blockOnDownstreamProjects()

        then:
        job.node.blockBuildWhenDownstreamBuilding[0].value() == true
    }

    def 'set concurrentBuild with value'(allowConcurrentBuild) {
        when:
        job.concurrentBuild(allowConcurrentBuild)

        then:
        job.node.concurrentBuild.size() == 1
        job.node.concurrentBuild[0].value() == allowConcurrentBuild ? 'true' : 'false'

        where:
        allowConcurrentBuild << [true, false]
    }

    def 'set concurrentBuild default'() {
        when:
        job.concurrentBuild()

        then:
        job.node.concurrentBuild[0].value() == true
    }

    def 'add batch task'() {
        when:
        job.batchTask('Hello World', 'echo Hello World')

        then:
        job.node.'properties'.size() == 1
        job.node.'properties'[0].'hudson.plugins.batch__task.BatchTaskProperty'.size() == 1
        with(job.node.'properties'[0].'hudson.plugins.batch__task.BatchTaskProperty'[0]) {
            tasks.size() == 1
            tasks[0].'hudson.plugins.batch__task.BatchTask'.size() == 1
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].children().size() == 2
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].'name'[0].value() == 'Hello World'
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].'script'[0].value() == 'echo Hello World'
        }
        1 * jobManagement.requirePlugin('batch-task')
    }

    def 'add two batch tasks'() {
        when:
        job.batchTask('Hello World', 'echo Hello World')
        job.batchTask('foo', 'echo bar')

        then:
        job.node.'properties'.size() == 1
        job.node.'properties'[0].'hudson.plugins.batch__task.BatchTaskProperty'.size() == 1
        with(job.node.'properties'[0].'hudson.plugins.batch__task.BatchTaskProperty'[0]) {
            tasks.size() == 1
            tasks[0].'hudson.plugins.batch__task.BatchTask'.size() == 2
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].children().size() == 2
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].'name'[0].value() == 'Hello World'
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].'script'[0].value() == 'echo Hello World'
            tasks[0].'hudson.plugins.batch__task.BatchTask'[1].children().size() == 2
            tasks[0].'hudson.plugins.batch__task.BatchTask'[1].'name'[0].value() == 'foo'
            tasks[0].'hudson.plugins.batch__task.BatchTask'[1].'script'[0].value() == 'echo bar'
        }
        2 * jobManagement.requirePlugin('batch-task')
    }
}
