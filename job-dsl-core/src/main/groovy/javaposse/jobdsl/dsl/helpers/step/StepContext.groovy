package javaposse.jobdsl.dsl.helpers.step

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.common.DownstreamContext

import static com.google.common.base.Strings.isNullOrEmpty
import static javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation.LOCAL_TO_WORKSPACE

class StepContext implements Context {
    final List<Node> stepNodes = []
    protected final JobManagement jobManagement

    StepContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    /**
     * <hudson.tasks.Shell>
     *     <command>echo Hello</command>
     * </hudson.tasks.Shell>
     */
    void shell(String commandStr) {
        NodeBuilder nodeBuilder = new NodeBuilder()
        stepNodes << nodeBuilder.'hudson.tasks.Shell' {
            'command' commandStr
        }
    }

    /**
     * <hudson.tasks.BatchFile>
     *     <command>echo Hello from Windows</command>
     * </hudson.tasks.BatchFile>
     */
    void batchFile(String commandStr) {
        NodeBuilder nodeBuilder = new NodeBuilder()
        stepNodes << nodeBuilder.'hudson.tasks.BatchFile' {
            'command' commandStr
        }
    }

    /**
     * <hudson.plugins.gradle.Gradle>
     *     <description/>
     *     <switches/>
     *     <tasks/>
     *     <rootBuildScriptDir/>
     *     <buildFile/>
     *     <gradleName>(Default)</gradleName>
     *     <useWrapper>false</useWrapper>
     *     <makeExecutable>false</makeExecutable>
     *     <fromRootBuildScriptDir>true</fromRootBuildScriptDir>
     * </hudson.plugins.gradle.Gradle>
     */
    void gradle(@DslContext(GradleContext) Closure gradleClosure) {
        GradleContext gradleContext = new GradleContext()
        ContextHelper.executeInContext(gradleClosure, gradleContext)

        Node gradleNode = new NodeBuilder().'hudson.plugins.gradle.Gradle' {
            description gradleContext.description
            switches gradleContext.switches.join(' ')
            tasks gradleContext.tasks.join(' ')
            rootBuildScriptDir gradleContext.rootBuildScriptDir
            buildFile gradleContext.buildFile
            gradleName gradleContext.gradleName
            useWrapper gradleContext.useWrapper
            makeExecutable gradleContext.makeExecutable
            fromRootBuildScriptDir gradleContext.fromRootBuildScriptDir
        }

        if (gradleContext.configureBlock) {
            WithXmlAction action = new WithXmlAction(gradleContext.configureBlock)
            action.execute(gradleNode)
        }

        stepNodes << gradleNode
    }

    void gradle(String tasks = null, String switches = null, Boolean useWrapper = true, Closure configure = null) {
        gradle {
            if (tasks != null) {
                delegate.tasks(tasks)
            }
            if (switches != null) {
                delegate.switches(switches)
            }
            if (useWrapper != null) {
                delegate.useWrapper(useWrapper)
            }
            delegate.configure(configure)
        }
    }

    /**
     * <org.jvnet.hudson.plugins.SbtPluginBuilder plugin="sbt@1.4">
     *     <name>SBT 0.12.3</name>
     *     <jvmFlags>-XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=512M -Dfile.encoding=UTF-8 -Xmx2G -Xms512M</jvmFlags>
     *     <sbtFlags>-Dsbt.log.noformat=true</sbtFlags>
     *     <actions>clean update &quot;env development&quot; test dist publish</actions>
     *     <subdirPath></subdirPath>
     * </org.jvnet.hudson.plugins.SbtPluginBuilder>
     */
    void sbt(String sbtNameArg, String actionsArg = null, String sbtFlagsArg=null,  String jvmFlagsArg=null,
            String subdirPathArg=null, Closure configure = null) {

        NodeBuilder nodeBuilder = new NodeBuilder()

        Node sbtNode = nodeBuilder.'org.jvnet.hudson.plugins.SbtPluginBuilder' {
            name Preconditions.checkNotNull(sbtNameArg, 'Please provide the name of the SBT to use' as Object)
            jvmFlags jvmFlagsArg ?: ''
            sbtFlags sbtFlagsArg ?: ''
            actions actionsArg ?: ''
            subdirPath subdirPathArg ?: ''
        }

        // Apply Context
        if (configure) {
            WithXmlAction action = new WithXmlAction(configure)
            action.execute(sbtNode)
        }

        stepNodes << sbtNode

    }

    /**
     * <javaposse.jobdsl.plugin.ExecuteDslScripts>
     *     <targets>sbt-template.groovy</targets>
     *     <usingScriptText>false</usingScriptText>
     *     <ignoreExisting>false</ignoreExisting>
     *     <removedJobAction>IGNORE</removedJobAction>
     *     <additionalClasspath>libs</additionalClasspath>
     * </javaposse.jobdsl.plugin.ExecuteDslScripts>
     */
    void dsl(@DslContext(javaposse.jobdsl.dsl.helpers.step.DslContext) Closure configure) {
        javaposse.jobdsl.dsl.helpers.step.DslContext context = new javaposse.jobdsl.dsl.helpers.step.DslContext()
        ContextHelper.executeInContext(configure, context)

        stepNodes << new NodeBuilder().'javaposse.jobdsl.plugin.ExecuteDslScripts' {
            targets context.externalScripts.join('\n')
            usingScriptText !isNullOrEmpty(context.scriptText)
            scriptText context.scriptText ?: ''
            ignoreExisting context.ignoreExisting
            removedJobAction context.removedJobAction
            additionalClasspath context.additionalClasspath ?: ''
        }
    }

