package javaposse.jobdsl.dsl.helpers.step

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.common.DownstreamContext

import static com.google.common.base.Strings.isNullOrEmpty
import static javaposse.jobdsl.dsl.helpers.common.MavenContext.LocalRepositoryLocation.LocalToWorkspace

class AbstractStepContext implements Context {
    List<Node> stepNodes = []

    AbstractStepContext(List<Node> stepNodes = []) {
        this.stepNodes = stepNodes
    }

    /**
     <hudson.tasks.Shell>
     <command>echo Hello</command>
     </hudson.tasks.Shell>
     */
    def shell(String commandStr) {
        def nodeBuilder = new NodeBuilder()
        stepNodes << nodeBuilder.'hudson.tasks.Shell' {
            'command' commandStr
        }
    }

    /**
     <hudson.tasks.BatchFile>
     <command>echo Hello from Windows</command>
     </hudson.tasks.BatchFile>
     */
    def batchFile(String commandStr) {
        def nodeBuilder = new NodeBuilder()
        stepNodes << nodeBuilder.'hudson.tasks.BatchFile' {
            'command' commandStr
        }
    }
    /**
     * <hudson.plugins.gradle.Gradle>
     * <description>descr</description>
     * <switches>--refresh-dependencies</switches>
     * <tasks>task1 task2</tasks>
     * <rootBuildScriptDir>master</rootBuildScriptDir>
     * <buildFile/>
     * <useWrapper>true</useWrapper>
     * <fromRootBuildScriptDir>true</fromRootBuildScriptDir>
     * </hudson.plugins.gradle.Gradle>
     */
    def gradle(Closure gradleClosure) {
        GradleContext gradleContext = new GradleContext()
        AbstractContextHelper.executeInContext(gradleClosure, gradleContext)

        def nodeBuilder = new NodeBuilder()
        def gradleNode = nodeBuilder.'hudson.plugins.gradle.Gradle' {
            description gradleContext.description
            switches gradleContext.switches
            tasks gradleContext.tasks
            rootBuildScriptDir gradleContext.rootBuildScriptDir
            buildFile gradleContext.buildFile
            useWrapper gradleContext.useWrapper.toString()
            if (gradleContext.fromRootBuildScriptDir != null) {
                fromRootBuildScriptDir gradleContext.fromRootBuildScriptDir.toString()
            }
            if (gradleContext.makeExecutable != null) {
                makeExecutable gradleContext.makeExecutable.toString()
            }
            if (gradleContext.gradleName != null) {
                gradleName gradleContext.gradleName
            }
        }
        // Apply Context
        if (gradleContext.configureBlock) {
            WithXmlAction action = new WithXmlAction(gradleContext.configureBlock)
            action.execute(gradleNode)
        }
        stepNodes << gradleNode
    }

    /**
     <hudson.plugins.gradle.Gradle>
     <description/>
     <switches>-Dtiming-multiple=5 -P${Status}=true -I ${WORKSPACE}/netflix-oss.gradle ${Option}</switches>
     <tasks>clean${Task}</tasks>
     <rootBuildScriptDir/>
     <buildFile/>
     <useWrapper>true</useWrapper>
     <wrapperScript/>
     </hudson.plugins.gradle.Gradle>
     */
    def gradle(String tasksArg = null, String switchesArg = null, boolean useWrapperArg = true, Closure configure = null) {
        gradle {
            if(tasksArg!=null) {
                tasks tasksArg
            }
            if(switchesArg!=null) {
                switches switchesArg
            }
            if(useWrapperArg!=null) {
                useWrapper useWrapperArg
            }
            delegate.configure(configure)
        }
    }

