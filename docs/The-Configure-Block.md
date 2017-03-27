# Overview

Configure blocks are used inside the Job DSL to give direct access to underlying XML of the Jenkins config.xml. The
blocks are closures which get a `groovy.util.Node` passed in. The XML element represents by the node depends on the
context of the configure block.

To use configure blocks at the job or view level, the `configure` DSL method must be used. Here the root element is
passed in. For free-style jobs that is `<project>`, but for other job types the name of the root element differs, e.g.
`<maven2-moduleset>` for Maven projects.

```groovy
job('example-1') {
    configure { node ->
        // node represents <project>
    }
}

mavenJob('example-2') {
    configure { node ->
        // node represents <maven2-moduleset>
    }
}

listView('example') {
    configure { node ->
        // node represents <hudson.model.ListView>
    }
}
```

Several DSL methods define their own `configure` methods, so that the XML can be manipulated e.g. to add elements which
are not supported by the DSL.

```groovy
job('example') {
    scm {
        git {
            remote {
                url('git@server:account/repo1.git')
            }
            configure { node ->
                // node represents <hudson.plugins.git.GitSCM>
            }
        }
    }
}
```

Other DSL elements allow configure blocks to be used directly without a `configure` method.

```groovy
job('example') {
    steps {
        gradle('build', '', true) { node ->
            // node represents <hudson.plugins.gradle.Gradle>
        }
    }
}
```

The `configure` method can be stated multiple times and configure blocks are run in the order they are provided.