    void dsl(String scriptText, String removedJobAction = null, boolean ignoreExisting = false) {
        dsl {
            text(scriptText)
            if (removedJobAction) {
                removeAction(removedJobAction)
            }
            delegate.ignoreExisting(ignoreExisting)
        }
    }

    void dsl(Iterable<String> externalScripts, String removedJobAction = null, boolean ignoreExisting = false) {
        dsl {
            external(externalScripts)
            if (removedJobAction) {
                removeAction(removedJobAction)
            }
            delegate.ignoreExisting(ignoreExisting)
        }
    }

    /**
     * <hudson.tasks.Ant>
     *     <targets>target</targets>
     *     <antName>Ant 1.8</antName>
     *     <antOpts>-XX:MaxPermSize=128M -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false</antOpts>
     *     <buildFile>build.xml</buildFile>
     *     <properties>
     *         test.jvmargs=-Xmx=1g
     *         test.maxmemory=2g
     *         multiline=true
     *     </properties>
     * </hudson.tasks.Ant>
     */
    void ant(@DslContext(AntContext) Closure antClosure = null) {
        ant(null, null, null, antClosure)
    }

    void ant(String targetsStr, @DslContext(AntContext) Closure antClosure = null) {
        ant(targetsStr, null, null, antClosure)
    }

    void ant(String targetsStr, String buildFileStr, @DslContext(AntContext) Closure antClosure = null) {
        ant(targetsStr, buildFileStr, null, antClosure)
    }

    void ant(String targetsArg, String buildFileArg, String antInstallation,
             @DslContext(AntContext) Closure antClosure = null) {
        AntContext antContext = new AntContext()
        ContextHelper.executeInContext(antClosure, antContext)

        List<String> targetList = []

        if (targetsArg) {
            targetList.addAll targetsArg.contains('\n') ? targetsArg.split('\n') : targetsArg.split(' ')
        }
        targetList.addAll antContext.targets

        List<String> antOptsList = antContext.antOpts

        List<String> propertiesList = []
        propertiesList += antContext.props

        NodeBuilder nodeBuilder = NodeBuilder.newInstance()
        Node antNode = nodeBuilder.'hudson.tasks.Ant' {
            targets targetList.join(' ')

            antName antInstallation ?: antContext.antName ?: '(Default)'

            if (antOptsList) {
                antOpts antOptsList.join('\n')
            }

            if (buildFileArg || antContext.buildFile) {
                buildFile buildFileArg ?: antContext.buildFile
            }
        }

        if (propertiesList) {
            antNode.appendNode('properties', propertiesList.join('\n'))
        }

        stepNodes << antNode
    }

    /**
     * <hudson.plugins.groovy.Groovy>
     *     <scriptSource class="hudson.plugins.groovy.StringScriptSource">
     *         <command>Command</command>
     *     </scriptSource>
     *     <groovyName>(Default)</groovyName>
     *     <parameters/>
     *     <scriptParameters/>
     *     <properties/>
     *     <javaOpts/>
     *     <classPath/>
     * </hudson.plugins.groovy.Groovy>
     */
    void groovyCommand(String command, @DslContext(GroovyContext) Closure groovyClosure = null) {
        groovy(command, true, null, groovyClosure)
    }

    void groovyCommand(String command, String groovyName, @DslContext(GroovyContext) Closure groovyClosure = null) {
        groovy(command, true, groovyName, groovyClosure)
    }

    /**
     * <hudson.plugins.groovy.Groovy>
     *     <scriptSource class="hudson.plugins.groovy.FileScriptSource">
     *         <scriptFile>acme.groovy</scriptFile>
     *     </scriptSource>
     *     <groovyName>(Default)</groovyName>
     *     <parameters/>
     *     <scriptParameters/>
     *     <properties/>
     *     <javaOpts/>
     *     <classPath/>
     * </hudson.plugins.groovy.Groovy>
     */
    void groovyScriptFile(String fileName, @DslContext(GroovyContext) Closure groovyClosure = null) {
        groovy(fileName, false, null, groovyClosure)
    }

    void groovyScriptFile(String fileName, String groovyName, @DslContext(GroovyContext) Closure groovyClosure = null) {
        groovy(fileName, false, groovyName, groovyClosure)
    }

    protected groovyScriptSource(String commandOrFileName, boolean isCommand) {
        NodeBuilder nodeBuilder = new NodeBuilder()
        nodeBuilder.scriptSource(class: "hudson.plugins.groovy.${isCommand ? 'String' : 'File'}ScriptSource") {
            if (isCommand) {
                command commandOrFileName
            } else {
                scriptFile commandOrFileName
            }
        }
    }

    protected groovy(String commandOrFileName, boolean isCommand, String groovyInstallation, Closure groovyClosure) {
        GroovyContext groovyContext = new GroovyContext()
        ContextHelper.executeInContext(groovyClosure, groovyContext)

        Node groovyNode = NodeBuilder.newInstance().'hudson.plugins.groovy.Groovy' {
            groovyName groovyInstallation ?: groovyContext.groovyInstallation ?: '(Default)'
            parameters groovyContext.groovyParams.join('\n')
            scriptParameters groovyContext.scriptParams.join('\n')
            javaOpts groovyContext.javaOpts.join(' ')
            classPath groovyContext.classpathEntries.join(File.pathSeparator)
        }
        groovyNode.append(groovyScriptSource(commandOrFileName, isCommand))
        groovyNode.appendNode('properties', groovyContext.props.join('\n'))

        stepNodes << groovyNode
    }

