package javaposse.jobdsl.plugin

import com.cloudbees.hudson.plugins.folder.Folder
import hudson.model.FreeStyleProject
import hudson.model.Item
import hudson.model.ItemGroup
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import org.jvnet.hudson.test.WithoutJenkins
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static javaposse.jobdsl.plugin.LookupStrategy.JENKINS_ROOT
import static javaposse.jobdsl.plugin.LookupStrategy.SEED_JOB

class LookupStrategySpec extends Specification {
    @Shared
    @ClassRule
    JenkinsRule jenkinsRule = new JenkinsRule()

    @Shared
    Folder folder

    @Shared
    Item seedJob

    def setupSpec() {
        folder = jenkinsRule.jenkins.createProject(Folder, 'folder')
        seedJob = folder.createProject(FreeStyleProject, 'seed')
        folder.createProject(FreeStyleProject, 'job')
        folder.createProject(Folder, 'folder')
        jenkinsRule.createFreeStyleProject('job')
    }

    @WithoutJenkins
    def 'display name'(LookupStrategy lookupStrategy, String expectedName) {
        when:
        String name = lookupStrategy.displayName

        then:
        name == expectedName

        where:
        lookupStrategy | expectedName
        JENKINS_ROOT   | 'Jenkins Root'
        SEED_JOB       | 'Seed Job'
    }

    def 'getItem'(LookupStrategy lookupStrategy, String expectedFullName) {
        when:
        Item result = lookupStrategy.getItem(seedJob, 'job', Item)

        then:
        result.fullName == expectedFullName

        where:
        lookupStrategy | expectedFullName
        JENKINS_ROOT   | 'job'
        SEED_JOB       | 'folder/job'
    }

    def 'getItem not normalized'(LookupStrategy lookupStrategy, String expectedFullName) {
        when:
        Item result = lookupStrategy.getItem(seedJob, '../job', Item)

        then:
        result?.fullName == expectedFullName

        where:
        lookupStrategy | expectedFullName
        JENKINS_ROOT   | null
        SEED_JOB       | 'job'
    }

    def 'getContext'(LookupStrategy lookupStrategy, String expectedFullName) {
        when:
        ItemGroup result = lookupStrategy.getContext(seedJob)

        then:
        result.fullName == expectedFullName

        where:
        lookupStrategy | expectedFullName
        JENKINS_ROOT   | ''
        SEED_JOB       | 'folder'
    }

    @Unroll
    def 'getParent for #lookupStrategy: #path'(LookupStrategy lookupStrategy, String path, String expectedFullName) {
        when:
        ItemGroup result = lookupStrategy.getParent(seedJob, path)

        then:
        result.fullName == expectedFullName

        where:
        lookupStrategy | path          | expectedFullName
        JENKINS_ROOT   | 'job'         | ''
        SEED_JOB       | 'job'         | 'folder'
        JENKINS_ROOT   | '/job'        | ''
        SEED_JOB       | '/job'        | ''
        JENKINS_ROOT   | 'folder/job'  | 'folder'
        SEED_JOB       | 'folder/job'  | 'folder/folder'
        JENKINS_ROOT   | '/folder/foo' | 'folder'
        SEED_JOB       | '/folder/foo' | 'folder'
    }

    @Unroll
    def 'getParent not normalized for path #path and #strategy'(LookupStrategy strategy, String path, String expected) {
        when:
        ItemGroup result = strategy.getParent(seedJob, path)

        then:
        result?.fullName == expected

        where:
        strategy     | path                          | expected
        JENKINS_ROOT | '../folder/job'               | null
        SEED_JOB     | '../folder/job'               | 'folder'
        JENKINS_ROOT | '/folder/../job'              | ''
        SEED_JOB     | '/folder/../job'              | ''
        JENKINS_ROOT | 'folder/../job'               | ''
        SEED_JOB     | 'folder/../job'               | 'folder'
        JENKINS_ROOT | 'folder/with space/../../job' | ''
        SEED_JOB     | 'folder/with space/../../job' | 'folder'
    }
}
