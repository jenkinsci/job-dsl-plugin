package javaposse.jobdsl.dsl

import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

/**
 * Attempt to execute the sample page in README.md
 */
class DslSampleSpec extends Specification {
    def 'load sample dsl'() {
        setup:
        MemoryJobManagement jm = new MemoryJobManagement()
        jm.availableConfigs['TMPL-test'] = sampleTemplate
        jm.availableConfigs['TMPL-test-maven'] = sampleMavenTemplate

        when:
        Set<GeneratedJob> results = new DslScriptLoader(jm).runScripts([new ScriptRequest(sampleDsl)]).jobs

        then:
        results != null
        results.size() == 6
        jm.savedConfigs.size() == 6
        def firstJob = jm.savedConfigs['PROJ-unit-tests']
        firstJob != null
        def mavenJob = jm.savedConfigs['PROJ-maven']

        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + mavenXml, mavenJob
        def mavenJobWithTemplate = jm.savedConfigs['PROJ-maven-with-template']
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + mavenXmlWithTemplate, mavenJobWithTemplate
    }

    def 'use parameters when loading script'() {
        setup:
        MemoryJobManagement jm = new MemoryJobManagement()
        jm.parameters.gitUrl = 'git://github.com/JavaPosseRoundup/job-dsl-plugin.git'
        jm.parameters.REPO = 'JavaPosseRoundup'

        when:
        Set<GeneratedJob> results = new DslScriptLoader(jm).runScripts([new ScriptRequest(sampleVarDsl)]).jobs

        then:
        results != null
        results.size() == 1
        jm.savedConfigs.size() == 1
        def firstJob = jm.savedConfigs['PROJ-JavaPosseRoundup']
        firstJob != null
    }