    /**
     * <hudson.plugins.groovy.SystemGroovy>
     *     <scriptSource class="hudson.plugins.groovy.StringScriptSource">
     *         <command>System Groovy</command>
     *     </scriptSource>
     *     <bindings/>
     *     <classpath/>
     * </hudson.plugins.groovy.SystemGroovy>
     */
    void systemGroovyCommand(String command, @DslContext(SystemGroovyContext) Closure systemGroovyClosure = null) {
        systemGroovy(command, true, systemGroovyClosure)
    }

    /**
     * <hudson.plugins.groovy.SystemGroovy>
     *     <scriptSource class="hudson.plugins.groovy.FileScriptSource">
     *         <scriptFile>System Groovy</scriptFile>
     *     </scriptSource>
     *     <bindings/>
     *     <classpath/>
     * </hudson.plugins.groovy.SystemGroovy>
     */
    void systemGroovyScriptFile(String fileName, @DslContext(SystemGroovyContext) Closure systemGroovyClosure = null) {
        systemGroovy(fileName, false, systemGroovyClosure)
    }

    protected systemGroovy(String commandOrFileName, boolean isCommand, Closure systemGroovyClosure) {
        SystemGroovyContext systemGroovyContext = new SystemGroovyContext()
        ContextHelper.executeInContext(systemGroovyClosure, systemGroovyContext)

        Node systemGroovyNode = NodeBuilder.newInstance().'hudson.plugins.groovy.SystemGroovy' {
            bindings systemGroovyContext.bindings.collect { key, value -> "${key}=${value}" }.join('\n')
            classpath systemGroovyContext.classpathEntries.join(File.pathSeparator)
        }
        systemGroovyNode.append(groovyScriptSource(commandOrFileName, isCommand))

        stepNodes << systemGroovyNode
    }

    /**
     * <hudson.tasks.Maven>
     *     <targets>install</targets>
     *     <mavenName>(Default)</mavenName>
     *     <jvmOptions>-Xmx512m</jvmOptions>
     *     <pom>pom.xml</pom>
     *     <usePrivateRepository>false</usePrivateRepository>
     * </hudson.tasks.Maven>
     */
    void maven(@DslContext(MavenContext) Closure closure) {
        MavenContext mavenContext = new MavenContext(jobManagement)
        ContextHelper.executeInContext(closure, mavenContext)

        Node mavenNode = new NodeBuilder().'hudson.tasks.Maven' {
            targets mavenContext.goals.join(' ')
            if (mavenContext.properties) {
                properties(mavenContext.properties.collect { key, value -> "${key}=${value}" }.join('\n'))
            }
            mavenName mavenContext.mavenInstallation
            jvmOptions mavenContext.mavenOpts.join(' ')
            if (mavenContext.rootPOM) {
                pom mavenContext.rootPOM
            }
            usePrivateRepository mavenContext.localRepositoryLocation == LOCAL_TO_WORKSPACE
            if (mavenContext.providedSettingsId) {
                settings(class: 'org.jenkinsci.plugins.configfiles.maven.job.MvnSettingsProvider') {
                    settingsConfigId(mavenContext.providedSettingsId)
                }
            }
        }

        // Apply Context
        if (mavenContext.configureBlock) {
            WithXmlAction action = new WithXmlAction(mavenContext.configureBlock)
            action.execute(mavenNode)
        }

        stepNodes << mavenNode
    }

    void maven(String targetsArg = null, String pomArg = null, Closure configure = null) {
        maven {
            delegate.goals(targetsArg)
            delegate.rootPOM(pomArg)
            delegate.configure(configure)
        }
    }

    /**
     * <com.g2one.hudson.grails.GrailsBuilder>
     *     <targets/>
     *     <name>(Default)</name>
     *     <grailsWorkDir/>
     *     <projectWorkDir/>
     *     <projectBaseDir/>
     *     <serverPort/>
     *     <properties/>
     *     <forceUpgrade>false</forceUpgrade>
     *     <nonInteractive>true</nonInteractive>
     *     <useWrapper>false</useWrapper>
     * </com.g2one.hudson.grails.GrailsBuilder>
     */
    void grails(@DslContext(GrailsContext) Closure grailsClosure) {
        grails null, false, grailsClosure
    }

    void grails(String targetsArg, @DslContext(GrailsContext) Closure grailsClosure) {
        grails targetsArg, false, grailsClosure
    }

    void grails(String targetsArg = null, boolean useWrapperArg = false,
                @DslContext(GrailsContext) Closure grailsClosure = null) {
        GrailsContext grailsContext = new GrailsContext(
            useWrapper: useWrapperArg
        )
        ContextHelper.executeInContext(grailsClosure, grailsContext)

        NodeBuilder nodeBuilder = new NodeBuilder()
        Node grailsNode = nodeBuilder.'com.g2one.hudson.grails.GrailsBuilder' {
            targets targetsArg ?: grailsContext.targetsString
            name grailsContext.name
            grailsWorkDir grailsContext.grailsWorkDir
            projectWorkDir grailsContext.projectWorkDir
            projectBaseDir grailsContext.projectBaseDir
            serverPort grailsContext.serverPort
            'properties' grailsContext.propertiesString
            forceUpgrade grailsContext.forceUpgrade.toString()
            nonInteractive grailsContext.nonInteractive.toString()
            useWrapper grailsContext.useWrapper.toString()
        }

        stepNodes << grailsNode
    }

