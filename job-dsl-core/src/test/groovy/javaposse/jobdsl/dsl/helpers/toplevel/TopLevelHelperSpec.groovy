package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification
import spock.lang.Unroll

class TopLevelHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    TopLevelHelper helper = new TopLevelHelper(mockActions, JobType.Freeform)
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.XML))

    def 'add description'() {
        when:
        def action = helper.description('Description')
        action.execute(root)

        then:
        root.description[0].value() == 'Description'

        when:
        def action2 = helper.description('Description2')
        action2.execute(root)

        then:
        root.description.size() == 1
        root.description[0].value() == 'Description2'

    }

    def 'environments work with map arg'() {
        when:
        def action = helper.environmentVariables([
                key1: 'val1',
                key2: 'val2'
        ])
        action.execute(root)

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
    }

    def 'environments work with context'() {
        when:
        def action = helper.environmentVariables {
            envs([key1: 'val1', key2: 'val2'])
            env 'key3', 'val3'
        }
        action.execute(root)

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
    }

    def 'environments work with combination'() {
        when:
        def action = helper.environmentVariables([key4: 'val4']) {
            env 'key3', 'val3'
        }
        action.execute(root)

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key4=val4')
    }

    def 'environment from groovy script'() {
        when:
        def action = helper.environmentVariables {
            groovy '[foo: "bar"]'
        }
        action.execute(root)

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].groovyScriptContent[0].value() == '[foo: "bar"]'
    }

    def 'environment from map and groovy script'() {
        when:
        def action = helper.environmentVariables {
            envs([key1: 'val1', key2: 'val2'])
            env 'key3', 'val3'
            groovy '[foo: "bar"]'
        }
        action.execute(root)

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
        root.properties[0].'EnvInjectJobProperty'[0].info[0].groovyScriptContent[0].value() == '[foo: "bar"]'
    }

    @Unroll
    def 'environment from #method'(content, method, xmlElement) {
        when:
        def action = helper.environmentVariables {
            "$method"(content)
        }
        action.execute(root)

        then:
        root.properties[0].'EnvInjectJobProperty'[0].info[0]."$xmlElement"[0].value() == content

        where:
        content          || method               || xmlElement
        'some.properties' | 'propertiesFile'      | 'propertiesFilePath'
        '/some/path'      | 'scriptFile'          | 'scriptFilePath'
        'echo "Yeah"'     | 'script'              | 'scriptContent'
        true              | 'loadFilesFromMaster' | 'loadFilesFromMaster'
    }

    @Unroll
    def 'environment sets #method to #content'(method, content, xmlElement) {
        when:
        def action = helper.environmentVariables {
            "${method}"(content)
        }
        action.execute(root)

        then:
        root.properties[0].'EnvInjectJobProperty'[0]."${xmlElement}"[0].value() == content

        where:
        method               || content || xmlElement
        'keepSystemVariables' | true     | 'keepJenkinsSystemVariables'
        'keepSystemVariables' | false    | 'keepJenkinsSystemVariables'
        'keepBuildVariables'  | true     | 'keepBuildVariables'
        'keepBuildVariables'  | false    | 'keepBuildVariables'
    }

    def 'throttle concurrents enabled as project alone'() {
        when:
        def action = helper.throttleConcurrentBuilds {
            maxPerNode 1
            maxTotal 2
        }
        action.execute(root)

        then:
        def throttleNode = root.properties[0].'hudson.plugins.throttleconcurrents.ThrottleJobProperty'[0]

        throttleNode.maxConcurrentPerNode[0].value() == 1
        throttleNode.maxConcurrentTotal[0].value() == 2
        throttleNode.throttleEnabled[0].value() == 'true'
        throttleNode.throttleOption[0].value() == 'project'
        throttleNode.categories[0].children().size() == 0
    }

    def 'throttle concurrents disabled'() {
        when:
        def action = helper.throttleConcurrentBuilds {
            throttleDisabled()
        }
        action.execute(root)

        then:
        def throttleNode = root.properties[0].'hudson.plugins.throttleconcurrents.ThrottleJobProperty'[0]

        throttleNode.throttleEnabled[0].value() == 'false'
    }

    def 'throttle concurrents enabled as part of categories'() {
        when:
        def action = helper.throttleConcurrentBuilds {
            maxPerNode 1
            maxTotal 2
            categories(['cat-1', 'cat-2'])
        }
        action.execute(root)

        then:
        def throttleNode = root.properties[0].'hudson.plugins.throttleconcurrents.ThrottleJobProperty'[0]

        throttleNode.maxConcurrentPerNode[0].value() == 1
        throttleNode.maxConcurrentTotal[0].value() == 2
        throttleNode.throttleEnabled[0].value() == 'true'
        throttleNode.throttleOption[0].value() == 'category'
        throttleNode.categories[0].children().size() == 2
        throttleNode.categories[0].string[0].value() == 'cat-1'
        throttleNode.categories[0].string[1].value() == 'cat-2'
    }

    def 'can run label'() {
        when:
        helper.label('RPM')

        then:
        1 * mockActions.add(_)
    }

    def 'disable defaults to true'() {
        when:
        helper.disabled().execute(root)

        then:
        root.disabled.size() == 1
        root.disabled[0].value() == 'true'

        when:
        helper.disabled(false).execute(root)

        then:
        root.disabled.size() == 1
        root.disabled[0].value() == 'false'
    }

    def 'label constructs xml'() {
        when:
        def action = helper.label('FullTools')
        action.execute(root)

        then:
        root.assignedNode[0].value() == 'FullTools'
        root.canRoam[0].value() == 'false'
    }

    def 'without label leaves canRoam as true'() {
        when:
        when:
        def action = helper.label()
        action.execute(root)

        then:
        root.assignedNode[0].value() == ''
        root.canRoam[0].value() == 'true'
    }

    def 'lockable resources simple'() {
        when:
        def action = helper.lockableResources('lock-resource')
        action.execute(root)

        then:
        with(root.properties[0].'org.jenkins.plugins.lockableresources.RequiredResourcesProperty'[0]) {
            children().size() == 1
            resourceNames.size() == 1
            resourceNamesVar.size() == 0
            resourceNumber.size() == 0
            resourceNames[0].value() == 'lock-resource'
        }
    }

    def 'lockable resources with all parameters'() {
        when:
        def action = helper.lockableResources('res0 res1 res2') {
            resourcesVariable('RESOURCES')
            resourceNumber(1)
        }
        action.execute(root)

        then:
        with(root.properties[0].'org.jenkins.plugins.lockableresources.RequiredResourcesProperty'[0]) {
            children().size() == 3
            resourceNames.size() == 1
            resourceNamesVar.size() == 1
            resourceNumber.size() == 1
            resourceNames[0].value() == 'res0 res1 res2'
            resourceNamesVar[0].value() == 'RESOURCES'
            resourceNumber[0].value() == 1
        }
    }

    def 'log rotate xml'() {
        when:
        def action = helper.logRotator(14, 50)
        action.execute(root)

        then:
        root.logRotator[0].daysToKeep[0].value() == '14'
        root.logRotator[0].numToKeep[0].value() == '50'
    }

    def 'build blocker xml'() {
        when:
        def action = helper.blockOn('MyProject')
        action.execute(root)

        then:
        root.properties[0].'hudson.plugins.buildblocker.BuildBlockerProperty'[0].useBuildBlocker[0].value() == 'true'
        root.properties[0].'hudson.plugins.buildblocker.BuildBlockerProperty'[0].blockingJobs[0].value() == 'MyProject'
    }

    def 'can run jdk'() {
        when:
        def action = helper.jdk('JDK1.6.0_32')
        action.execute(root)

        then:
        root.jdk[0].value() == 'JDK1.6.0_32'
    }

    def 'can run jdk twice'() {
        when:
        helper.jdk('JDK1.6.0_16').execute(root)

        then:
        root.jdk[0].value() == 'JDK1.6.0_16'

        when:
        helper.jdk('JDK1.6.0_17').execute(root)

        then:
        root.jdk.size() == 1
        root.jdk[0].value() == 'JDK1.6.0_17'
    }

    def 'priority constructs xml'() {
        when:
        def action = helper.priority(99)
        action.execute(root)

        then:
        root.properties.'hudson.queueSorter.PrioritySorterJobProperty'.priority[0].value() == 99
    }

    def 'add a quiet period'() {
        when:
        def action = helper.quietPeriod()
        action.execute(root)

        then:
        root.quietPeriod[0].value() == 5

        when:
        action = helper.quietPeriod(10)
        action.execute(root)

        then:
        root.quietPeriod[0].value() == 10
    }

    def 'add SCM retry count' () {
        when:
        def action = helper.checkoutRetryCount()
        action.execute(root)

        then:
        root.scmCheckoutRetryCount[0].value() == 3

        when:
        action = helper.checkoutRetryCount(6)
        action.execute(root)

        then:
        root.scmCheckoutRetryCount[0].value() == 6
    }

    def 'add display name' () {
        when:
        def action = helper.displayName('FooBar')
        action.execute(root)

        then:
        root.displayName[0].value() == 'FooBar'
    }

    def 'add custom workspace' () {
        when:
        def action = helper.customWorkspace('/var/lib/jenkins/foobar')
        action.execute(root)

        then:
        root.customWorkspace[0].value() == '/var/lib/jenkins/foobar'
    }

    def 'add block for up and downstream projects' () {
        when:
        def action = helper.blockOnUpstreamProjects()
        action.execute(root)

        then:
        root.blockBuildWhenUpstreamBuilding[0].value() == true

        when:
        action = helper.blockOnDownstreamProjects()
        action.execute(root)

        then:
        root.blockBuildWhenDownstreamBuilding[0].value() == true
    }

    def 'set keep Dependencies'(keep) {
        when:
        def action = helper.keepDependencies(keep)
        action.execute(root)

        then:
        root.keepDependencies.size() == 1
        root.keepDependencies[0].value() == keep

        where:
        keep << [true, false]
    }

    def 'set concurrentBuild with value'(allowConcurrentBuild) {
        when:
        def action = helper.concurrentBuild(allowConcurrentBuild)
        action.execute(root)

        then:
        root.concurrentBuild.size() == 1
        root.concurrentBuild[0].value() == allowConcurrentBuild ? 'true' : 'false'

        where:
        allowConcurrentBuild << [true, false]
    }

    def 'set concurrentBuild default'() {
        when:
        def action = helper.concurrentBuild()
        action.execute(root)

        then:
        root.concurrentBuild[0].value() == 'true'
    }

    def 'add batch task'() {
        when:
        def action = helper.batchTask('Hello World', 'echo Hello World')
        action.execute(root)

        then:
        root.'properties'.size() == 1
        root.'properties'[0].'hudson.plugins.batch__task.BatchTaskProperty'.size() == 1
        with(root.'properties'[0].'hudson.plugins.batch__task.BatchTaskProperty'[0]) {
            tasks.size() == 1
            tasks[0].'hudson.plugins.batch__task.BatchTask'.size() == 1
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].children().size() == 2
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].'name'[0].value() == 'Hello World'
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].'script'[0].value() == 'echo Hello World'
        }
    }

    def 'add two batch tasks'() {
        when:
        def action = helper.batchTask('Hello World', 'echo Hello World')
        action.execute(root)
        action = helper.batchTask('foo', 'echo bar')
        action.execute(root)
        new XmlNodePrinter().print(root)

        then:
        root.'properties'.size() == 1
        root.'properties'[0].'hudson.plugins.batch__task.BatchTaskProperty'.size() == 1
        with(root.'properties'[0].'hudson.plugins.batch__task.BatchTaskProperty'[0]) {
            tasks.size() == 1
            tasks[0].'hudson.plugins.batch__task.BatchTask'.size() == 2
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].children().size() == 2
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].'name'[0].value() == 'Hello World'
            tasks[0].'hudson.plugins.batch__task.BatchTask'[0].'script'[0].value() == 'echo Hello World'
            tasks[0].'hudson.plugins.batch__task.BatchTask'[1].children().size() == 2
            tasks[0].'hudson.plugins.batch__task.BatchTask'[1].'name'[0].value() == 'foo'
            tasks[0].'hudson.plugins.batch__task.BatchTask'[1].'script'[0].value() == 'echo bar'
        }
    }

    def 'delivery pipeline configuration with stage and task names'() {
        when:
        def action = helper.deliveryPipelineConfiguration('qa', 'integration-tests')
        action.execute(root)

        then:
        root.'properties'.size() == 1
        root.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'.size() == 1
        with(root.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'[0]) {
            children().size() == 2
            taskName[0].value() == 'integration-tests'
            stageName[0].value() == 'qa'
        }
    }

    def 'delivery pipeline configuration with stage name'() {
        when:
        def action = helper.deliveryPipelineConfiguration('qa')
        action.execute(root)

        then:
        root.'properties'.size() == 1
        root.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'.size() == 1
        with(root.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'[0]) {
            children().size() == 1
            stageName[0].value() == 'qa'
        }
    }

    def 'delivery pipeline configuration with task name'() {
        when:
        def action = helper.deliveryPipelineConfiguration(null, 'integration-tests')
        action.execute(root)

        then:
        root.'properties'.size() == 1
        root.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'.size() == 1
        with(root.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'[0]) {
            children().size() == 1
            taskName[0].value() == 'integration-tests'
        }
    }

    def 'set notification with default properties'() {
        when:
        def action = helper.notification {
            endpoint("http://endpoint.com")
        }

        action.execute(root)

        then:
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 1
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint.com'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'HTTP'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'JSON'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].event[0].text() == 'all'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].timeout[0].value() == 30000
    }

    def 'set notification with all required properties'() {
        when:
        def action = helper.notification {
            endpoint("http://endpoint.com", "TCP", "XML")
        }

        action.execute(root)

        then:
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 1
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint.com'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'TCP'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'XML'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].event[0].text() == 'all'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].timeout[0].value() == 30000
    }

    def 'set notification with default properties and using a closure'() {
        when:
        def action = helper.notification {
            endpoint("http://endpoint.com") {
                event("started")
                timeout(10000)
            }
        }

        action.execute(root)

        then:
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 1
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint.com'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'HTTP'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'JSON'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].event[0].text() == 'started'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].timeout[0].value() == 10000
    }

    def 'set notification with all required properties and using a closure'() {
        when:
        def action = helper.notification {
            endpoint("http://endpoint.com", "TCP", "XML") {
                event("started")
                timeout(10000)
            }
        }

        action.execute(root)

        then:
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 1
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint.com'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'TCP'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'XML'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].event[0].text() == 'started'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].timeout[0].value() == 10000
    }

    def 'set notification with multiple endpoints'() {
        when:
        def action = helper.notification {
            endpoint("http://endpoint1.com")
            endpoint("http://endpoint2.com")
        }

        action.execute(root)

        then:
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 2

        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint1.com'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'HTTP'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'JSON'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].event[0].text() == 'all'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].timeout[0].value() == 30000

        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].url[0].text() == 'http://endpoint2.com'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].protocol[0].text() == 'HTTP'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].format[0].text() == 'JSON'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].event[0].text() == 'all'
        root.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty'.endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].timeout[0].value() == 30000
    }
}