    /**
     <org.jvnet.hudson.plugins.SbtPluginBuilder plugin="sbt@1.4">
     <name>SBT 0.12.3</name>
     <jvmFlags>-XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=512M -Dfile.encoding=UTF-8 -Xmx2G -Xms512M</jvmFlags>
     <sbtFlags>-Dsbt.log.noformat=true</sbtFlags>
     <actions>clean update &quot;env development&quot; test dist publish</actions>
     <subdirPath></subdirPath>
     </org.jvnet.hudson.plugins.SbtPluginBuilder>
     */
    def sbt(String sbtNameArg, String actionsArg = null, String sbtFlagsArg=null,  String jvmFlagsArg=null, String subdirPathArg=null, Closure configure = null) {

        def nodeBuilder = new NodeBuilder()

        def attributes = [plugin:'sbt@1.4']
        def sbtNode = nodeBuilder.'org.jvnet.hudson.plugins.SbtPluginBuilder'(attributes) {
            name Preconditions.checkNotNull(sbtNameArg, "Please provide the name of the SBT to use" as Object)
            jvmFlags jvmFlagsArg?:''
            sbtFlags sbtFlagsArg?:''
            actions actionsArg?:''
            subdirPath subdirPathArg?:''
        }

        // Apply Context
        if (configure) {
            WithXmlAction action = new WithXmlAction(configure)
            action.execute(sbtNode)
        }

        stepNodes << sbtNode

    }

    /**
     <javaposse.jobdsl.plugin.ExecuteDslScripts plugin="job-dsl@1.16">
        <targets>sbt-template.groovy</targets>
        <usingScriptText>false</usingScriptText>
        <ignoreExisting>false</ignoreExisting>
        <removedJobAction>IGNORE</removedJobAction>
     </javaposse.jobdsl.plugin.ExecuteDslScripts>     */
    def dsl(Closure configure = null) {
        DslContext context = new DslContext()
        AbstractContextHelper.executeInContext(configure, context)
        buildDslNode(context)
    }

    def dsl(String scriptText, String removedJobAction = null, boolean ignoreExisting = false) {
        DslContext ctx = new DslContext()
        ctx.text(scriptText)
        if (removedJobAction) {
            ctx.removeAction(removedJobAction)
        }
        ctx.ignoreExisting = ignoreExisting
        buildDslNode(ctx)
    }

    def dsl(Collection<String> externalScripts, String removedJobAction = null, boolean ignoreExisting = false) {
        DslContext ctx = new DslContext()
        ctx.external(externalScripts.toArray(new String[0]))
        if (removedJobAction) {
            ctx.removeAction(removedJobAction)
        }
        ctx.ignoreExisting = ignoreExisting
        buildDslNode(ctx)

    }

    protected void buildDslNode(context) {
        def nodeBuilder = new NodeBuilder()
        def dslNode = nodeBuilder.'javaposse.jobdsl.plugin.ExecuteDslScripts' {
            targets context.targets
            usingScriptText context.useScriptText()
            scriptText context.scriptText
            ignoreExisting context.ignoreExisting
            removedJobAction context.removedJobAction.name()
        }

        stepNodes << dslNode
    }

    /**
     <hudson.tasks.Ant>
     <targets>target</targets>
     <antName>Ant 1.8</antName>
     <antOpts>-Xmx1g -XX:MaxPermSize=128M -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false</antOpts>
     <buildFile>build.xml</buildFile>
     <properties>test.jvmargs=-Xmx=1g
     test.maxmemory=2g
     multiline=true</properties>
     </hudson.tasks.Ant>

     Empty:
     <hudson.tasks.Ant>
     <targets/>
     <antName>(Default)</antName>
     </hudson.tasks.Ant>
     */
    def ant(Closure antClosure = null) {
        ant(null, null, null, antClosure)
    }

    def ant(String targetsStr, Closure antClosure = null) {
        ant(targetsStr, null, null, antClosure)
    }

    def ant(String targetsStr, String buildFileStr, Closure antClosure = null) {
        ant(targetsStr, buildFileStr, null, antClosure)
    }

    def ant(String targetsArg, String buildFileArg, String antInstallation, Closure antClosure = null) {
        AntContext antContext = new AntContext()
        AbstractContextHelper.executeInContext(antClosure, antContext)

        def targetList = []

        if (targetsArg) {
            targetList.addAll targetsArg.contains('\n') ? targetsArg.split('\n') : targetsArg.split(' ')
        }
        targetList.addAll antContext.targets

        // Build File
        if (!buildFileArg && antContext.buildFile) { // Fall back to context
            buildFileArg = antContext.buildFile
        }

        def antOptsList = antContext.antOpts

        if(!antInstallation) {
            antInstallation = antContext.antName?:'(Default)'
        }

        def propertiesList = []
        propertiesList += antContext.props

        def nodeBuilder = NodeBuilder.newInstance()
        def antNode = nodeBuilder.'hudson.tasks.Ant' {
            targets targetList.join(' ')

            antName antInstallation

            if (antOptsList) {
                antOpts antOptsList.join('\n')
            }

            if (buildFileArg) {
                buildFile buildFileArg
            }
        }

        if(propertiesList) {
            antNode.appendNode('properties', propertiesList.join('\n'))
        }

        stepNodes << antNode
    }