    /**
     * Upstream build that triggered this job
     * <hudson.plugins.copyartifact.CopyArtifact>
     *     <filter>*ivy-locked.xml</filter>
     *     <target>target/</target>
     *     <selector class="hudson.plugins.copyartifact.TriggeredBuildSelector"/>
     *     <flatten>true</flatten>
     *     <optional>true</optional>
     * </hudson.plugins.copyartifact.CopyArtifact>
     *
     * Latest successful build
     * <hudson.plugins.copyartifact.CopyArtifact>
     *     ...
     *     <selector class="hudson.plugins.copyartifact.StatusBuildSelector"/>
     * </hudson.plugins.copyartifact.CopyArtifact>
     *
     * Latest saved build (marked "keep forever")
     * <hudson.plugins.copyartifact.CopyArtifact>
     *     ...
     *     <selector class="hudson.plugins.copyartifact.SavedBuildSelector"/>
     * </hudson.plugins.copyartifact.CopyArtifact>
     *
     * Specified by permalink
     * <hudson.plugins.copyartifact.CopyArtifact>
     *     ...
     *     <selector class="hudson.plugins.copyartifact.PermalinkBuildSelector">
     *         <id>lastBuild</id> <!-- Last Build-->
     *         <id>lastStableBuild</id> <!-- Latest Stable Build -->
     *     </selector>
     * </hudson.plugins.copyartifact.CopyArtifact>
     *
     * Specific Build
     * <hudson.plugins.copyartifact.CopyArtifact>
     *     ...
     *     <selector class="hudson.plugins.copyartifact.SpecificBuildSelector">
     *         <buildNumber>43</buildNumber>
     *     </selector>
     * </hudson.plugins.copyartifact.CopyArtifact>
     *
     * Copy from WORKSPACE of latest completed build
     * <hudson.plugins.copyartifact.CopyArtifact>
     *     ...
     *     <selector class="hudson.plugins.copyartifact.WorkspaceSelector"/>
     * </hudson.plugins.copyartifact.CopyArtifact>
     *
     * Specified by build parameter
     * <hudson.plugins.copyartifact.CopyArtifact>
     *     ...
     *     <selector class="hudson.plugins.copyartifact.ParameterizedBuildSelector">
     *         <parameterName>BUILD_SELECTOR</parameterName>
     *     </selector>
     * </hudson.plugins.copyartifact.CopyArtifact>
     */
    void copyArtifacts(String jobName, String includeGlob,
                       @DslContext(CopyArtifactContext) Closure copyArtifactClosure) {
        copyArtifacts(jobName, includeGlob, '', copyArtifactClosure)
    }

    void copyArtifacts(String jobName, String includeGlob, String targetPath,
                       @DslContext(CopyArtifactContext) Closure copyArtifactClosure) {
        copyArtifacts(jobName, includeGlob, targetPath, false, copyArtifactClosure)
    }

    void copyArtifacts(String jobName, String includeGlob, String targetPath = '', boolean flattenFiles,
                       @DslContext(CopyArtifactContext) Closure copyArtifactClosure) {
        copyArtifacts(jobName, includeGlob, targetPath, flattenFiles, false, copyArtifactClosure)
    }

    @RequiresPlugin(id = 'copyartifact', minimumVersion = '1.26')
    void copyArtifacts(String jobName, String includeGlob, String targetPath = '', boolean flattenFiles,
                       boolean optionalAllowed,
                       @DslContext(CopyArtifactContext) Closure copyArtifactClosure) {
        CopyArtifactContext copyArtifactContext = new CopyArtifactContext()
        ContextHelper.executeInContext(copyArtifactClosure, copyArtifactContext)

        if (!copyArtifactContext.selectedSelector) {
            throw new IllegalArgumentException('A selector has to be select in the closure argument')
        }

        NodeBuilder nodeBuilder = NodeBuilder.newInstance()
        Node copyArtifactNode = nodeBuilder.'hudson.plugins.copyartifact.CopyArtifact' {
            project jobName
            filter includeGlob
            target targetPath ?: ''

            selector(class: "hudson.plugins.copyartifact.${copyArtifactContext.selectedSelector}Selector") {
                if (copyArtifactContext.selectedSelector == 'TriggeredBuild' && copyArtifactContext.fallback) {
                    fallbackToLastSuccessful 'true'
                }
                if (copyArtifactContext.selectedSelector == 'StatusBuild' && copyArtifactContext.stable) {
                    stable 'true'
                }
                if (copyArtifactContext.selectedSelector == 'PermalinkBuild') {
                    id copyArtifactContext.permalinkName
                }
                if (copyArtifactContext.selectedSelector == 'SpecificBuild') {
                    buildNumber copyArtifactContext.buildNumber
                }
                if (copyArtifactContext.selectedSelector == 'ParameterizedBuild') {
                    parameterName copyArtifactContext.parameterName
                }
            }

            if (flattenFiles) {
                flatten 'true'
            }
            if (optionalAllowed) {
                optional 'true'
            }
        }

        stepNodes << copyArtifactNode

    }

