package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class IvyBuilderContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Item item = Mock(Item)
    IvyBuilderContext context = new IvyBuilderContext(jobManagement, item)

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
        1 * jobManagement.requirePlugin('ant')
    }

    def 'construct ant builder type with options'() {
        when:
        context.ant {
            target 'clean'
            targets(['test', 'publish'])
            buildFile 'build.xml'
            antInstallation 'Ant 1.9'
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
        1 * jobManagement.requirePlugin('ant')
    }
}
