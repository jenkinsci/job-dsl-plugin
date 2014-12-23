package javaposse.jobdsl.dsl

import spock.lang.Specification
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

/**
 * Attempt to execute the sample page in README.md
 */
class DslSampleSpec extends Specification {

    def 'load sample dsl'() {
        setup:
        StringJobManagement jm = new StringJobManagement()
        jm.addConfig('TMPL-test', sampleTemplate)
        jm.addConfig('TMPL-test-maven', sampleMavenTemplate)

        when:
        Set<GeneratedJob> results = DslScriptLoader.runDslEngine(sampleDsl, jm).jobs

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

    def 'load sample promotions dsl'() {
        setup:
        StringJobManagement jm = new StringJobManagement()
        jm.addConfig('TMPL-test', sampleTemplate)
        jm.addConfig('TMPL-test-maven', sampleMavenTemplate)

        when:
        GeneratedItems results = DslScriptLoader.runDslEngine(samplePromotionsDsl, jm)

        then:
        results != null
        results.jobs.size() == 1
        jm.savedConfigs.size() == 1
        def firstJob = jm.savedConfigs['promos']
        firstJob != null
        // TODO Review actual results
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + promotionJobXml, firstJob

        // Promotions
        jm.savedConfigsPromotions.size() == 1
        def firstConfigs = jm.savedConfigsPromotions['promos']
        def devConfig = firstConfigs[new JobConfigId(ItemType.ADDITIONAL, 'promotions/dev')]
        // TODO Review actual results
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + promotionXml, devConfig
    }

    def 'use parameters when loading script'() {
        setup:
        StringJobManagement jm = new StringJobManagement()
        jm.params.gitUrl = 'git://github.com/JavaPosseRoundup/job-dsl-plugin.git'
        jm.params.REPO = 'JavaPosseRoundup'

        when:
        Set<GeneratedJob> results = DslScriptLoader.runDslEngine(sampleVarDsl, jm).jobs

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
    <triggers class="vector"/>
    <concurrentBuild>false</concurrentBuild>
    <aggregatorStyleBuild>true</aggregatorStyleBuild>
    <incrementalBuild>false</incrementalBuild>
    <perModuleEmail>false</perModuleEmail>
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
        <disableSubmodules>false</disableSubmodules>
        <recursiveSubmodules>false</recursiveSubmodules>
        <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
        <authorOrCommitter>false</authorOrCommitter>
        <clean>false</clean>
        <wipeOutWorkspace>false</wipeOutWorkspace>
        <pruneBranches>false</pruneBranches>
        <remotePoll>false</remotePoll>
        <ignoreNotifyCommit>false</ignoreNotifyCommit>
        <gitTool>Default</gitTool>
        <skipTag>false</skipTag>
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
    private final samplePromotionsDsl = '''
job(type:'Maven') {
    name('promos')
    promotions {
        promotion('dev') {
            conditions {
                manual('name')
            }
            actions {
                shell('bring nach test')
            }
        }
        promotion('test') {
            conditions {
                manual('name')
            }
        }
    }
}
'''

    private final sampleDsl = '''
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

job(type: Maven) {
    name 'PROJ-maven'
    rootPOM 'my_module/pom.xml'
    goals 'clean verify'
    mavenOpts '-Xmx1024m'
    perModuleEmail true
    archivingDisabled true
    runHeadless true
    scm {
        git(gitUrl)
    }
    triggers {
        snapshotDependencies true
    }
}

job(type: Maven) {
    using 'TMPL-test-maven'
    name 'PROJ-maven-with-template'
    rootPOM 'other_module/pom.xml'
    goals 'clean'
    goals 'install'
    mavenOpts '-Xms128m'
    mavenOpts '-Xmx512m'
    perModuleEmail true
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
    <triggers class="vector"/>
    <concurrentBuild>false</concurrentBuild>
    <aggregatorStyleBuild>true</aggregatorStyleBuild>
    <incrementalBuild>false</incrementalBuild>
    <perModuleEmail>true</perModuleEmail>
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
        <disableSubmodules>false</disableSubmodules>
        <recursiveSubmodules>false</recursiveSubmodules>
        <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
        <authorOrCommitter>false</authorOrCommitter>
        <clean>false</clean>
        <wipeOutWorkspace>false</wipeOutWorkspace>
        <pruneBranches>false</pruneBranches>
        <remotePoll>false</remotePoll>
        <ignoreNotifyCommit>false</ignoreNotifyCommit>
        <gitTool>Default</gitTool>
        <skipTag>false</skipTag>
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
    <triggers class="vector"/>
    <concurrentBuild>false</concurrentBuild>
    <aggregatorStyleBuild>true</aggregatorStyleBuild>
    <incrementalBuild>false</incrementalBuild>
    <perModuleEmail>true</perModuleEmail>
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
        <disableSubmodules>false</disableSubmodules>
        <recursiveSubmodules>false</recursiveSubmodules>
        <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
        <authorOrCommitter>false</authorOrCommitter>
        <clean>false</clean>
        <wipeOutWorkspace>false</wipeOutWorkspace>
        <pruneBranches>false</pruneBranches>
        <remotePoll>false</remotePoll>
        <ignoreNotifyCommit>false</ignoreNotifyCommit>
        <gitTool>Default</gitTool>
        <skipTag>false</skipTag>
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
    private final promotionJobXml = '''
<maven2-moduleset>
    <actions></actions>
    <description></description>
    <keepDependencies>false</keepDependencies>
    <properties>
        <hudson.plugins.promoted__builds.JobPropertyImpl plugin='promoted-builds@2.15'>
            <activeProcessNames>
                <string>dev</string>
                <string>test</string>
            </activeProcessNames>
        </hudson.plugins.promoted__builds.JobPropertyImpl>
    </properties>
    <scm class='hudson.scm.NullSCM'></scm>
    <canRoam>true</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
    <triggers class='vector'></triggers>
    <concurrentBuild>false</concurrentBuild>
    <aggregatorStyleBuild>true</aggregatorStyleBuild>
    <incrementalBuild>false</incrementalBuild>
    <perModuleEmail>false</perModuleEmail>
    <ignoreUpstremChanges>true</ignoreUpstremChanges>
    <archivingDisabled>false</archivingDisabled>
    <resolveDependencies>false</resolveDependencies>
    <processPlugins>false</processPlugins>
    <mavenValidationLevel>-1</mavenValidationLevel>
    <runHeadless>false</runHeadless>
    <publishers></publishers>
    <buildWrappers></buildWrappers>
</maven2-moduleset>
'''

    private final promotionXml = '''
<hudson.plugins.promoted__builds.PromotionProcess plugin='promoted-builds@2.15'>
    <actions></actions>
    <keepDependencies>false</keepDependencies>
    <properties></properties>
    <scm class='hudson.scm.NullSCM'></scm>
    <canRoam>false</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
    <triggers></triggers>
    <concurrentBuild>false</concurrentBuild>
    <icon></icon>
    <conditions>
        <hudson.plugins.promoted__builds.conditions.ManualCondition>
            <users>name</users>
            <parameterDefinitions></parameterDefinitions>
        </hudson.plugins.promoted__builds.conditions.ManualCondition>
    </conditions>
    <buildSteps>
        <hudson.tasks.Shell>
            <command>bring nach test</command>
        </hudson.tasks.Shell>
    </buildSteps>
</hudson.plugins.promoted__builds.PromotionProcess>
'''
}