    /**
     * <org.jvnet.hudson.plugins.repositoryconnector.ArtifactResolver>
     *     <targetDirectory>target</targetDirectory>
     *     <failOnError>false</failOnError>
     *     <enableRepoLogging>false</enableRepoLogging>
     *     <snapshotUpdatePolicy>daily</snapshotUpdatePolicy>
     *     <releaseUpdatePolicy>daily</releaseUpdatePolicy>
     *     <snapshotChecksumPolicy>warn</snapshotChecksumPolicy>
     *     <releaseChecksumPolicy>warn</releaseChecksumPolicy>
     *     <artifacts>
     *         <org.jvnet.hudson.plugins.repositoryconnector.Artifact>
     *             <groupId>de.test.me</groupId>
     *             <artifactId>myTestArtifact</artifactId>
     *             <classifier/>
     *             <version>RELEASE</version>
     *             <extension>war</extension>
     *             <targetFileName>myTestArtifact.war</targetFileName>
     *         </org.jvnet.hudson.plugins.repositoryconnector.Artifact>
     *     </artifacts>
     * </org.jvnet.hudson.plugins.repositoryconnector.ArtifactResolver>
     */
    void resolveArtifacts(@DslContext(RepositoryConnectorContext) Closure repositoryConnectorClosure) {
        RepositoryConnectorContext context = new RepositoryConnectorContext()
        ContextHelper.executeInContext(repositoryConnectorClosure, context)

        stepNodes << new NodeBuilder().'org.jvnet.hudson.plugins.repositoryconnector.ArtifactResolver' {
            targetDirectory context.targetDirectory ?: ''
            failOnError context.failOnError
            enableRepoLogging context.enableRepoLogging
            snapshotUpdatePolicy context.snapshotUpdatePolicy
            releaseUpdatePolicy context.releaseUpdatePolicy
            snapshotChecksumPolicy 'warn'
            releaseChecksumPolicy 'warn'
            artifacts context.artifactNodes
        }
    }

    /**
     * <dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder>
     *     <projects>project-A,project-B</projects>
     *     <warningOnly>false</warningOnly>
     * </dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder>
     */
    void prerequisite(String projectList = '', boolean warningOnlyBool = false) {
        NodeBuilder nodeBuilder = new NodeBuilder()
        Node preReqNode = nodeBuilder.'dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder' {
             // Important that there are no spaces for comma delimited values, plugin doesn't trim, so we will
            projects(projectList.tokenize(',')*.trim().join(','))
            warningOnly(warningOnlyBool)
        }
        stepNodes << preReqNode
    }

    /**
     * <jenkins.plugins.publish__over__ssh.BapSshBuilderPlugin>
     *     <delegate>
     *         <consolePrefix>SSH: </consolePrefix>
     *         <delegate>
     *             <publishers>
     *                 <jenkins.plugins.publish__over__ssh.BapSshPublisher>
     *                     <configName>my-server</configName>
     *                     <verbose>false</verbose>
     *                     <transfers>
     *                         <jenkins.plugins.publish__over__ssh.BapSshTransfer>
     *                             <remoteDirectory></remoteDirectory>
     *                             <sourceFiles></sourceFiles>
     *                             <excludes></excludes>
     *                             <removePrefix></removePrefix>
     *                             <remoteDirectorySDF>false</remoteDirectorySDF>
     *                             <flatten>false</flatten>
     *                             <cleanRemote>false</cleanRemote>
     *                             <noDefaultExcludes>false</noDefaultExcludes>
     *                             <makeEmptyDirs>false</makeEmptyDirs>
     *                             <patternSeparator>[, ]+</patternSeparator>
     *                             <execCommand></execCommand>
     *                             <execTimeout>120000</execTimeout>
     *                             <usePty>false</usePty>
     *                         </jenkins.plugins.publish__over__ssh.BapSshTransfer>
     *                     </transfers>
     *                     <useWorkspaceInPromotion>false</useWorkspaceInPromotion>
     *                     <usePromotionTimestamp>false</usePromotionTimestamp>
     *                     <retry class="jenkins.plugins.publish_over_ssh.BapSshRetry">
     *                         <retries>10</retries>
     *                         <retryDelay>10000</retryDelay>
     *                     </retry>
     *                     <credentials class="jenkins.plugins.publish_over_ssh.BapSshCredentials">
     *                         <secretPassphrase/>
     *                         <key/>
     *                         <keyPath>path01</keyPath>
     *                         <username>user01</username>
     *                     </credentials>
     *                     <label class="jenkins.plugins.publish_over_ssh.BapSshPublisherLabel">
     *                         <label>server-01</label>
     *                     </label>
     *                 </jenkins.plugins.publish__over__ssh.BapSshPublisher>
     *             </publishers>
     *             <continueOnError>false</continueOnError>
     *             <failOnError>false</failOnError>
     *             <alwaysPublishFromMaster>false</alwaysPublishFromMaster>
     *             <hostConfigurationAccess class="jenkins.plugins.publish_over_ssh.BapSshPublisherPlugin"
     *                                      reference="../.."/>
     *             <paramPublish class="jenkins.plugins.publish_over_ssh.BapSshParamPublish">
     *                 <parameterName>PARAMETER</parameterName>
     *             </paramPublish>
     *         </delegate>
     *     </delegate>
     * </jenkins.plugins.publish__over__ssh.BapSshBuilderPlugin>
     */
    void publishOverSsh(@DslContext(PublishOverSshContext) Closure publishOverSshClosure) {
        PublishOverSshContext publishOverSshContext = new PublishOverSshContext()
        ContextHelper.executeInContext(publishOverSshClosure, publishOverSshContext)

        Preconditions.checkArgument(!publishOverSshContext.servers.empty, 'At least 1 server must be configured')

        stepNodes << new NodeBuilder().'jenkins.plugins.publish__over__ssh.BapSshBuilderPlugin' {
            delegate.delegate {
                consolePrefix('SSH: ')
                delegate.delegate {
                    publishers {
                        publishOverSshContext.servers.each { server ->
                            'jenkins.plugins.publish__over__ssh.BapSshPublisher' {
                                configName(server.name)
                                verbose(server.verbose)
                                transfers {
                                    server.transferSets.each { transferSet ->
                                        'jenkins.plugins.publish__over__ssh.BapSshTransfer' {
                                            remoteDirectory(transferSet.remoteDirectory ?: '')
                                            sourceFiles(transferSet.sourceFiles ?: '')
                                            excludes(transferSet.excludeFiles ?: '')
                                            removePrefix(transferSet.removePrefix ?: '')
                                            remoteDirectorySDF(transferSet.remoteDirIsDateFormat)
                                            flatten(transferSet.flattenFiles)
                                            cleanRemote(false)
                                            noDefaultExcludes(transferSet.noDefaultExcludes)
                                            makeEmptyDirs(transferSet.makeEmptyDirs)
                                            patternSeparator(transferSet.patternSeparator)
                                            execCommand(transferSet.execCommand ?: '')
                                            execTimeout(transferSet.execTimeout)
                                            usePty(transferSet.execInPty)
                                        }
                                    }
                                }
                                useWorkspaceInPromotion(false)
                                usePromotionTimestamp(false)
                                if (server.retry) {
                                    retry(class: 'jenkins.plugins.publish_over_ssh.BapSshRetry') {
                                        retries(server.retries)
                                        retryDelay(server.delay)
                                    }
                                }
                                if (server.credentials) {
                                    credentials(class: 'jenkins.plugins.publish_over_ssh.BapSshCredentials') {
                                        secretPassphrase('')
                                        key(server.credentials.key ?: '')
                                        keyPath(server.credentials.pathToKey ?: '')
                                        username(server.credentials.username)
                                    }
                                }
                                if (server.label) {
                                    label(class: 'jenkins.plugins.publish_over_ssh.BapSshPublisherLabel') {
                                        label(server.label)
                                    }
                                }
                            }
                        }
                    }
                    continueOnError(publishOverSshContext.continueOnError)
                    failOnError(publishOverSshContext.failOnError)
                    alwaysPublishFromMaster(publishOverSshContext.alwaysPublishFromMaster)
                    hostConfigurationAccess(
                            class: 'jenkins.plugins.publish_over_ssh.BapSshPublisherPlugin',
                            reference: '../..',
                    )
                    if (publishOverSshContext.parameterName) {
                        paramPublish(class: 'jenkins.plugins.publish_over_ssh.BapSshParamPublish') {
                            parameterName(publishOverSshContext.parameterName)
                        }
                    }
                }
            }
        }
    }

