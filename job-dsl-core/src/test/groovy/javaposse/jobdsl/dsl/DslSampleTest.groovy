package javaposse.jobdsl.dsl

import spock.lang.*
import groovy.xml.MarkupBuilder

/**
 * Attempt to execute the sample page in README.md
 */
class DslSampleTest extends Specification {

    def 'load sample dsl'() {
        setup:
        StringJobManagement jm = new StringJobManagement()
        jm.addConfig('TMPL-test', sampleTemplate)

        when:
        Set<GeneratedJob> results = DslScriptLoader.runDsl(sampleDsl, jm)

        then:
        results != null
        results.size() == 4
        jm.savedConfigs.size() == 4
        def firstJob = jm.savedConfigs['PROJ-unit-tests']
        firstJob != null
        // TODO Review actual results
        println(firstJob)
    }

    def 'use parameters when loading script'() {
        setup:
        setup:
        StringJobManagement jm = new StringJobManagement()
        jm.params.gitUrl = 'git://github.com/JavaPosseRoundup/job-dsl-plugin.git'
        jm.params.REPO = 'JavaPosseRoundup'

        when:
        Set<GeneratedJob> results = DslScriptLoader.runDsl(sampleVarDsl, jm)

        then:
        results != null
        results.size() == 1
        jm.savedConfigs.size() == 1
        def firstJob = jm.savedConfigs['PROJ-JavaPosseRoundup']
        firstJob != null
        println firstJob
    }

    def sampleTemplate = '''<?xml version='1.0' encoding='UTF-8'?>
<project>
  <actions/>
  <description>Description</description>
  <logRotator>
    <daysToKeep>-1</daysToKeep>
    <numToKeep>10</numToKeep>
    <artifactDaysToKeep>-1</artifactDaysToKeep>
    <artifactNumToKeep>-1</artifactNumToKeep>
  </logRotator>
  <keepDependencies>false</keepDependencies>
  <properties>
    <hudson.security.AuthorizationMatrixProperty>
      <permission>hudson.model.Item.Configure:jryan</permission>
      <permission>hudson.model.Item.Workspace:jryan</permission>
      <permission>hudson.model.Item.Delete:jryan</permission>
      <permission>hudson.model.Item.Build:jryan</permission>
      <permission>hudson.model.Run.Delete:jryan</permission>
      <permission>hudson.model.Item.Read:jryan</permission>
      <permission>hudson.model.Item.Release:jryan</permission>
      <permission>hudson.model.Item.ExtendedRead:jryan</permission>
      <permission>hudson.model.Run.Update:jryan</permission>
    </hudson.security.AuthorizationMatrixProperty>
    <hudson.plugins.disk__usage.DiskUsageProperty/>
    <hudson.plugins.jira.JiraProjectProperty>
      <siteName>http://jira.company.com/</siteName>
    </hudson.plugins.jira.JiraProjectProperty>
    <hudson.plugins.descriptionsetter.JobByDescription/>
  </properties>
  <assignedNode>RPM</assignedNode>
  <canRoam>false</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <jdk>JDK 6</jdk>
  <concurrentBuild>false</concurrentBuild>
  <builders>
  </builders>
  <publishers>
    <hudson.tasks.ArtifactArchiver>
      <artifacts>webapplication/build/**/*.rpm, webapplication/dist/*</artifacts>
      <latestOnly>false</latestOnly>
    </hudson.tasks.ArtifactArchiver>
    <hudson.plugins.chucknorris.CordellWalkerRecorder>
      <factGenerator/>
    </hudson.plugins.chucknorris.CordellWalkerRecorder>
    <hudson.plugins.emailext.ExtendedEmailPublisher>
      <recipientList>Engineering@company.com</recipientList>
      <configuredTriggers>
        <hudson.plugins.emailext.plugins.trigger.FailureTrigger>
          <email>
            <recipientList>$PROJECT_DEFAULT_RECIPIENTS</recipientList>
            <subject>$PROJECT_DEFAULT_SUBJECT</subject>
            <body>$PROJECT_DEFAULT_CONTENT</body>
            <sendToDevelopers>true</sendToDevelopers>
            <sendToRequester>false</sendToRequester>
            <includeCulprits>false</includeCulprits>
            <sendToRecipientList>true</sendToRecipientList>
          </email>
        </hudson.plugins.emailext.plugins.trigger.FailureTrigger>
        <hudson.plugins.emailext.plugins.trigger.FixedTrigger>
          <email>
            <recipientList>$PROJECT_DEFAULT_RECIPIENTS</recipientList>
            <subject>$PROJECT_DEFAULT_SUBJECT</subject>
            <body>$PROJECT_DEFAULT_CONTENT</body>
            <sendToDevelopers>true</sendToDevelopers>
            <sendToRequester>false</sendToRequester>
            <includeCulprits>true</includeCulprits>
            <sendToRecipientList>true</sendToRecipientList>
          </email>
        </hudson.plugins.emailext.plugins.trigger.FixedTrigger>
      </configuredTriggers>
      <contentType>default</contentType>
      <defaultSubject>$DEFAULT_SUBJECT</defaultSubject>
      <defaultContent>$DEFAULT_CONTENT</defaultContent>
    </hudson.plugins.emailext.ExtendedEmailPublisher>
  </publishers>
  <buildWrappers/>
</project>
'''
    def sampleVarDsl = '''
job {
    name "PROJ-${REPO}"
    scm {
        git(gitUrl)
    }
    triggers {
        scm('*/15 * * * *')
    }
    steps { // build step
        maven('install')
    }
}
'''

    def sampleDsl = '''
def gitUrl = 'git://github.com/JavaPosseRoundup/job-dsl-plugin.git'

job {
    using 'TMPL-test'
    name 'PROJ-unit-tests'
    scm {
        git(gitUrl)
    }
    triggers {
        scm('*/15 * * * *')
    }
    steps { // build step
        maven('-e clean test')
    }
}

job {
    using 'TMPL-test'
    name 'PROJ-sonar'
    scm {
        git(gitUrl)
    }
    triggers {
        cron('15 13 * * *')
    }
    steps {
        maven('sonar:sonar')
    }
}
job {
    using 'TMPL-test'
    name 'PROJ-integ-tests'
    scm {
        git(gitUrl)
    }
    triggers {
        cron('15 1,13 * * *')
    }
    steps {
        maven('-e clean integTest')
    }
}

job {
    // No template, not needed
    name 'PROJ-release'
    scm {
        git(gitUrl)
    }
    // No Trigger
    authorization {
        // Limit builds to just jack and jill
        permission('ItemBuild', 'jill')
        permission('ItemBuild', 'jack')
    }
    steps {
        maven('release')
        shell('cleanup.sh')
    }
}
'''
}