See the [API Viewer](https://jenkinsci.github.io/job-dsl-plugin/) for details about the configure blocks supported by
DSL methods.

[This article](http://www.devexp.eu/2014/10/26/use-unsupported-jenkins-plugins-with-jenkins-dsl/) provides a
step-by-step tutorial on how to create a configure block.

# Transforming XML

All standard documentation for [Node](http://docs.groovy-lang.org/latest/html/gapi/groovy/util/Node.html) applies here,
but transforming XML via Node is no fun, and quite ugly. The general
Groovy use-cases are to consume this structure or build it up via
[NodeBuilder](http://docs.groovy-lang.org/latest/html/gapi/groovy/util/NodeBuilder.html). Use the samples below to help
navigate the Jenkins XML, but keep in mind that the actual syntax is Groovy syntax. See
[Processing XML](http://groovy-lang.org/processing-xml.html) for more information about manipulating XML with Groovy.

Things to keep in mind:

* All queries (methodMissing or find) assume that a NodeList is being returned, so if you need a single Node get the
  first element, e.g. [0]
* `+` adds siblings, so once we have one node, you can keep adding siblings, but will need an initial peer to add to.
* Children can be easily accessed if they exist, if they don't exist you have to append them. This means accessing deep
  trees is laborious.
* Groovy key words (e.g. `switch`), methods each object inherits (e.g. `properties`) and element names
  containing operators (e.g. `.`) must be put into quotes:

```groovy
configure {
    it / 'properties' / 'com.example.Test' {
        'switch'('on')
    }
}
```

To ease navigation, two key operators have been overridden. Try to use them as much as possible:

* `/` - finds a child node by name, always returning the first child. If no child exists, one will be created. E.g.
  `project / description` will find the description node, creating it if it doesn't exist. If attributes are specified,
  it will find the first child which carries those attributes. It has a very low precedence
  in the order of operation, so you need to wrap parenthesis around some operations.
* `<<` - appends as a child. If a Node is provided, it is directly added. A string is created as a node. A closure is
  processed like a NodeBuilder, allowing many nodes to be appended.

Note that these operators are not available when Jenkins security is enabled and the DSL script is running in the
restricted sandbox, use the [Node](http://docs.groovy-lang.org/latest/html/gapi/groovy/util/Node.html) API instead.

# Reusable Configure Blocks

To reuse an configure block for many jobs, the configure block can be refactored into a helper function.

```groovy
def switchOn = {
    it / 'properties' / 'com.example.Test' {
        'switch'('on')
    }
}
               
job('example-1') {
    configure switchOn
}

job('example-2') {
    configure switchOn
}
```

To create a reusable, parametrized configure block, the configure block can be recreated in a parametrized function.

```groovy
Closure switchOnOrOff(String value) {
    return {
        it / 'properties' / 'com.example.Test' {
            'switch'(value)
        }
    }
}
               
job('example-1') {
    configure switchOnOrOff('on')
}

job('example-2') {
    configure switchOnOrOff('off')
}
```

To reuse an configure block for many scripts, the configure block can be moved to a helper class.

```groovy
package helpers

class JobHelper {
    static Closure switchOnOrOff(String value) {
        return {
            it / 'properties' / 'com.example.Test' {
                'switch'(value)
            }
        }
    }
}
```

In the job script you can then import the helper method and use it create several matrix jobs.

```groovy
import static helpers.JobHelper.switchOnOrOff

job('example-1') {
    configure switchOnOrOff('on')
}

job('example-2') {
    configure switchOnOrOff('off')
}
```

# Troubleshooting

## ConcurrentModificationException

If you are getting a ConcurrentModificationException when using the configure block, the XML element that you are trying
to generate might have a conflict with other (possibly undocumented and internal) DSL method names. To avoid this, you
can use the closure's `delegate` variable.

Below is an example of a configure block that is trying to generate `<label>my-label</label>`.
The label element is conflicting with the [label](https://jenkinsci.github.io/job-dsl-plugin/#path/job-label) DSL
method.

```groovy
job('example') {
    configure { project ->
        project << foo {
            bar {
                delegate.label('my-label') // use the delegate variable here
            }
        }
    }
}
```

## Append (<<) Not Working With Attributes

The behavior of the `/` operator changes when specifying attributes on the right side. In this case the `/` does not
only find or create the element on the right side of the operator, but also replaces children and attributes of the node
on the left side with children and attributes from the node on the right side.

The following example demonstrates the problem:

```groovy
job('example') {
    configure {
        it / scm(class: 'org.MyScm') << 'aChild' {
            serverUrl('http://example.org/product-a')
        }
        it / scm(class: 'org.MyScm') << 'aChild' {
            serverUrl('http://example.org/product-b')
        }
    }
}
```

It will not generate two `aChild` elements within the `scm` element, but only one since the second `/` operator will
replace the content appended above. The outcome looks like this:

```xml
<project>
   <scm class='org.MyScm'>
        <aChild>
            <serverUrl>http://example.org/product-a</serverUrl>
        </aChild>
    </scm>
</project>
```

To avoid the problem, the selected node can be assigned to a variable, so that the `/` operator will only be used once:

```groovy
job('example') {
    configure {
        def scm = it / scm(class: 'org.MyScm')
        scm << 'aChild' {
            serverUrl('http://example.org/product-a')
        }
        scm << 'aChild' {
            serverUrl('http://example.org/product-b')
        }
    }
}
```

This change leads to the desired result:

```xml
<project>
    <scm class='org.MyScm'>
        <aChild>
            <serverUrl>http://example.org/product-a</serverUrl>
        </aChild>
        <aChild>
            <serverUrl>http://example.org/product-b</serverUrl>
        </aChild>
    </scm>
</project>
```

See also [JENKINS-41958](https://issues.jenkins-ci.org/browse/JENKINS-41958).

# Samples

Here is an (inelegant) configure block which may explain what Groovy sees, e.g.

```groovy
configure {
    // "it" is a groovy.util.Node
    //    representing the job's config.xml's root "project" element.
    // anotherNode is also groovy.util.Node
    //    obtained with the overloaded "/" operator
    //    on which we can call "setValue(...)"
    def aNode = it
    def anotherNode = aNode / 'blockBuildWhenDownstreamBuilding'
    anotherNode.setValue('true')

    // You can chain these steps,
    //    but must add wrapping parenthesis
    //    because the "/" has a very low precedence (lower than the ".")
    (it / 'blockBuildWhenUpstreamBuilding').setValue('true')
}
```

Some of the sample here have been integrated into the DSL over time. It's highly recommended to use a DSL command if
available.

For brevity, the samples show only the part of the job configuration that is relevant for the shown configure block. To
get a runnable DSL script, further methods have to be added, like setting the job's name by using `name()`.

## Add permissions

Configure block:
```groovy
job('example') {
    configure { project ->
        def matrix = project / 'properties' / 'hudson.security.AuthorizationMatrixProperty' {
            permission('hudson.model.Item.Configure:jill')
            permission('hudson.model.Item.Configure:jack')
        }
        matrix.appendNode('permission', 'hudson.model.Run.Delete:jryan')
    }
}
```

Result:
```xml
<project>
    <properties>
        <hudson.security.AuthorizationMatrixProperty>
            <permission>hudson.model.Item.Configure:jill</permission>
            <permission>hudson.model.Item.Configure:jack</permission>
            <permission>hudson.model.Run.Delete:jryan</permission>
        </hudson.security.AuthorizationMatrixProperty>
    </properties>
</project>
```

DSL:
```groovy
job('example') {
    authorization {
        permission(Permissions.ItemConfigure, 'jill')
        permission(Permissions.ItemConfigure, 'jack')
        permission(Permissions.RunDelete, 'jryan')
    }
}
```

## Configure Log Rotator Plugin

Configure block:
```groovy
job('example') {
    configure { project ->
        // Doesn't take into account existing node
        project << logRotator {
            daysToKeep(-1)
            numToKeep(10)
            artifactDaysToKeep(-1)
            artifactNumToKeep(-1)
        }

        // Alters existing value
        (project / logRotator / daysToKeep).value = 2
    }
}
```

Result:
```xml
<project>
    <logRotator>
        <daysToKeep>2</daysToKeep>
        <numToKeep>10</numToKeep>
        <artifactDaysToKeep>-1</artifactDaysToKeep>
        <artifactNumToKeep>-1</artifactNumToKeep>
    </logRotator>
</project>
```

DSL:
```groovy
job('example') {
    logRotator(2, 10, -1, -1)
}
```

## Configure Email Notification

Configure block:
```groovy
def emailTrigger = {
    trigger {
        email {
            recipientList '$PROJECT_DEFAULT_RECIPIENTS'
            subject '$PROJECT_DEFAULT_SUBJECT'
            body '$PROJECT_DEFAULT_CONTENT'
            sendToDevelopers true
            sendToRequester false
            includeCulprits false
            sendToRecipientList true
        }
    }
}

job('example') {
    configure { project ->
        project / publisher << 'hudson.plugins.emailext.ExtendedEmailPublisher' {
              recipientList 'Engineering@company.com'
              configuredTriggers {
                  'hudson.plugins.emailext.plugins.trigger.FailureTrigger' emailTrigger
                  'hudson.plugins.emailext.plugins.trigger.FixedTrigger' emailTrigger
              }
              contentType 'default'
              defaultSubject '$DEFAULT_SUBJECT'
              defaultContent '$DEFAULT_CONTENT'
        }
    }
}
```

Result:
```xml
<project>
    <publisher>
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
</project>
```

DSL:
```groovy
job('example') {
    publishers {
        extendedEmail('Engineering@company.com') {
            trigger(triggerName: 'Failure', recipientList: '$PROJECT_DEFAULT_RECIPIENTS')
            trigger(triggerName: 'Fixed', recipientList: '$PROJECT_DEFAULT_RECIPIENTS')
        }
    }
}
```

## Configure Shell Step

Configure block:
```groovy
job('example') {
    configure { project ->
        project / builders / 'hudson.tasks.Shell' {
            command 'echo "Hello" > ${WORKSPACE}/out.txt'
        }
    }
}
```

Result:
```xml
<project>
    <builders>
        <hudson.tasks.Shell>
            <command>echo "Hello" &gt; ${WORKSPACE}/out.txt</command>
        </hudson.tasks.Shell>
    </builders>
</project>
```

DSL:
```groovy
job('example') {
    steps {
        shell 'echo "Hello" > ${WORKSPACE}/out.txt'
    }
}
```

## Configure Gradle

Configure block:
```groovy
job('example') {
    configure { project ->
        project / builders << 'hudson.plugins.gradle.Gradle' {
            description ''
            switches '-Dtiming-multiple=5'
            tasks 'test'
            rootBuildScriptDir ''
            buildFile ''
            useWrapper 'true'
            wrapperScript 'gradlew'
        }
    }
}
```

Result:
```xml
<project>
    <builders>
        <hudson.plugins.gradle.Gradle>
            <description/>
            <switches>-Dtiming-multiple=5</switches>
            <tasks>test</tasks>
            <rootBuildScriptDir/>
            <buildFile/>
            <useWrapper>true</useWrapper>
            <wrapperScript>gradlew</wrapperScript>
        </hudson.plugins.gradle.Gradle>
    </builders>
</project>
```

DSL:
```groovy
job('example') {
    steps {
        gradle('test', '-Dtiming-multiple-5', true) {
            it / wrapperScript 'gradlew'
        }
    }
}
```

## Configure SVN

Configure block:
```groovy
job('example') {
    configure { project ->
        project.remove(project / scm) // remove the existing 'scm' element
        project / scm(class: 'hudson.scm.SubversionSCM') {
            locations {
                'hudson.scm.SubversionSCM_-ModuleLocation' {
                    remote 'http://svn.apache.org/repos/asf/tomcat/maven-plugin/trunk'
                    local '.'
                }
            }
            excludedRegions ''
            includedRegions ''
            excludedUsers ''
            excludedRevprop ''
            excludedCommitMessages ''
            workspaceUpdater(class: "hudson.scm.subversion.UpdateUpdater")
        }
    }
}
```

Result:
```xml
<project>
    <scm class="hudson.scm.SubversionSCM">
        <locations>
            <hudson.scm.SubversionSCM_-ModuleLocation>
                <remote>http://svn.apache.org/repos/asf/tomcat/maven-plugin/trunk</remote>
                <local>.</local>
            </hudson.scm.SubversionSCM_-ModuleLocation>
        </locations>
        <excludedRegions/>
        <includedRegions/>
        <excludedUsers/>
        <excludedRevprop/>
        <excludedCommitMessages/>
        <workspaceUpdater class="hudson.scm.subversion.UpdateUpdater"/>
    </scm>
</project>
```

DSL:
```groovy
job('example') {
    scm {
        svn('http://svn.apache.org/repos/asf/tomcat/maven-plugin/trunk')
    }
}
```

## Configure GIT

Configure block:
```groovy
def gitConfigWithSubdir(subdir, remote) {
    { node ->
        // use remote name given
        node / 'userRemoteConfigs' / 'hudson.plugins.git.UserRemoteConfig' / name(remote)

        // use local dir given
        node / 'extensions' << 'hudson.plugins.git.extensions.impl.RelativeTargetDirectory' {
            relativeTargetDir subdir
        }

        // clean after checkout
        node / 'extensions' << 'hudson.plugins.git.extensions.impl.CleanCheckout'()
    }
}

job('example') {
    scm {
        git(
            'git@server:account/repo1.git',
            'remoteB/master',
            gitConfigWithSubdir('repo1', 'remoteB')
        )
    }
}
```

Result:
```xml
<project>
    <scm class='hudson.plugins.git.GitSCM'>
        <userRemoteConfigs>
            <hudson.plugins.git.UserRemoteConfig>
                <url>git@server:account/repo1.git</url>
                <name>remoteB</name>
            </hudson.plugins.git.UserRemoteConfig>
        </userRemoteConfigs>
        <branches>
            <hudson.plugins.git.BranchSpec>
                <name>remoteB/master</name>
            </hudson.plugins.git.BranchSpec>
        </branches>
        <extensions>
            <hudson.plugins.git.extensions.impl.RelativeTargetDirectory>
                <relativeTargetDir>repo1</relativeTargetDir>
            </hudson.plugins.git.extensions.impl.RelativeTargetDirectory>
            <hudson.plugins.git.extensions.impl.CleanCheckout/>
        </extensions>
    </scm>
</project>
```

DSL:
```groovy
job('example') {
    scm {
        git {
            remote {
                name 'remoteB'
                url 'git@server:account/repo1.git'
            }
            extensions {
                relativeTargetDirectory('repo1')
                cleanAfterCheckout()
            }
        }
    }
}
```

## Configure Matrix Job

Configure block:
```groovy
job('example') {
    configure { project ->
        project.name = 'matrix-project'
        project / axes / 'hudson.matrix.LabelAxis' {
            name 'label'
            values {
                string 'linux'
                string 'windows'
            }
        }
        project / executionStrategy(class: 'hudson.matrix.DefaultMatrixExecutionStrategyImpl') {
            runSequentially false
        }
    }
}
```

Result:
```xml
<matrix-project>
    <actions/>
    <description/>
    <keepDependencies>false</keepDependencies>
    <properties/>
    <scm class="hudson.scm.NullSCM"/>
    <canRoam>true</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
    <triggers class="vector"/>
    <concurrentBuild>false</concurrentBuild>
    <builders/>
    <publishers/>
    <buildWrappers/>
    <axes>
        <hudson.matrix.LabelAxis>
            <name>label</name>
            <values>
                <string>linux</string>
                <string>windows</string>
            </values>
        </hudson.matrix.LabelAxis>
    </axes>
    <executionStrategy class="hudson.matrix.DefaultMatrixExecutionStrategyImpl">
        <runSequentially>false</runSequentially>
    </executionStrategy>
</matrix-project>
```

DSL:
```groovy
matrixJob('example') {
    axes {
        label('label', 'linux', 'windows')
    }
}
```

## Configure Aggregate Downstream Test Result Publisher

Configure block:
```groovy
job('example') {
    configure { project ->
        project / publishers << 'hudson.tasks.test.AggregatedTestResultPublisher' {
            jobs('some-downstream-test')
            includeFailedBuilds(false)
        }
    }
}
```

Result:
```xml
<project>
    <publishers>
        <hudson.tasks.test.AggregatedTestResultPublisher>
            <jobs>some-downstream-test</jobs>
            <includeFailedBuilds>false</includeFailedBuilds>
        </hudson.tasks.test.AggregatedTestResultPublisher>
    </publishers>
</project>
```

DSL:
```groovy
job('example') {
    publishers {
        aggregateDownstreamTestResults 'some-downstream-test'
    }
}
```

## Configure Pre-requisite Build Step

Configure block:
```groovy
job('example') {
    configure { project ->
        project / builders / 'dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder' {
            projects('project-A,project-B') // no spaces, plugin doesn't handle trimming
            warningOnly(false)
        }
    }
}
```

Result:
```xml
<project>
    <builders>
        <dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder>
            <projects>project-A,project-B</projects>
            <warningOnly>false</warningOnly>
        </dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder>
    </builders>
</project>
```

DSL:
```groovy
job('example') {
    steps {
        prerequisite('project-A, project-B') // any spaces will be trimmed
    }
}
```

## Configure Post Build Task Publisher

Configure block:
```groovy
job('example') {
    configure { project ->
        def postbuildTask = project / publishers / 'hudson.plugins.postbuildtask.PostbuildTask'
        postbuildTask / tasks << 'hudson.plugins.postbuildtask.TaskProperties' {
            logTexts {
                'hudson.plugins.postbuildtask.LogProperties' {
                    logText('BUILD SUCCESSFUL')
                    operator('AND')
                }
            }
            EscalateStatus(false)
            RunIfJobSuccessful(false)
            script('git clean -fdx')
        }
    }
}
```

Result:
```xml
<project>
    <publishers>
        <hudson.plugins.postbuildtask.PostbuildTask>
            <tasks>
                <hudson.plugins.postbuildtask.TaskProperties>
                    <logTexts>
                        <hudson.plugins.postbuildtask.LogProperties>
                            <logText>BUILD SUCCESSFUL</logText>
                            <operator>AND</operator>
                        </hudson.plugins.postbuildtask.LogProperties>
                    </logTexts>
                    <EscalateStatus>false</EscalateStatus>
                    <RunIfJobSuccessful>false</RunIfJobSuccessful>
                    <script>git clean -fdx</script>
                </hudson.plugins.postbuildtask.TaskProperties>
            </tasks>
        </hudson.plugins.postbuildtask.PostbuildTask>
    </publishers>
</project>
```

DSL:
```groovy
job('example') {
    publishers {
        postBuildTask {
            task('BUILD SUCCESSFUL', 'git clean -fdx')
        }
    }
}
```

## Configure Post Build Sonar Task

In order to trigger a Sonar analysis as a Post Build Task in your job you can use the following code.
The requirements are:

* Have a Sonar Server installed somewhere.
* Have installed the Sonar plugin in Jenkins.
* Configured access to the Sonar Server in Jenkins (Jenkins >> Manage Jenkins >> configure >> Sonar)

Configure block:
```groovy
job('example') {
    configure { project ->
        project / publishers << 'hudson.plugins.sonar.SonarPublisher' {
            jdk('(Inherit From Job)')
            branch()
            language()
            mavenOpts()
            jobAdditionalProperties()
            settings(class: 'jenkins.mvn.DefaultSettingsProvider')
            globalSettings(class: 'jenkins.mvn.DefaultGlobalSettingsProvider')
            usePrivateRepository(false)
        }
    }
}
```

Result:
```xml
<project>
    <publishers>
        <hudson.plugins.sonar.SonarPublisher>
            <jdk>(Inherit From Job)</jdk>
            <branch/>
            <language/>
            <mavenOpts/>
            <jobAdditionalProperties/>
            <settings class="jenkins.mvn.DefaultSettingsProvider"/>
            <globalSettings class="jenkins.mvn.DefaultGlobalSettingsProvider"/>
            <usePrivateRepository>false</usePrivateRepository>
        </hudson.plugins.sonar.SonarPublisher>
    </publishers>
</project>
```

## Configure Post Build Artifactory Task

In order to publish the result of your job into an Artifactory Repository as a Post Build Task you can use the following
code. The requirements are:

* Have an Artifactory Repository installed somewhere.
* Have installed the Artifactory plugin in Jenkins.
* Configured access to the Artifactory Repository in Jenkins (Jenkins >> Manage Jenkins >> configure >> Artifactory)

Once the Artifactory Repository is configured you will need to get the "artifactoryName" property which will be
necessary to properly configure your jobs. This property is the "serverId" input element found in the configuration
page. It is an hidden element.

Configure block:
```groovy
job('example') {
    configure { project ->
        project / publishers << 'org.jfrog.hudson.ArtifactoryRedeployPublisher' {
            details {
                artifactoryUrl('http://artifactory.server.com/artifactory')
                artifactoryName('925330@138764814') // <= You will need to change that to fit your setup
                repositoryKey('libs-release-local')
                snapshotsRepositoryKey('libs-snapshot-local')
            }
            deployBuildInfo(false)
            deployArtifacts(true)
            evenIfUnstable(true)
        }
    }
}
```

Result:
```xml
<project>
    <publishers>
        <org.jfrog.hudson.ArtifactoryRedeployPublisher>
            <details>
                <artifactoryUrl>http://artifactory.server.com/artifactory</artifactoryUrl>
                <artifactoryName>920955330@1387638748614</artifactoryName>
                <repositoryKey>libs-release-local</repositoryKey>
                <snapshotsRepositoryKey>libs-snapshot-local</snapshotsRepositoryKey>
            </details>
            <deployBuildInfo>false</deployBuildInfo>
            <deployArtifacts>true</deployArtifacts>
            <evenIfUnstable>true</evenIfUnstable>
        </org.jfrog.hudson.ArtifactoryRedeployPublisher>
    </publishers>
</project>
```

## Configure Post Build Confluence Publisher

In order to update Confluence Pages, you can use the Confluence Publisher.
The requirements are:

* Have a Confluence Wiki installed.
* Install Confluence Publisher plugin in Jenkins.
* Configure access to Confluence in Jenkins (Jenkins >> Manage Jenkins >> configure >> Confluence Publisher)

Configure block:
```groovy
job('example') {
    configure { project ->
        project / publishers << 'com.myyearbook.hudson.plugins.confluence.ConfluencePublisher' {
            siteName('confluence.company.com')
            attachArchivedArtifacts(false)
            buildIfUnstable(false)
            spaceName('TEST')
            pageName('Jenkins Confluence Publisher Integration Test')
            editors {
                'com.myyearbook.hudson.plugins.confluence.wiki.editors.PrependEditor' {
                    generator(class: 'com.myyearbook.hudson.plugins.confluence.wiki.generators.PlainTextGenerator') {
                        text('Jenkins Publisher Job Build Number: $BUILD_NUMBER')
                    }                
                }
                'com.myyearbook.hudson.plugins.confluence.wiki.editors.AppendEditor' {
                    generator(class: 'com.myyearbook.hudson.plugins.confluence.wiki.generators.PlainTextGenerator') {
                        text('Jenkins Publisher Job Build Number: $BUILD_NUMBER')
                    }                
                }
            }
        }
    }
}
```

Result:
```xml
<project>
    <publishers>
        <com.myyearbook.hudson.plugins.confluence.ConfluencePublisher>
            <siteName>confluence.company.com</siteName>
            <attachArchivedArtifacts>false</attachArchivedArtifacts>
            <buildIfUnstable>false</buildIfUnstable>
            <spaceName>TEST</spaceName>
            <pageName>Jenkins Confluence Publisher Integration Test</pageName>
            <editors>
                <com.myyearbook.hudson.plugins.confluence.wiki.editors.PrependEditor>
                    <generator class='com.myyearbook.hudson.plugins.confluence.wiki.generators.PlainTextGenerator'>
                        <text>Jenkins Publisher Job Build Number: $BUILD_NUMBER</text>
                    </generator>
                </com.myyearbook.hudson.plugins.confluence.wiki.editors.PrependEditor>
                <com.myyearbook.hudson.plugins.confluence.wiki.editors.AppendEditor>
                    <generator class='com.myyearbook.hudson.plugins.confluence.wiki.generators.PlainTextGenerator'>
                        <text>Jenkins Publisher Job Build Number: $BUILD_NUMBER</text>
                    </generator>
                </com.myyearbook.hudson.plugins.confluence.wiki.editors.AppendEditor>
            </editors>
        </com.myyearbook.hudson.plugins.confluence.ConfluencePublisher>
    </publishers>
</project>
```