    private final sampleTemplate = '''<?xml version='1.0' encoding='UTF-8'?>
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

    private final sampleMavenTemplate = '''<?xml version='1.0' encoding='UTF-8'?>
<maven2-moduleset>
    <actions/>
    <description></description>
    <keepDependencies>false</keepDependencies>
    <properties/>
    <canRoam>true</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
    <triggers/>
    <concurrentBuild>false</concurrentBuild>
    <aggregatorStyleBuild>true</aggregatorStyleBuild>
    <incrementalBuild>false</incrementalBuild>
    <ignoreUpstremChanges>true</ignoreUpstremChanges>
    <archivingDisabled>true</archivingDisabled>
    <resolveDependencies>false</resolveDependencies>
    <processPlugins>false</processPlugins>
    <mavenValidationLevel>-1</mavenValidationLevel>
    <runHeadless>true</runHeadless>
    <publishers/>
    <buildWrappers/>
    <rootPOM>my_module/pom.xml</rootPOM>
    <goals>clean verify</goals>
    <mavenOpts>-Xmx1024m</mavenOpts>
    <scm class='hudson.plugins.git.GitSCM'>
        <configVersion>2</configVersion>
        <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
        <gitTool>Default</gitTool>
        <userRemoteConfigs>
            <hudson.plugins.git.UserRemoteConfig>
                <url>git://github.com/JavaPosseRoundup/job-dsl-plugin.git</url>
            </hudson.plugins.git.UserRemoteConfig>
        </userRemoteConfigs>
        <branches>
            <hudson.plugins.git.BranchSpec>
                <name>**</name>
            </hudson.plugins.git.BranchSpec>
        </branches>
    </scm>
</maven2-moduleset>
'''

    private final sampleVarDsl = '''
job("PROJ-${REPO}") {
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

    private final sampleDsl = '''
def gitUrl = 'git://github.com/JavaPosseRoundup/job-dsl-plugin.git'

job('PROJ-unit-tests') {
    using 'TMPL-test'
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

job('PROJ-sonar') {
    using 'TMPL-test'
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
job('PROJ-integ-tests') {
    using 'TMPL-test'
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

job('PROJ-release') {
    // No template, not needed
    scm {
        git(gitUrl)
    }
    steps {
        maven('release')
        shell('cleanup.sh')
    }
}

mavenJob('PROJ-maven') {
    rootPOM 'my_module/pom.xml'
    goals 'clean verify'
    mavenOpts '-Xmx1024m'
    archivingDisabled true
    runHeadless true
    scm {
        git(gitUrl)
    }
    triggers {
        snapshotDependencies true
    }
}

mavenJob('PROJ-maven-with-template') {
    using 'TMPL-test-maven'
    rootPOM 'other_module/pom.xml'
    goals 'clean'
    goals 'install'
    mavenOpts '-Xms128m'
    mavenOpts '-Xmx512m'
    jdk 'JDK1.7.0_12'
}
'''

    private final mavenXml = '''
<maven2-moduleset>
    <actions/>
    <description></description>
    <keepDependencies>false</keepDependencies>
    <properties/>
    <canRoam>true</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
    <triggers/>
    <concurrentBuild>false</concurrentBuild>
    <aggregatorStyleBuild>true</aggregatorStyleBuild>
    <incrementalBuild>false</incrementalBuild>
    <ignoreUpstremChanges>false</ignoreUpstremChanges>
    <archivingDisabled>true</archivingDisabled>
    <resolveDependencies>false</resolveDependencies>
    <processPlugins>false</processPlugins>
    <mavenValidationLevel>-1</mavenValidationLevel>
    <runHeadless>true</runHeadless>
    <publishers/>
    <buildWrappers/>
    <rootPOM>my_module/pom.xml</rootPOM>
    <goals>clean verify</goals>
    <mavenOpts>-Xmx1024m</mavenOpts>
    <scm class='hudson.plugins.git.GitSCM'>
        <configVersion>2</configVersion>
        <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
        <gitTool>Default</gitTool>
        <userRemoteConfigs>
            <hudson.plugins.git.UserRemoteConfig>
                <url>git://github.com/JavaPosseRoundup/job-dsl-plugin.git</url>
            </hudson.plugins.git.UserRemoteConfig>
        </userRemoteConfigs>
        <branches>
            <hudson.plugins.git.BranchSpec>
                <name>**</name>
            </hudson.plugins.git.BranchSpec>
        </branches>
        <extensions>
            <hudson.plugins.git.extensions.impl.PerBuildTag/>
        </extensions>
    </scm>
</maven2-moduleset>
'''

    private final mavenXmlWithTemplate = '''
<maven2-moduleset>
    <actions/>
    <description></description>
    <keepDependencies>false</keepDependencies>
    <properties/>
    <canRoam>true</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
    <triggers/>
    <concurrentBuild>false</concurrentBuild>
    <aggregatorStyleBuild>true</aggregatorStyleBuild>
    <incrementalBuild>false</incrementalBuild>
    <ignoreUpstremChanges>true</ignoreUpstremChanges>
    <archivingDisabled>true</archivingDisabled>
    <resolveDependencies>false</resolveDependencies>
    <processPlugins>false</processPlugins>
    <mavenValidationLevel>-1</mavenValidationLevel>
    <runHeadless>true</runHeadless>
    <publishers/>
    <buildWrappers/>
    <rootPOM>other_module/pom.xml</rootPOM>
    <goals>clean install</goals>
    <mavenOpts>-Xms128m -Xmx512m</mavenOpts>
    <jdk>JDK1.7.0_12</jdk>
    <scm class='hudson.plugins.git.GitSCM'>
        <configVersion>2</configVersion>
        <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
        <gitTool>Default</gitTool>
        <userRemoteConfigs>
            <hudson.plugins.git.UserRemoteConfig>
                <url>git://github.com/JavaPosseRoundup/job-dsl-plugin.git</url>
            </hudson.plugins.git.UserRemoteConfig>
        </userRemoteConfigs>
        <branches>
            <hudson.plugins.git.BranchSpec>
                <name>**</name>
            </hudson.plugins.git.BranchSpec>
        </branches>
    </scm>
</maven2-moduleset>
'''
}
