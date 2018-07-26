package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class MavenPropertiesContextSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final Item item = Mock(Item)
    private final MavenPropertiesContext context = new MavenPropertiesContext(jobManagement, item)

    def 'call mavenInfo with no options'() {
        when:
        context.mavenInfo {
        }

        then:
        with(context.propertiesNodes[0]) {
            name() == 'jenkins.plugins.maveninfo.config.MavenInfoJobConfig'
            children().size() == 6
            assignDescription[0].value() == false
            descriptionTemplate[0].value().empty
            mainModulePattern[0].value().empty
            dependenciesPattern[0].value().empty
            assignName[0].value() == false
            nameTemplate[0].value().empty
        }
        1 * jobManagement.requireMinimumPluginVersion('maven-info', '0.2.0')
    }

    def 'call mavenInfo with all options'() {
        when:
        context.mavenInfo {
            modulePattern(':my-artifact')
            interestingDependenciesPattern('org.springframework.*:*')
            assignName('name template')
            assignDescription('some text')
        }

        then:
        with(context.propertiesNodes[0]) {
            name() == 'jenkins.plugins.maveninfo.config.MavenInfoJobConfig'
            children().size() == 6
            assignDescription[0].value() == true
            descriptionTemplate[0].value() == 'some text'
            mainModulePattern[0].value() == ':my-artifact'
            dependenciesPattern[0].value() == 'org.springframework.*:*'
            assignName[0].value() == true
            nameTemplate[0].value() == 'name template'
        }
        1 * jobManagement.requireMinimumPluginVersion('maven-info', '0.2.0')
    }
}
