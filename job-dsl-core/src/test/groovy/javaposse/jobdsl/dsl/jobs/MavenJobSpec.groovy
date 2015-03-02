package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobTypeMismatchException
import javaposse.jobdsl.dsl.helpers.common.MavenContext
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class MavenJobSpec extends Specification {

    private final JobManagement jobManagement = Mock(JobManagement)
    private final MavenJob job = new MavenJob(jobManagement)

    def setup() {
        XMLUnit.setIgnoreWhitespace(true)
    }

    def 'construct simple Maven job and generate xml from it'() {
        when:
        def xml = job.xml

        then:
        assertXMLEqual MavenJob.TEMPLATE, xml
    }

    def 'free-style job extends Maven template and fails to generate xml'() {
        when:
        FreeStyleJob job = new FreeStyleJob(jobManagement)
        job.using('TMPL')
        job.xml

        then:
        1 * jobManagement.getConfig('TMPL') >> MavenJob.TEMPLATE
        thrown(JobTypeMismatchException)
    }

    def 'Maven job extends free-style template and fails to generate xml'() {
        when:
        job.using('TMPL')
        job.xml

        then:
        1 * jobManagement.getConfig('TMPL') >> FreeStyleJob.TEMPLATE
        thrown(JobTypeMismatchException)
    }

    def 'no steps for Maven jobs'() {
        when:
        job.steps {
        }

        then:
        thrown(IllegalStateException)
    }

    def 'rootPOM constructs xml'() {
        when:
        job.rootPOM('my_module/pom.xml')

        then:
        job.node.rootPOM.size() == 1
        job.node.rootPOM[0].value() == 'my_module/pom.xml'
    }

    def 'goals constructs xml'() {
        when:
        job.goals('clean')
        job.goals('verify')

        then:
        job.node.goals.size() == 1
        job.node.goals[0].value() == 'clean verify'
    }
    def 'mavenOpts constructs xml'() {
        when:
        job.mavenOpts('-Xms256m')
        job.mavenOpts('-Xmx512m')

        then:
        job.node.mavenOpts.size() == 1
        job.node.mavenOpts[0].value() == '-Xms256m -Xmx512m'
    }

    def 'perModuleEmail constructs xml'() {
        when:
        job.perModuleEmail(false)

        then:
        job.node.perModuleEmail.size() == 1
        job.node.perModuleEmail[0].value() == false
    }

    def 'archivingDisabled constructs xml'() {
        when:
        job.archivingDisabled(true)

        then:
        job.node.archivingDisabled.size() == 1
        job.node.archivingDisabled[0].value() == true
    }

    def 'runHeadless constructs xml'() {
        when:
        job.runHeadless(true)

        then:
        job.node.runHeadless.size() == 1
        job.node.runHeadless[0].value() == true
    }

    def 'cannot run localRepository with null argument'() {
        when:
        job.localRepository(null)

        then:
        thrown(NullPointerException)
    }

    def 'localRepository constructs xml for LocalToExecutor'() {
        when:
        job.localRepository(MavenContext.LocalRepositoryLocation.LocalToExecutor)

        then:
        job.node.localRepository[0].attribute('class') == 'hudson.maven.local_repo.PerExecutorLocalRepositoryLocator'
    }

    def 'localRepository constructs xml for LocalToWorkspace'() {
        when:
        job.localRepository(MavenContext.LocalRepositoryLocation.LocalToWorkspace)

        then:
        job.node.localRepository[0].attribute('class') == 'hudson.maven.local_repo.PerJobLocalRepositoryLocator'
    }

    def 'can add preBuildSteps'() {
        when:
        job.preBuildSteps {
            shell('ls')
        }

        then:
        job.node.prebuilders[0].children()[0].name() == 'hudson.tasks.Shell'
        job.node.prebuilders[0].children()[0].command[0].value() == 'ls'
    }

    def 'can add postBuildSteps'() {
        when:
        job.postBuildSteps {
            shell('ls')
        }

        then:
        job.node.postbuilders[0].children()[0].name() == 'hudson.tasks.Shell'
        job.node.postbuilders[0].children()[0].command[0].value() == 'ls'
    }

    def 'mavenInstallation constructs xml'() {
        when:
        job.mavenInstallation('test')

        then:
        job.node.mavenName.size() == 1
        job.node.mavenName[0].value() == 'test'
    }

    def 'call maven method with unknown provided settings'() {
        setup:
        String settingsName = 'lalala'

        when:
        job.providedSettings(settingsName)

        then:
        Exception e = thrown(NullPointerException)
        e.message.contains(settingsName)
    }

    def 'call maven method with provided settings'() {
        setup:
        String settingsName = 'maven-proxy'
        String settingsId = '123123415'
        jobManagement.getConfigFileId(ConfigFileType.MavenSettings, settingsName) >> settingsId

        when:
        job.providedSettings(settingsName)

        then:
        job.node.settings.size() == 1
        job.node.settings[0].attribute('class') == 'org.jenkinsci.plugins.configfiles.maven.job.MvnSettingsProvider'
        job.node.settings[0].children().size() == 1
        job.node.settings[0].settingsConfigId[0].value() == settingsId
    }
}