    /**
     * <hudson.plugins.parameterizedtrigger.TriggerBuilder>
     *     <configs>
     *         <hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig>
     *             <projects>one-project,another-project</projects>
     *             <condition>ALWAYS</condition>
     *             <triggerWithNoParameters>false</triggerWithNoParameters>
     *             <configs>
     *                 <hudson.plugins.parameterizedtrigger.CurrentBuildParameters/>
     *                 <hudson.plugins.parameterizedtrigger.FileBuildParameters>
     *                     <propertiesFile>some.properties</propertiesFile>
     *                 </hudson.plugins.parameterizedtrigger.FileBuildParameters>
     *                 <hudson.plugins.git.GitRevisionBuildParameters>
     *                     <combineQueuedCommits>false</combineQueuedCommits>
     *                 </hudson.plugins.git.GitRevisionBuildParameters>
     *                 <hudson.plugins.parameterizedtrigger.PredefinedBuildParameters>
     *                     <properties>
     *                         prop1=value1
     *                         prop2=value2
     *                     </properties>
     *                 </hudson.plugins.parameterizedtrigger.PredefinedBuildParameters>
     *                 <hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters>
     *                     <filter>label=="${TARGET}"</filter>
     *                 </hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters>
     *                 <hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters/>
     *             </configs>
     *             <block>
     *                 <unstableThreshold>
     *                     <name>UNSTABLE</name>
     *                     <ordinal>1</ordinal>
     *                     <color>YELLOW</color>
     *                 </unstableThreshold>
     *                 <buildStepFailureThreshold>
     *                     <name>FAILURE</name>
     *                     <ordinal>2</ordinal>
     *                     <color>RED</color>
     *                 </buildStepFailureThreshold>
     *                 <failureThreshold>
     *                     <name>FAILURE</name>
     *                     <ordinal>2</ordinal>
     *                     <color>RED</color>
     *                 </failureThreshold>
     *             </block>
     *             <buildAllNodesWithLabel>false</buildAllNodesWithLabel>
     *         </hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig>
     *     </configs>
     * </hudson.plugins.parameterizedtrigger.TriggerBuilder>
     */
    void downstreamParameterized(@DslContext(DownstreamContext) Closure downstreamClosure) {
        DownstreamContext downstreamContext = new DownstreamContext()
        ContextHelper.executeInContext(downstreamClosure, downstreamContext)

        Node stepNode = downstreamContext.createDownstreamNode(true)
        stepNodes << stepNode
    }

    /**
     * <org.jenkinsci.plugins.conditionalbuildstep.singlestep.SingleConditionalBuilder>
     *     <condition class="org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition">
     *         <arg1/><arg2/>
     *         <ignoreCase>false</ignoreCase>
     *     </condition>
     *     <buildStep class="hudson.tasks.Shell">
     *         <command/>
     *     </buildStep>
     *     <runner class="org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail"/>
     * </org.jenkinsci.plugins.conditionalbuildstep.singlestep.SingleConditionalBuilder>
     */
    void conditionalSteps(@DslContext(ConditionalStepsContext) Closure conditionalStepsClosure) {
        ConditionalStepsContext conditionalStepsContext = new ConditionalStepsContext(jobManagement)
        ContextHelper.executeInContext(conditionalStepsClosure, conditionalStepsContext)

        if (conditionalStepsContext.stepNodes.size() > 1) {
            stepNodes << conditionalStepsContext.createMultiStepNode()
        } else {
            stepNodes << conditionalStepsContext.createSingleStepNode()
        }
    }

