package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation
import spock.lang.Specification

class MavenJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final MavenJob job = new MavenJob(jobManagement, 'test')

    def 'construct simple Maven job and generate xml from it'() {
        when:
        def xml = job.node

        then:
        xml.name() == 'maven2-moduleset'
        xml.children().size() == 21
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

    def 'archivingDisabled constructs xml'() {
        when:
        job.archivingDisabled(true)

        then:
        job.node.archivingDisabled.size() == 1
        job.node.archivingDisabled[0].value() == true
    }

    def 'siteArchivingDisabled constructs xml'() {
        when:
        job.siteArchivingDisabled(true)

        then:
        job.node.siteArchivingDisabled.size() == 1
        job.node.siteArchivingDisabled[0].value() == true
    }

    def 'fingerprintingDisabled constructs xml'() {
        when:
        job.fingerprintingDisabled(true)

        then:
        job.node.fingerprintingDisabled.size() == 1
        job.node.fingerprintingDisabled[0].value() == true
        1 * jobManagement.requireMinimumPluginVersion('maven-plugin', '2.4')
    }

    def 'resolveDependencies without arg constructs xml'() {
        when:
        job.resolveDependencies()

        then:
        job.node.resolveDependencies.size() == 1
        job.node.resolveDependencies[0].value() == true
    }

    def 'resolveDependencies constructs xml'() {
        when:
        job.resolveDependencies(value)

        then:
        job.node.resolveDependencies.size() == 1
        job.node.resolveDependencies[0].value() == value

        where:
        value << [true, false]
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
        job.localRepository((LocalRepositoryLocation) null)

        then:
        thrown(DslScriptException)
    }

    def 'localRepository constructs xml for LOCAL_TO_EXECUTOR'() {
        when:
        job.localRepository(LocalRepositoryLocation.LOCAL_TO_EXECUTOR)

        then:
        job.node.localRepository[0].attribute('class') == 'hudson.maven.local_repo.PerExecutorLocalRepositoryLocator'
    }

    def 'localRepository constructs xml for LOCAL_TO_WORKSPACE'() {
        when:
        job.localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)

        then:
        job.node.localRepository[0].attribute('class') == 'hudson.maven.local_repo.PerJobLocalRepositoryLocator'
    }

    def 'incrementalBuild constructs xml'() {
        when:
        job.incrementalBuild(value)

        then:
        job.node.incrementalBuild.size() == 1
        job.node.incrementalBuild[0].value() == value

        where:
        value << [true, false]
    }

    def 'incrementalBuild without arg constructs xml'() {
        when:
        job.incrementalBuild()

        then:
        job.node.incrementalBuild.size() == 1
        job.node.incrementalBuild[0].value() == true
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
        job.node.runPostStepsIfResult[0].name[0].value() == 'FAILURE'
        job.node.runPostStepsIfResult[0].ordinal[0].value() == 2
        job.node.runPostStepsIfResult[0].color[0].value() == 'RED'
        job.node.runPostStepsIfResult[0].completeBuild[0].value() == true
        job.node.postbuilders[0].children()[0].name() == 'hudson.tasks.Shell'
        job.node.postbuilders[0].children()[0].command[0].value() == 'ls'
    }

    def 'call post build steps only on successful build'() {
        when:
        job.postBuildSteps(threshold) {
            shell('ls')
        }

        then:
        job.node.runPostStepsIfResult[0].name[0].value() == threshold
        job.node.runPostStepsIfResult[0].ordinal[0].value() == ordinal
        job.node.runPostStepsIfResult[0].color[0].value() == color
        job.node.runPostStepsIfResult[0].completeBuild[0].value() == true
        job.node.postbuilders[0].children()[0].name() == 'hudson.tasks.Shell'
        job.node.postbuilders[0].children()[0].command[0].value() == 'ls'

        where:
        threshold  | ordinal | color
        'SUCCESS'  | 0       | 'BLUE'
        'UNSTABLE' | 1       | 'YELLOW'
        'FAILURE'  | 2       | 'RED'
    }

    def 'call post build steps with invalid threshold'() {
        when:
        job.postBuildSteps('INVALID') {
            shell('ls')
        }

        then:
        thrown(DslScriptException)
    }

    def 'mavenInstallation constructs xml'() {
        when:
        job.mavenInstallation('test')

        then:
        job.node.mavenName.size() == 1
        job.node.mavenName[0].value() == 'test'
    }

    def 'call maven method with provided settings ID'() {
        setup:
        String settingsName = 'lalala'

        when:
        job.providedSettings(settingsName)

        then:
        job.node.settings.size() == 1
        with(job.node.settings[0]) {
            attribute('class') == 'org.jenkinsci.plugins.configfiles.maven.job.MvnSettingsProvider'
            children().size() == 1
            settingsConfigId[0].value() == settingsName
        }
    }

    def 'call maven method with provided settings name'() {
        setup:
        String settingsName = 'maven-proxy'
        String settingsId = '123123415'
        jobManagement.getConfigFileId(ConfigFileType.MavenSettings, settingsName) >> settingsId

        when:
        job.providedSettings(settingsName)

        then:
        job.node.settings.size() == 1
        with(job.node.settings[0]) {
            attribute('class') == 'org.jenkinsci.plugins.configfiles.maven.job.MvnSettingsProvider'
            children().size() == 1
            settingsConfigId[0].value() == settingsId
        }
    }

    def 'call maven method with provided global settings ID'() {
        setup:
        String settingsName = 'lalala'

        when:
        job.providedGlobalSettings(settingsName)

        then:
        job.node.globalSettings.size() == 1
        with(job.node.globalSettings[0]) {
            attribute('class') == 'org.jenkinsci.plugins.configfiles.maven.job.MvnGlobalSettingsProvider'
            children().size() == 1
            settingsConfigId[0].value() == settingsName
        }
    }

    def 'call maven method with provided global settings name'() {
        setup:
        String settingsName = 'maven-proxy'
        String settingsId = '123123415'
        jobManagement.getConfigFileId(ConfigFileType.GlobalMavenSettings, settingsName) >> settingsId

        when:
        job.providedGlobalSettings(settingsName)

        then:
        job.node.globalSettings.size() == 1
        with(job.node.globalSettings[0]) {
            attribute('class') == 'org.jenkinsci.plugins.configfiles.maven.job.MvnGlobalSettingsProvider'
            children().size() == 1
            settingsConfigId[0].value() == settingsId
        }
    }

    def 'call publishers'() {
        when:
        job.publishers {
            deployArtifacts()
        }

        then:
        job.node.publishers[0].children()[0].name() == 'hudson.maven.RedeployPublisher'
    }

    def 'call properties'() {
        when:
        job.properties {
            mavenInfo {}
        }

        then:
        job.node.properties[0].children()[0].name() == 'jenkins.plugins.maveninfo.config.MavenInfoJobConfig'
    }

    def 'call inherited publishers which use JobManagement, JENKINS-27767'() {
        when:
        job.publishers {
            publishHtml {
                report('target/sonar/issues-report/')
            }
        }

        then:
        noExceptionThrown()
    }

    def 'disableDownstreamTrigger constructs xml'() {
        when:
        job.disableDownstreamTrigger()

        then:
        job.node.disableTriggerDownstreamProjects.size() == 1
        job.node.disableTriggerDownstreamProjects[0].value() == true
    }
}
