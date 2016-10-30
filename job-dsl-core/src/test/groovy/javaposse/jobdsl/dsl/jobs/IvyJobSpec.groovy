package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class IvyJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final IvyJob job = new IvyJob(jobManagement, 'test')

    def 'construct simple Ivy job and generate xml from it'() {
        when:
        def xml = job.node

        then:
        xml.name() == 'hudson.ivy.IvyModuleSet'
        xml.children().size() == 17
    }

    def 'no steps for Ivy jobs'() {
        when:
        job.steps {
        }

        then:
        thrown(IllegalStateException)
    }

    def 'ivyFilePattern constructs xml'() {
        when:
        job.ivyFilePattern('modules/*/ivy/ivy.xml')

        then:
        job.node.ivyFilePattern.size() == 1
        job.node.ivyFilePattern[0].value() == 'modules/*/ivy/ivy.xml'
    }

    def 'ivyFileExcludesPattern constructs xml'() {
        when:
        job.ivyFileExcludesPattern('moduleA/**,moduleB/**')

        then:
        job.node.ivyFileExcludesPattern.size() == 1
        job.node.ivyFileExcludesPattern[0].value() == 'moduleA/**,moduleB/**'
    }

    def 'ivyBranch constructs xml'() {
        when:
        job.ivyBranch('productX/1.0')

        then:
        job.node.ivyBranch.size() == 1
        job.node.ivyBranch[0].value() == 'productX/1.0'
    }

    def 'relativePathToDescriptorFromModuleRoot constructs xml'() {
        when:
        job.relativePathToDescriptorFromModuleRoot('ivy/ivy.xml')

        then:
        job.node.relativePathToDescriptorFromModuleRoot.size() == 1
        job.node.relativePathToDescriptorFromModuleRoot[0].value() == 'ivy/ivy.xml'
    }

    def 'ivySettingsFile constructs xml'() {
        when:
        job.ivySettingsFile('build/ivy/ivysettings.xml')

        then:
        job.node.ivySettingsFile.size() == 1
        job.node.ivySettingsFile[0].value() == 'build/ivy/ivysettings.xml'
    }

    def 'ivySettingsPropertyFiles constructs xml'() {
        when:
        job.ivySettingsPropertyFiles('branch.properties,deps.properties')

        then:
        job.node.ivySettingsPropertyFiles.size() == 1
        job.node.ivySettingsPropertyFiles[0].value() == 'branch.properties,deps.properties'
    }

    def 'perModuleBuild constructs xml'() {
        when:
        job.perModuleBuild()

        then:
        job.node.aggregatorStyleBuild.size() == 1
        job.node.aggregatorStyleBuild[0].value() == false
    }

    def 'incrementalBuild constructs xml'() {
        when:
        job.incrementalBuild()

        then:
        job.node.incrementalBuild.size() == 1
        job.node.incrementalBuild[0].value() == true
    }

    def 'can add ant ivyBuilder'() {
        when:
        job.ivyBuilder {
            ant()
        }

        then:
        job.node.ivyBuilderType.size() == 1
        job.node.ivyBuilderType[0].attribute('class') == 'hudson.ivy.builder.AntIvyBuilderType'
    }

    def 'can only add one ivyBuilder'() {
        when:
        job.ivyBuilder {
            ant()
            ant()
        }

        then:
        thrown(DslScriptException)
    }
}