    /**
     * <EnvInjectBuilder>
     *     <info>
     *         <propertiesFilePath>some.properties</propertiesFilePath>
     *         <propertiesContent>REV=15</propertiesContent>
     *     </info>
     * </EnvInjectBuilder>
     */
    void environmentVariables(@DslContext(StepEnvironmentVariableContext) Closure envClosure) {
        StepEnvironmentVariableContext envContext = new StepEnvironmentVariableContext()
        ContextHelper.executeInContext(envClosure, envContext)

        Node envNode = new NodeBuilder().'EnvInjectBuilder' {
            envContext.addInfoToBuilder(delegate)
        }

        stepNodes << envNode
    }

    /**
     * <org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteBuildConfiguration>
     *     <token/>
     *     <remoteJenkinsName>ci.acme.org</remoteJenkinsName>
     *     <job>CM7.5-SwingEditor-UITests-ALL</job>
     *     <shouldNotFailBuild>false</shouldNotFailBuild>
     *     <pollInterval>10</pollInterval>
     *     <preventRemoteBuildQueue>false</preventRemoteBuildQueue>
     *     <blockBuildUntilComplete>false</blockBuildUntilComplete>
     *     <parameters>BRANCH_OR_TAG=master-7.5 CMS_VERSION=$PIPELINE_VERSION</parameters>
     *     <parameterList>
     *         <string>BRANCH_OR_TAG=master-7.5</string>
     *         <string>CMS_VERSION=$PIPELINE_VERSION</string>
     *     </parameterList>
     *     <overrideAuth>false</overrideAuth>
     *     <auth>
     *         <org.jenkinsci.plugins.ParameterizedRemoteTrigger.Auth>
     *             <NONE>none</NONE>
     *             <API__TOKEN>apiToken</API__TOKEN>
     *             <CREDENTIALS__PLUGIN>credentialsPlugin</CREDENTIALS__PLUGIN>
     *         </org.jenkinsci.plugins.ParameterizedRemoteTrigger.Auth>
     *     </auth>
     *     <loadParamsFromFile>false</loadParamsFromFile>
     *     <parameterFile/>
     *     <queryString/>
     * </org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteBuildConfiguration>
     */
    void remoteTrigger(String remoteJenkins, String jobName,
                       @DslContext(ParameterizedRemoteTriggerContext) Closure closure = null) {
        Preconditions.checkArgument(!isNullOrEmpty(remoteJenkins), 'remoteJenkins must be specified')
        Preconditions.checkArgument(!isNullOrEmpty(jobName), 'jobName must be specified')

        ParameterizedRemoteTriggerContext context = new ParameterizedRemoteTriggerContext()
        ContextHelper.executeInContext(closure, context)

        List<String> jobParameters = context.parameters.collect { String key, String value -> "$key=$value" }

        stepNodes << new NodeBuilder().'org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteBuildConfiguration' {
            token()
            remoteJenkinsName(remoteJenkins)
            job(jobName)
            shouldNotFailBuild(context.shouldNotFailBuild)
            pollInterval(context.pollInterval)
            preventRemoteBuildQueue(context.preventRemoteBuildQueue)
            blockBuildUntilComplete(context.blockBuildUntilComplete)
            parameters(jobParameters.join('\n'))
            parameterList {
                if (jobParameters.empty) {
                    string()
                } else {
                    jobParameters.each { String value ->
                        string(value)
                    }
                }
            }
            overrideAuth(false)
            auth {
                'org.jenkinsci.plugins.ParameterizedRemoteTrigger.Auth' {
                    NONE('none')
                    API__TOKEN('apiToken')
                    CREDENTIALS__PLUGIN('credentialsPlugin')
                }
            }
            loadParamsFromFile(false)
            parameterFile()
            queryString()
        }
    }

    /**
     * <org.jvnet.hudson.plugins.exclusion.CriticalBlockStart/>
     * ...
     * <org.jvnet.hudson.plugins.exclusion.CriticalBlockEnd/>
     */
    void criticalBlock(@DslContext(StepContext) Closure closure) {
        StepContext stepContext = new StepContext(jobManagement)
        ContextHelper.executeInContext(closure, stepContext)

        stepNodes << new NodeBuilder().'org.jvnet.hudson.plugins.exclusion.CriticalBlockStart'()
        stepNodes.addAll(stepContext.stepNodes)
        stepNodes << new NodeBuilder().'org.jvnet.hudson.plugins.exclusion.CriticalBlockEnd'()
    }

    /**
     * <hudson.plugins.rake.Rake>
     *     <rakeInstallation>(Default)</rakeInstallation>
     *     <rakeFile/>
     *     <rakeLibDir/>
     *     <rakeWorkingDir/>
     *     <tasks/>
     *     <silent>false</silent>
     *     <bundleExec>false</bundleExec>
     * </hudson.plugins.rake.Rake>
     */
    void rake(@DslContext(RakeContext) Closure rakeClosure = null) {
        rake(null, rakeClosure)
    }

