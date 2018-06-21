package javaposse.jobdsl.dsl

import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.atomic.AtomicBoolean

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class JobSpec extends Specification {
    private final File resourcesDir = new File(getClass().getResource('/simple.dsl').toURI()).parentFile
    private final JobManagement jobManagement = Mock(JobManagement)
    private Job job = new TestJob(jobManagement)

    def setup() {
        XMLUnit.ignoreWhitespace = true
    }

    def 'load an empty template from a manually constructed job and generate xml from it'() {
        when:
        job.using('TMPL')
        def xml = job.xml

        then:
        1 * jobManagement.getConfig('TMPL') >> '<test/>'
        assertXMLEqual '<test/>', xml
    }

    def 'load large template from file'() {
        setup:
        JobManagement jm = new FileJobManagement(resourcesDir)
        TestJob job = new TestJob(jm)

        when:
        job.using('config')
        String project = job.xml

        then:
        project.contains('<description>Description</description>')
    }

    def 'generate job from missing template - throws JobTemplateMissingException'() {
        setup:
        JobManagement jm = new FileJobManagement(resourcesDir)
        TestJob job = new TestJob(jm)

        when:
        job.using('TMPL-NOT_THERE')
        job.xml

        then:
        thrown(JobTemplateMissingException)
    }

    def 'template type mismatch'() {
        when:
        TestJob job = new TestJob(jobManagement)
        job.using('TMPL')
        job.xml

        then:
        1 * jobManagement.getConfig('TMPL') >> '<other/>'
        thrown(JobTypeMismatchException)
    }

    def 'update Node using withXml'() {
        setup:
        TestJob job = new TestJob(null)
        AtomicBoolean boolOutside = new AtomicBoolean(true)

        when: 'Simple update'
        job.configure { Node node ->
            node.foo[0].value = 'Test'
        }
        Node project = job.node

        then:
        project.foo[0].text() == 'Test'

        when: 'Update using variable from outside scope'
        job.configure { Node node ->
            node.bar[0].value = boolOutside.get()
        }
        project = job.node

        then:
        project.bar[0].text() == 'true'

        then:
        project.foo[0].text() == 'Test'

        when: 'Change value on outside scope variable to ensure closure is being run again'
        boolOutside.set(false)
        project = job.node

        then:
        project.bar[0].text() == 'false'

        when: 'Append node'
        job.configure { Node node ->
            def baz = node.baz[0]
            baz.appendNode('some', 'value')
        }
        project = job.node

        then:
        project.baz[0].children().size() == 1

        when: 'Execute withXmlActions again'
        project = job.node

        then:
        project.baz[0].children().size() == 1
    }

    def 'call authorization'() {
        setup:
        jobManagement.getPermissions('hudson.security.AuthorizationMatrixProperty') >> [
                'hudson.model.Item.Configure',
                'hudson.model.Item.Read',
                'hudson.model.Run.Update',
        ]

        when:
        job.authorization {
            permission('hudson.model.Item.Configure:jill')
            permission('hudson.model.Item.Configure:jack')
            permission('hudson.model.Run.Update', 'joe')
        }

        then:
        with(job.node.properties[0].'hudson.security.AuthorizationMatrixProperty'[0]) {
            children().size() == 4
            blocksInheritance[0].value() == false
            permission.size() == 3
            permission[0].text() == 'hudson.model.Item.Configure:jill'
            permission[1].text() == 'hudson.model.Item.Configure:jack'
            permission[2].text() == 'hudson.model.Run.Update:joe'
        }
        1 * jobManagement.requireMinimumPluginVersion('matrix-auth', '1.2')
    }

    def 'call authorization with blocksInheritance'() {
        setup:
        jobManagement.getPermissions('hudson.security.AuthorizationMatrixProperty') >> [
                'hudson.model.Item.Configure',
        ]

        when:
        job.authorization {
            permission('hudson.model.Item.Configure:jill')
            permission('hudson.model.Item.Configure:jack')
            blocksInheritance()
        }

        then:
        with(job.node.properties[0].'hudson.security.AuthorizationMatrixProperty'[0]) {
            children().size() == 3
            blocksInheritance[0].value() == true
            permission.size() == 2
            permission[0].text() == 'hudson.model.Item.Configure:jill'
            permission[1].text() == 'hudson.model.Item.Configure:jack'
        }
        1 * jobManagement.requireMinimumPluginVersion('matrix-auth', '1.2')
    }

    def 'call parameters via helper'() {
        when:
        job.parameters {
            booleanParam('myBooleanParam', true)
        }

        then:
        with(job.node.properties[0].'hudson.model.ParametersDefinitionProperty'[0].parameterDefinitions[0]) {
            children()[0].name() == 'hudson.model.BooleanParameterDefinition'
            children()[0].name.text() == 'myBooleanParam'
            children()[0].defaultValue.text() == 'true'
        }
    }

    def 'call properties'() {
        when:
        job.properties {
            propertiesNodes << new Node(null, 'hack')
        }

        then:
        job.node.properties[0].children()[0].name() == 'hack'
    }

    def 'call triggers'() {
        when:
        job.triggers {
            scm('2 3 * * * *')
        }

        then:
        job.node.triggers[0].'hudson.triggers.SCMTrigger'[0].spec[0].text() == '2 3 * * * *'
    }

    def 'add description'() {
        when:
        job.description('Description')

        then:
        job.node.description[0].value() == 'Description'

        when:
        job.description('Description2')

        then:
        job.node.description.size() == 1
        job.node.description[0].value() == 'Description2'
    }

    def 'environments work with map arg'() {
        when:
        job.environmentVariables([
            key1: 'val1',
            key2: 'val2'
        ])

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
        1 * jobManagement.requirePlugin('envinject')
    }

    def 'environments work with context'() {
        when:
        job.environmentVariables {
            envs([key1: 'val1', key2: 'val2'])
            env 'key3', 'val3'
        }

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
        1 * jobManagement.requirePlugin('envinject')
    }

    def 'environments work with combination'() {
        when:
        job.environmentVariables([key4: 'val4']) {
            env 'key3', 'val3'
        }

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key4=val4')
        1 * jobManagement.requirePlugin('envinject')
    }

    def 'environment from groovy script'() {
        when:
        job.environmentVariables {
            groovy '[foo: "bar"]'
        }

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].groovyScriptContent[0].value() == '[foo: "bar"]'
        1 * jobManagement.requirePlugin('envinject')
    }

    def 'environment from map and groovy script'() {
        when:
        job.environmentVariables {
            envs([key1: 'val1', key2: 'val2'])
            env 'key3', 'val3'
            groovy '[foo: "bar"]'
        }

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key1=val1')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key2=val2')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].propertiesContent[0].value().contains('key3=val3')
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0].groovyScriptContent[0].value() == '[foo: "bar"]'
        1 * jobManagement.requirePlugin('envinject')
    }

    @Unroll
    def 'environment from #method'(content, method, xmlElement) {
        when:
        job.environmentVariables {
            "$method"(content)
        }

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0].info[0]."$xmlElement"[0].value() == content
        1 * jobManagement.requirePlugin('envinject')

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
        job.environmentVariables {
            "${method}"(content)
        }

        then:
        job.node.properties[0].'EnvInjectJobProperty'[0]."${xmlElement}"[0].value() == content
        1 * jobManagement.requirePlugin('envinject')

        where:
        method                   || content || xmlElement
        'keepSystemVariables'     | true     | 'keepJenkinsSystemVariables'
        'keepSystemVariables'     | false    | 'keepJenkinsSystemVariables'
        'keepBuildVariables'      | true     | 'keepBuildVariables'
        'keepBuildVariables'      | false    | 'keepBuildVariables'
        'overrideBuildParameters' | false    | 'overrideBuildParameters'
        'overrideBuildParameters' | true     | 'overrideBuildParameters'
    }

    def 'environment can populate tool installations'() {
        when:
        job.environmentVariables {
            contributors {
                populateToolInstallations()
            }
        }

        then:
        def contributors = job.node.properties[0].'EnvInjectJobProperty'[0].contributors[0]
        contributors.children().size() == 1
        contributors.'org.jenkinsci.plugins.sharedobjects.ToolInstallationJobProperty'
            .'populateToolInstallation'[0].value() == true
        1 * jobManagement.requirePlugin('envinject')
        1 * jobManagement.requireMinimumPluginVersion('shared-objects', '0.1')
    }

    def 'throttle concurrents enabled as project alone'() {
        when:
        job.throttleConcurrentBuilds {
            maxPerNode(1)
            maxTotal(2)
        }

        then:
        with(job.node.properties[0].'hudson.plugins.throttleconcurrents.ThrottleJobProperty'[0]) {
            children().size() == 5
            maxConcurrentPerNode[0].value() == 1
            maxConcurrentTotal[0].value() == 2
            throttleEnabled[0].value() == true
            throttleOption[0].value() == 'project'
            categories[0].children().size() == 0
        }
        1 * jobManagement.requirePlugin('throttle-concurrents')
    }

    def 'throttle concurrents disabled'() {
        when:
        job.throttleConcurrentBuilds {
            throttleDisabled()
        }

        then:
        with(job.node.properties[0].'hudson.plugins.throttleconcurrents.ThrottleJobProperty'[0]) {
            children().size() == 5
            maxConcurrentPerNode[0].value() == 0
            maxConcurrentTotal[0].value() == 0
            throttleEnabled[0].value() == false
            throttleOption[0].value() == 'project'
            categories[0].children().size() == 0
        }
        1 * jobManagement.requirePlugin('throttle-concurrents')
    }

    def 'throttle concurrents enabled as part of categories'() {
        when:
        job.throttleConcurrentBuilds {
            maxPerNode(1)
            maxTotal(2)
            categories(['cat-1', 'cat-2'])
        }

        then:
        with(job.node.properties[0].'hudson.plugins.throttleconcurrents.ThrottleJobProperty'[0]) {
            children().size() == 5
            maxConcurrentPerNode[0].value() == 1
            maxConcurrentTotal[0].value() == 2
            throttleEnabled[0].value() == true
            throttleOption[0].value() == 'category'
            categories[0].children().size() == 2
            categories[0].string[0].value() == 'cat-1'
            categories[0].string[1].value() == 'cat-2'
        }
        1 * jobManagement.requirePlugin('throttle-concurrents')
    }

    def 'throttle concurrents matrix options not allowed for non-matrix jobs'() {
        when:
        job.throttleConcurrentBuilds {
            throttleMatrixBuilds()
        }

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ 'throttleMatrixBuilds can only be used in matrix jobs'

        when:
        job.throttleConcurrentBuilds {
            throttleMatrixConfigurations()
        }

        then:
        e = thrown(DslScriptException)
        e.message =~ 'throttleMatrixConfigurations can only be used in matrix jobs'
    }

    def 'disable defaults to true'() {
        when:
        job.disabled()

        then:
        job.node.disabled.size() == 1
        job.node.disabled[0].value() == true

        when:
        job.disabled(false)

        then:
        job.node.disabled.size() == 1
        job.node.disabled[0].value() == false
    }

    def 'compress build log'() {
        when:
        job.compressBuildLog()

        then:
        with(job.node.properties[0].'org.jenkinsci.plugins.compressbuildlog.BuildLogCompressor'[0]) {
            children().size() == 0
        }
        1 * jobManagement.requireMinimumPluginVersion('compress-buildlog', '1.0')
    }

    def 'log rotate xml'() {
        when:
        job.logRotator(14, 50)

        then:
        job.node.logRotator[0].daysToKeep[0].value() == 14
        job.node.logRotator[0].numToKeep[0].value() == 50
    }

    def 'log rotate xml with closure'() {
        when:
        job.logRotator {
            daysToKeep 1
            numToKeep 2
            artifactDaysToKeep 3
            artifactNumToKeep 4
        }

        then:
        job.node.logRotator[0].daysToKeep[0].value() == 1
        job.node.logRotator[0].numToKeep[0].value() == 2
        job.node.logRotator[0].artifactDaysToKeep[0].value() == 3
        job.node.logRotator[0].artifactNumToKeep[0].value() == 4
    }

    def 'log rotate xml with closure defaults'() {
        when:
        job.logRotator {
        }

        then:
        job.node.logRotator[0].daysToKeep[0].value() == -1
        job.node.logRotator[0].numToKeep[0].value() == -1
        job.node.logRotator[0].artifactDaysToKeep[0].value() == -1
        job.node.logRotator[0].artifactNumToKeep[0].value() == -1
    }

    def 'build blocker'() {
        setup:
        jobManagement.isMinimumPluginVersionInstalled('build-blocker-plugin', '1.7.1') >> true

        when:
        job.blockOn('MyProject')

        then:
        with(job.node.properties[0].'hudson.plugins.buildblocker.BuildBlockerProperty'[0]) {
            children().size() == 4
            useBuildBlocker[0].value() == true
            blockingJobs[0].value() == 'MyProject'
            blockLevel[0].value() == 'NODE'
            scanQueueFor[0].value() == 'DISABLED'
        }
        1 * jobManagement.requireMinimumPluginVersion('build-blocker-plugin', '1.7.1')
    }

    def 'build blocker with all options'() {
        setup:
        jobManagement.isMinimumPluginVersionInstalled('build-blocker-plugin', '1.7.1') >> true

        when:
        job.blockOn('MyProject2') {
            blockLevel(level)
            scanQueueFor(queue)
        }

        then:
        with(job.node.properties[0].'hudson.plugins.buildblocker.BuildBlockerProperty'[0]) {
            children().size() == 4
            useBuildBlocker[0].value() == true
            blockingJobs[0].value() == 'MyProject2'
            blockLevel[0].value() == level
            scanQueueFor[0].value() == queue
        }
        1 * jobManagement.requireMinimumPluginVersion('build-blocker-plugin', '1.7.1')

        where:
        level    | queue
        'GLOBAL' | 'ALL'
        'NODE'   | 'ALL'
        'GLOBAL' | 'DISABLED'
        'NODE'   | 'DISABLED'
        'GLOBAL' | 'BUILDABLE'
        'NODE'   | 'BUILDABLE'
    }

    def 'build blocker with invalid options'() {
        setup:
        jobManagement.isMinimumPluginVersionInstalled('build-blocker-plugin', '1.7.1') >> true

        when:
        job.blockOn('MyProject2') {
            blockLevel(level)
            scanQueueFor(queue)
        }

        then:
        thrown(DslScriptException)

        where:
        level    | queue
        'GLOBAL' | ''
        'NODE'   | 'FOO'
        'GLOBAL' | null
        ''       | 'DISABLED'
        'FOO'    | 'BUILDABLE'
        null     | 'BUILDABLE'
    }

    def 'build blocker with iterator'() {
        setup:
        jobManagement.isMinimumPluginVersionInstalled('build-blocker-plugin', '1.7.1') >> true

        when:
        job.blockOn(['MyProject', 'MyProject2', 'MyProject3']) {
            blockLevel('GLOBAL')
            scanQueueFor('ALL')
        }

        then:
        with(job.node.properties[0].'hudson.plugins.buildblocker.BuildBlockerProperty'[0]) {
            children().size() == 4
            useBuildBlocker[0].value() == true
            blockingJobs[0].value() == 'MyProject\nMyProject2\nMyProject3'
            blockLevel[0].value() == 'GLOBAL'
            scanQueueFor[0].value() == 'ALL'
        }
        1 * jobManagement.requireMinimumPluginVersion('build-blocker-plugin', '1.7.1')
    }

    def 'add a quiet period'() {
        when:
        job.quietPeriod()

        then:
        job.node.quietPeriod[0].value() == 5

        when:
        job.quietPeriod(10)

        then:
        job.node.quietPeriod[0].value() == 10
    }

    def 'add display name'() {
        when:
        job.displayName('FooBar')

        then:
        job.node.displayName[0].value() == 'FooBar'
    }

    def 'set keep Dependencies'(keep) {
        when:
        job.keepDependencies(keep)

        then:
        job.node.keepDependencies.size() == 1
        job.node.keepDependencies[0].value() == keep

        where:
        keep << [true, false]
    }

    def 'delivery pipeline configuration with stage and task names'() {
        when:
        job.deliveryPipelineConfiguration('qa', 'integration-tests')

        then:
        job.node.'properties'.size() == 1
        job.node.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'.size() == 1
        with(job.node.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'[0]) {
            children().size() == 2
            taskName[0].value() == 'integration-tests'
            stageName[0].value() == 'qa'
        }
        1 * jobManagement.requireMinimumPluginVersion('delivery-pipeline-plugin', '0.10.0')
    }

    def 'delivery pipeline configuration with stage name'() {
        when:
        job.deliveryPipelineConfiguration('qa')

        then:
        job.node.'properties'.size() == 1
        job.node.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'.size() == 1
        with(job.node.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'[0]) {
            children().size() == 1
            stageName[0].value() == 'qa'
        }
        1 * jobManagement.requireMinimumPluginVersion('delivery-pipeline-plugin', '0.10.0')
    }

    def 'delivery pipeline configuration with task name'() {
        when:
        job.deliveryPipelineConfiguration(null, 'integration-tests')

        then:
        job.node.'properties'.size() == 1
        job.node.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'.size() == 1
        with(job.node.'properties'[0].'se.diabol.jenkins.pipeline.PipelineProperty'[0]) {
            children().size() == 1
            taskName[0].value() == 'integration-tests'
        }
        1 * jobManagement.requireMinimumPluginVersion('delivery-pipeline-plugin', '0.10.0')
    }

    def 'set notification with default properties'() {
        when:
        job.notifications {
            endpoint('http://endpoint.com')
        }

        then:
        with(job.node.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty') {
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 1
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].children().size() == 6
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint.com'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'HTTP'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'JSON'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].event[0].text() == 'all'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].timeout[0].value() == 30000
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].loglines[0].value() == 0
        }
        1 * jobManagement.requireMinimumPluginVersion('notification', '1.12')
    }

    def 'set notification with all required properties'() {
        when:
        job.notifications {
            endpoint('http://endpoint.com', 'TCP', 'XML')
        }

        then:
        with(job.node.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty') {
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 1
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].children().size() == 6
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint.com'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'TCP'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'XML'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].event[0].text() == 'all'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].timeout[0].value() == 30000
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].loglines[0].value() == 0
        }
        1 * jobManagement.requireMinimumPluginVersion('notification', '1.12')
    }

    def 'set notification with invalid parameters'(String url, String protocol, String format, String event) {
        when:
        job.notifications {
            endpoint(url, protocol, format) {
                delegate.event(event)
            }
        }

        then:
        thrown(DslScriptException)

        where:
        url        | protocol | format  | event
        'foo:2300' | 'TCP'    | 'what?' | 'all'
        'foo:2300' | 'TCP'    | ''      | 'all'
        'foo:2300' | 'TCP'    | null    | 'all'
        'foo:2300' | 'test'   | 'JSON'  | 'all'
        'foo:2300' | ''       | 'JSON'  | 'all'
        'foo:2300' | null     | 'JSON'  | 'all'
        ''         | 'TCP'    | 'JSON'  | 'all'
        null       | 'TCP'    | 'JSON'  | 'all'
        'foo:2300' | 'TCP'    | 'JSON'  | 'acme'
        'foo:2300' | 'TCP'    | 'JSON'  | ''
        'foo:2300' | 'TCP'    | 'JSON'  | null
    }

    def 'set notification with default properties and using a closure'() {
        when:
        job.notifications {
            endpoint('http://endpoint.com') {
                event('started')
                timeout(10000)
            }
        }

        then:
        with(job.node.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty') {
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 1
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].children().size() == 6
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint.com'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'HTTP'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'JSON'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].event[0].text() == 'started'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].timeout[0].value() == 10000
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].loglines[0].value() == 0
        }
        1 * jobManagement.requireMinimumPluginVersion('notification', '1.12')
    }

    def 'set notification with all required properties and using a closure'() {
        when:
        job.notifications {
            endpoint('http://endpoint.com', 'TCP', 'XML') {
                event('started')
                timeout(10000)
                logLines(10)
            }
        }

        then:
        with(job.node.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty') {
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 1
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].children().size() == 6
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint.com'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'TCP'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'XML'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].event[0].text() == 'started'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].timeout[0].value() == 10000
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].loglines[0].value() == 10
        }
        1 * jobManagement.requireMinimumPluginVersion('notification', '1.12')
    }

    def 'set notification with multiple endpoints'() {
        when:
        job.notifications {
            endpoint('http://endpoint1.com')
            endpoint('http://endpoint2.com')
        }

        then:
        with(job.node.properties[0].'com.tikal.hudson.plugins.notification.HudsonNotificationProperty') {
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'.size() == 2

            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].children().size() == 6
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].url[0].text() == 'http://endpoint1.com'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].protocol[0].text() == 'HTTP'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].format[0].text() == 'JSON'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].event[0].text() == 'all'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].timeout[0].value() == 30000
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[0].loglines[0].value() == 0

            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].children().size() == 6
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].url[0].text() == 'http://endpoint2.com'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].protocol[0].text() == 'HTTP'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].format[0].text() == 'JSON'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].event[0].text() == 'all'
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].timeout[0].value() == 30000
            endpoints.'com.tikal.hudson.plugins.notification.Endpoint'[1].loglines[0].value() == 0
        }
        1 * jobManagement.requireMinimumPluginVersion('notification', '1.12')
    }
}
