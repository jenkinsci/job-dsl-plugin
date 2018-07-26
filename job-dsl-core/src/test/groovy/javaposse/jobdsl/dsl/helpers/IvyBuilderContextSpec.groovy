package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class IvyBuilderContextSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final Item item = Mock(Item)
    private final IvyBuilderContext context = new IvyBuilderContext(jobManagement, item)

    def 'extension node is transformed to ivyBuilderType node'() {
        Node node = new Node(null, 'org.example.CustomBuilder', [foo: 'bar'])
        node.appendNode('test', 'value')

        when:
        context.addExtensionNode(node)

        then:
        with(context.ivyBuilderNodes[0]) {
            name() == 'ivyBuilderType'
            attributes().size() == 2
            attribute('class') == 'org.example.CustomBuilder'
            attribute('foo') == 'bar'
            children().size() == 1
            test[0].text() == 'value'
        }
    }

    def 'construct simple ant builder type'() {
        when:
        context.ant()

        then:
        context.ivyBuilderNodes[0].@class == 'hudson.ivy.builder.AntIvyBuilderType'
        with(context.ivyBuilderNodes[0]) {
            name() == 'ivyBuilderType'
            children().size() == 2
            targets[0].text() == ''
            antName[0].text() == '(Default)'
        }
        1 * jobManagement.requireMinimumPluginVersion('ant', '1.2')
    }

    def 'construct ant builder type with options'() {
        when:
        context.ant {
            target('clean')
            targets(['test', 'publish'])
            buildFile('build.xml')
            antInstallation('Ant 1.9')
            prop('key', 'value')
            javaOpt('-Xmx=1G')
        }

        then:
        context.ivyBuilderNodes[0].@class == 'hudson.ivy.builder.AntIvyBuilderType'
        with(context.ivyBuilderNodes[0]) {
            name() == 'ivyBuilderType'
            children().size() == 5
            targets[0].text() == 'clean test publish'
            antName[0].text() == 'Ant 1.9'
            antOpts[0].text() == '-Xmx=1G'
            buildFile[0].text() == 'build.xml'
            antProperties[0].text() == 'key=value'
        }
        1 * jobManagement.requireMinimumPluginVersion('ant', '1.2')
    }
}