    void rake(String tasksArg, @DslContext(RakeContext) Closure rakeClosure = null) {
        RakeContext rakeContext = new RakeContext()

        if (tasksArg) {
            rakeContext.task(tasksArg)
        }

        ContextHelper.executeInContext(rakeClosure, rakeContext)

        stepNodes << new NodeBuilder().'hudson.plugins.rake.Rake' {
            rakeInstallation rakeContext.installation
            rakeFile rakeContext.file
            rakeLibDir rakeContext.libDir
            rakeWorkingDir rakeContext.workingDir
            tasks rakeContext.tasks.join(' ')
            silent rakeContext.silent
            bundleExec rakeContext.bundleExec
        }
    }

    /**
     * <org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer>
     *     <buildStep class="org.jenkinsci.plugins.vsphere.builders.PowerOff">
     *         <vm>test</vm>
     *         <evenIfSuspended>false</evenIfSuspended>
     *         <shutdownGracefully>false</shutdownGracefully>
     *     </buildStep>
     *     <serverName>test</serverName>
     *     <serverHash>320615527</serverHash>
     * </org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer>
     */
    void vSpherePowerOff(String server, String vm) {
        vSphereBuildStep(server, 'PowerOff') {
            delegate.vm vm
            evenIfSuspended false
            shutdownGracefully false
        }
    }

    /**
     * <org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer>
     *     <buildStep class="org.jenkinsci.plugins.vsphere.builders.PowerOn">
     *         <vm>test</vm>
     *         <timeoutInSeconds>180</timeoutInSeconds>
     *     </buildStep>
     *     <serverName>test</serverName>
     *     <serverHash>320615527</serverHash>
     * </org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer>
     */
    void vSpherePowerOn(String server, String vm) {
        vSphereBuildStep(server, 'PowerOn') {
            delegate.vm vm
            timeoutInSeconds 180
        }
    }

    /**
     * <org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer>
     *     <buildStep class="org.jenkinsci.plugins.vsphere.builders.PowerOm">
     *         <vm>test</vm>
     *         <timeoutInSeconds>180</timeoutInSeconds>
     *     </buildStep>
     *     <serverName>test</serverName>
     *     <serverHash>320615527</serverHash>
     * </org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer>
     */
    void vSphereRevertToSnapshot(String server, String vm, String snapshot) {
        vSphereBuildStep(server, 'RevertToSnapshot') {
            delegate.vm vm
            snapshotName snapshot
        }
    }

    private vSphereBuildStep(String server, String builder, Closure configuration) {
        int hash = Preconditions.checkNotNull(
                jobManagement.getVSphereCloudHash(server),
                "vSphere server ${server} does not exist"
        )
        stepNodes << new NodeBuilder().'org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer' {
            buildStep(class: "org.jenkinsci.plugins.vsphere.builders.${builder}", configuration)
            serverName server
            serverHash hash
        }
    }

    /**
     * <jenkins.plugins.http__request.HttpRequest>
     *     <url>https://rtfm.freelancer.com</url>
     *     <httpMode>POST</httpMode>
     *     <authentication>RTFM</authentication>
     *     <returnCodeBuildRelevant>true</returnCodeBuildRelevant>
     *     <logResponseBody>false</logResponseBody>
     * </jenkins.plugins.http__request.HttpRequest>
     */
    void httpRequest(String requestUrl, @DslContext(HttpRequestContext) Closure closure = null) {
        HttpRequestContext context = new HttpRequestContext()
        ContextHelper.executeInContext(closure, context)

        stepNodes << new NodeBuilder().'jenkins.plugins.http__request.HttpRequest' {
            url(requestUrl)
            if (context.httpMode != null) {
                httpMode(context.httpMode)
            }
            if (context.authentication != null) {
                authentication(context.authentication)
            }
            if (context.returnCodeBuildRelevant != null) {
                returnCodeBuildRelevant(context.returnCodeBuildRelevant)
            }
            if (context.logResponseBody != null) {
                logResponseBody(context.logResponseBody)
            }
        }
    }

    /**
     * <jenkins.plugins.nodejs.NodeJsCommandInterpreter>
     *     <command>console.log("Hello World!")</command>
     *     <nodeJSInstallationName>Node 0.12.0</nodeJSInstallationName>
     * </jenkins.plugins.nodejs.NodeJsCommandInterpreter>
     */
    void nodejsCommand(String commandScript, String installation) {
        stepNodes << new NodeBuilder().'jenkins.plugins.nodejs.NodeJsCommandInterpreter' {
            command(commandScript)
            nodeJSInstallationName(installation)
        }
    }

    /**
     * <ru.yandex.jenkins.plugins.debuilder.DebianPackageBuilder>
     *     <pathToDebian>package</pathToDebian>
     *     <nextVersion></nextVersion>
     *     <generateChangelog>false</generateChangelog>
     *     <signPackage>false</signPackage>
     *     <buildEvenWhenThereAreNoChanges>false</buildEvenWhenThereAreNoChanges>
     * </ru.yandex.jenkins.plugins.debuilder.DebianPackageBuilder>
     */
    @RequiresPlugin(id = 'debian-package-builder', minimumVersion = '1.6.6')
    void debianPackage(String path, @DslContext(DebianContext) Closure closure = null) {
        Preconditions.checkArgument(!isNullOrEmpty(path), 'path must be specified')

        DebianContext context = new DebianContext()
        ContextHelper.executeInContext(closure, context)

        stepNodes << new NodeBuilder().'ru.yandex.jenkins.plugins.debuilder.DebianPackageBuilder' {
            pathToDebian(path)
            nextVersion(context.nextVersion ?: '')
            generateChangelog(context.generateChangelog)
            signPackage(context.signPackage)
            buildEvenWhenThereAreNoChanges(context.alwaysBuild)
        }
    }
}