    /**
     <hudson.plugins.groovy.Groovy>
     <scriptSource class="hudson.plugins.groovy.StringScriptSource">
     <command>Command</command>
     </scriptSource>
     <groovyName>(Default)</groovyName>
     <parameters/>
     <scriptParameters/>
     <properties/>
     <javaOpts/>
     <classPath/>
     </hudson.plugins.groovy.Groovy>
     */
    def groovyCommand(String command, Closure groovyClosure = null) {
        groovy(command, true, null, groovyClosure)
    }

    def groovyCommand(String command, String groovyName, Closure groovyClosure = null) {
        groovy(command, true, groovyName, groovyClosure)
    }

    /**
     <hudson.plugins.groovy.Groovy>
     <scriptSource class="hudson.plugins.groovy.FileScriptSource">
     <scriptFile>acme.groovy</scriptFile>
     </scriptSource>
     <groovyName>(Default)</groovyName>
     <parameters/>
     <scriptParameters/>
     <properties/>
     <javaOpts/>
     <classPath/>
     </hudson.plugins.groovy.Groovy>
     */
    def groovyScriptFile(String fileName, Closure groovyClosure = null) {
        groovy(fileName, false, null, groovyClosure)
    }

    def groovyScriptFile(String fileName, String groovyName, Closure groovyClosure = null) {
        groovy(fileName, false, groovyName, groovyClosure)
    }

    protected def groovyScriptSource(String commandOrFileName, boolean isCommand) {
        def nodeBuilder = new NodeBuilder()
        nodeBuilder.scriptSource(class: "hudson.plugins.groovy.${isCommand ? 'String' : 'File'}ScriptSource") {
            if (isCommand) {
                command commandOrFileName
            } else {
                scriptFile commandOrFileName
            }
        }
    }

    protected def groovy(String commandOrFileName, boolean isCommand, String groovyInstallation, Closure groovyClosure) {
        def groovyContext = new GroovyContext()
        AbstractContextHelper.executeInContext(groovyClosure, groovyContext)

        def groovyNode = NodeBuilder.newInstance().'hudson.plugins.groovy.Groovy' {
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
     <hudson.plugins.groovy.SystemGroovy>
     <scriptSource class="hudson.plugins.groovy.StringScriptSource">
     <command>System Groovy</command>
     </scriptSource>
     <bindings/>
     <classpath/>
     </hudson.plugins.groovy.SystemGroovy>
     */
    def systemGroovyCommand(String command, Closure systemGroovyClosure = null) {
        systemGroovy(command, true, systemGroovyClosure)
    }

    /**
     <hudson.plugins.groovy.SystemGroovy>
     <scriptSource class="hudson.plugins.groovy.FileScriptSource">
     <scriptFile>System Groovy</scriptFile>
     </scriptSource>
     <bindings/>
     <classpath/>
     </hudson.plugins.groovy.SystemGroovy>
     */
    def systemGroovyScriptFile(String fileName, Closure systemGroovyClosure = null) {
        systemGroovy(fileName, false, systemGroovyClosure)
    }

    protected def systemGroovy(String commandOrFileName, boolean isCommand, Closure systemGroovyClosure) {
        def systemGroovyContext = new SystemGroovyContext()
        AbstractContextHelper.executeInContext(systemGroovyClosure, systemGroovyContext)

        def systemGroovyNode = NodeBuilder.newInstance().'hudson.plugins.groovy.SystemGroovy' {
            bindings systemGroovyContext.bindings.collect({ key, value -> "${key}=${value}" }).join('\n')
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
    def maven(Closure closure) {
        MavenContext mavenContext = new MavenContext()
        AbstractContextHelper.executeInContext(closure, mavenContext)

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
            usePrivateRepository mavenContext.localRepositoryLocation == LocalToWorkspace ? 'true' : 'false'
        }

        // Apply Context
        if (mavenContext.configureBlock) {
            WithXmlAction action = new WithXmlAction(mavenContext.configureBlock)
            action.execute(mavenNode)
        }

        stepNodes << mavenNode
    }

    def maven(String targetsArg = null, String pomArg = null, Closure configure = null) {
        maven {
            delegate.goals(targetsArg)
            delegate.rootPOM(pomArg)
            delegate.configure(configure)
        }
    }

    /**
     <com.g2one.hudson.grails.GrailsBuilder>
     <targets/>
     <name>(Default)</name>
     <grailsWorkDir/>
     <projectWorkDir/>
     <projectBaseDir/>
     <serverPort/>
     <properties/>
     <forceUpgrade>false</forceUpgrade>
     <nonInteractive>true</nonInteractive>
     <useWrapper>false</useWrapper>
     </com.g2one.hudson.grails.GrailsBuilder>
     */
    def grails(Closure grailsClosure) {
        grails null, false, grailsClosure
    }

    def grails(String targetsArg, Closure grailsClosure) {
        grails targetsArg, false, grailsClosure
    }

    def grails(String targetsArg = null, boolean useWrapperArg = false, Closure grailsClosure = null) {
        GrailsContext grailsContext = new GrailsContext(
            useWrapper: useWrapperArg
        )
        AbstractContextHelper.executeInContext(grailsClosure, grailsContext)

        def nodeBuilder = new NodeBuilder()
        def grailsNode = nodeBuilder.'com.g2one.hudson.grails.GrailsBuilder' {
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
     <hudson.plugins.copyartifact.CopyArtifact>
     <projectName>jryan-odin-test</projectName>
     <filter>*ivy-locked.xml</filter>
     <target>target/</target>
     <selector class="hudson.plugins.copyartifact.TriggeredBuildSelector"/> <!-- Upstream build that triggered this job -->
     <flatten>true</flatten>
     <optional>true</optional>
     </hudson.plugins.copyartifact.CopyArtifact>
     <hudson.plugins.copyartifact.CopyArtifact>
     <projectName>jryan-odin-test</projectName>
     <filter>*ivy-locked.xml</filter>
     <target/>
     <selector class="hudson.plugins.copyartifact.StatusBuildSelector"/> <!-- Latest successful build -->
     </hudson.plugins.copyartifact.CopyArtifact>
     <selector class="hudson.plugins.copyartifact.SavedBuildSelector"/> <!-- Latest saved build (marked "keep forever")-->
     <selector class="hudson.plugins.copyartifact.PermalinkBuildSelector"> <!-- Specified by permalink -->
     <id>lastBuild</id> <!-- Last Build-->
     <id>lastStableBuild</id> <!-- Latest Stable Build -->
     </selector>
     <selector class="hudson.plugins.copyartifact.SpecificBuildSelector"> <!-- Specific Build -->
     <buildNumber>43</buildNumber>
     </selector>
     <selector class="hudson.plugins.copyartifact.WorkspaceSelector"/> <!-- Copy from WORKSPACE of latest completed build -->
     <selector class="hudson.plugins.copyartifact.ParameterizedBuildSelector"> <!-- Specified by build parameter -->
     <parameterName>BUILD_SELECTOR</parameterName>
     </selector>
     */
    def copyArtifacts(String jobName, String includeGlob, Closure copyArtifactClosure) {
        return copyArtifacts(jobName, includeGlob, '', copyArtifactClosure)
    }

    def copyArtifacts(String jobName, String includeGlob, String targetPath, Closure copyArtifactClosure) {
        return copyArtifacts(jobName, includeGlob, targetPath, false, copyArtifactClosure)
    }

    def copyArtifacts(String jobName, String includeGlob, String targetPath = '', boolean flattenFiles, Closure copyArtifactClosure) {
        return copyArtifacts(jobName, includeGlob, targetPath, flattenFiles, false, copyArtifactClosure)
    }

    def copyArtifacts(String jobName, String includeGlob, String targetPath = '', boolean flattenFiles, boolean optionalAllowed, Closure copyArtifactClosure) {
        CopyArtifactContext copyArtifactContext = new CopyArtifactContext()
        AbstractContextHelper.executeInContext(copyArtifactClosure, copyArtifactContext)

        if (!copyArtifactContext.selectedSelector) {
            throw new IllegalArgumentException("A selector has to be select in the closure argument")
        }

        def nodeBuilder = NodeBuilder.newInstance()
        def copyArtifactNode = nodeBuilder.'hudson.plugins.copyartifact.CopyArtifact' {
            projectName jobName // Older name for field
            project jobName // Newer name for field
            filter includeGlob
            target targetPath?:''

            selector('class':"hudson.plugins.copyartifact.${copyArtifactContext.selectedSelector}Selector") {
                if (copyArtifactContext.selectedSelector == 'TriggeredBuild' && copyArtifactContext.fallback) {
                    fallbackToLastSuccessful 'true'
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
     * phaseName will have to be provided in the closure
     * @param phaseContext
     * @return
     */
    def phase(Closure phaseContext) {
        phase(null, 'SUCCESSFUL', phaseContext)
    }

    def phase(String phaseName, Closure phaseContext = null) {
        phase(phaseName, 'SUCCESSFUL', phaseContext)
    }

    def phase(String name, String continuationConditionArg, Closure phaseClosure) {
        PhaseContext phaseContext = new PhaseContext(name, continuationConditionArg)
        AbstractContextHelper.executeInContext(phaseClosure, phaseContext)

        Preconditions.checkArgument phaseContext.phaseName as Boolean, "A phase needs a name"

        def validConditions = ['SUCCESSFUL', 'UNSTABLE', 'COMPLETED']
        Preconditions.checkArgument(validConditions.contains(phaseContext.continuationCondition), "Continuation Condition need to be one of these values: ${validConditions.join(',')}" )

        def nodeBuilder = NodeBuilder.newInstance()
        def multiJobPhaseNode = nodeBuilder.'com.tikal.jenkins.plugins.multijob.MultiJobBuilder' {
            phaseName phaseContext.phaseName
            continuationCondition phaseContext.continuationCondition
            phaseJobs {
                phaseContext.jobsInPhase.each { jobInPhase ->
                    'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig' {
                        jobName jobInPhase.jobName
                        currParams jobInPhase.currentJobParameters?'true':'false'
                        exposedSCM jobInPhase.exposedScm?'true':'false'
                        if (jobInPhase.hasConfig()) {
                            configs(jobInPhase.configAsNode().children())
                        } else {
                            configs('class': 'java.util.Collections$EmptyList')
                        }
                    }
                }
            }
        }
        stepNodes << multiJobPhaseNode
    }

    /**
     * <dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder>
     *     <projects>project-A,project-B</projects>
     *     <warningOnly>false</warningOnly>
     * </dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder>
     */
    def prerequisite(String projectList = '', boolean warningOnlyBool = false) {
        def nodeBuilder = new NodeBuilder()
        def preReqNode = nodeBuilder.'dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder' {
             // Important that there are no spaces for comma delimited values, plugin doesn't handle by trimming, so we will
            projectList = projectList.tokenize(',').collect{ it.trim() }.join(',')
            projects(projectList)
            warningOnly(warningOnlyBool)
        }
        stepNodes << preReqNode
    }

    /**
     <hudson.plugins.parameterizedtrigger.TriggerBuilder plugin="parameterized-trigger@2.21">
     <configs>
     <hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig>
     <configs>
     <hudson.plugins.parameterizedtrigger.CurrentBuildParameters/> // Current build parameters
     <hudson.plugins.parameterizedtrigger.FileBuildParameters> // Parameters from properties file
     <propertiesFile>some.properties</propertiesFile>
     </hudson.plugins.parameterizedtrigger.FileBuildParameters>
     <hudson.plugins.git.GitRevisionBuildParameters> // Pass-through Git commit that was built
     <combineQueuedCommits>false</combineQueuedCommits>
     </hudson.plugins.git.GitRevisionBuildParameters>
     <hudson.plugins.parameterizedtrigger.PredefinedBuildParameters> // Predefined properties
     <properties>prop1=value1
     prop2=value2</properties>
     </hudson.plugins.parameterizedtrigger.PredefinedBuildParameters>
     <hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters> // Restrict matrix execution to a subset
     <filter>label=="${TARGET}"</filter>
     </hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters>
     <hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters/> // Subversion revision
     <projects>one-project,another-project</projects>
     <condition>ALWAYS</condition>
     <triggerWithNoParameters>false</triggerWithNoParameters>
     <block>
     <unstableThreshold>
     <name>UNSTABLE</name>
     <ordinal>1</ordinal>
     <color>YELLOW</color>
     </unstableThreshold>
     <buildStepFailureThreshold>
     <name>FAILURE</name>
     <ordinal>2</ordinal>
     <color>RED</color>
     </buildStepFailureThreshold>
     <failureThreshold>
     <name>FAILURE</name>
     <ordinal>2</ordinal>
     <color>RED</color>
     </failureThreshold>
     </block>
     <buildAllNodesWithLabel>false</buildAllNodesWithLabel>
     </hudson.plugins.parameterizedtrigger.BlockableBuildTriggerConfig>
     </configs>
     </hudson.plugins.parameterizedtrigger.TriggerBuilder>
     */
    def downstreamParameterized(Closure downstreamClosure) {
        DownstreamContext downstreamContext = new DownstreamContext()
        AbstractContextHelper.executeInContext(downstreamClosure, downstreamContext)

        def stepNode = downstreamContext.createDownstreamNode(true)
        stepNodes << stepNode
    }

    /**
     <org.jenkinsci.plugins.conditionalbuildstep.singlestep.SingleConditionalBuilder plugin="conditional-buildstep@1.2.2">
     <condition class="org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition" plugin="run-condition@0.10">
     <arg1/><arg2/>
     <ignoreCase>false</ignoreCase>
     </condition>
     <buildStep class="hudson.tasks.Shell">
     <command/>
     </buildStep>
     <runner class="org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail" plugin="run-condition@0.10"/>
     </org.jenkinsci.plugins.conditionalbuildstep.singlestep.SingleConditionalBuilder>
     */
    def conditionalSteps(Closure conditionalStepsClosure) {
        ConditionalStepsContext conditionalStepsContext = new ConditionalStepsContext()
        AbstractContextHelper.executeInContext(conditionalStepsClosure, conditionalStepsContext)

        if (conditionalStepsContext.stepNodes.size() > 1) {
            stepNodes << conditionalStepsContext.createMultiStepNode()
        } else {
            stepNodes << conditionalStepsContext.createSingleStepNode()
        }
    }

    /**
     * <pre>
     * {@code
     * <EnvInjectBuilder>
     *   <info>
     *     <propertiesFilePath>some.properties</propertiesFilePath>
     *     <propertiesContent>REV=15</propertiesContent>
     *   </info>
     * </EnvInjectBuilder>
     * }
     * </pre>
     */
    def environmentVariables(Closure envClosure) {
        StepEnvironmentVariableContext envContext = new StepEnvironmentVariableContext()
        AbstractContextHelper.executeInContext(envClosure, envContext)

        def envNode = new NodeBuilder().'EnvInjectBuilder' {
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
    def remoteTrigger(String remoteJenkins, String jobName, Closure closure = null) {
        Preconditions.checkArgument(!isNullOrEmpty(remoteJenkins), "remoteJenkins must be specified")
        Preconditions.checkArgument(!isNullOrEmpty(jobName), "jobName must be specified")

        ParameterizedRemoteTriggerContext context = new ParameterizedRemoteTriggerContext()
        AbstractContextHelper.executeInContext(closure, context)

        List<String> jobParameters = context.parameters.collect { String key, String value -> "$key=$value" }

        stepNodes << new NodeBuilder().'org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteBuildConfiguration' {
            token()
            remoteJenkinsName(remoteJenkins)
            job(jobName)
            shouldNotFailBuild(false)
            pollInterval(10)
            preventRemoteBuildQueue(false)
            blockBuildUntilComplete(false)
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
}
