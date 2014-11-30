package javaposse.jobdsl.dsl.helpers.step

import hudson.util.VersionNumber
import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.ContextHelper
import javaposse.jobdsl.dsl.helpers.common.DownstreamContext

import static com.google.common.base.Strings.isNullOrEmpty
import static javaposse.jobdsl.dsl.helpers.common.MavenContext.LocalRepositoryLocation.LocalToWorkspace

class StepContext implements Context {
    private static final List<String> VALID_CONTINUATION_CONDITIONS = ['SUCCESSFUL', 'UNSTABLE', 'COMPLETED']

    List<Node> stepNodes = []
    JobManagement jobManagement

    StepContext(List<Node> stepNodes = [], JobManagement jobManagement) {
        this.stepNodes = stepNodes
        this.jobManagement = jobManagement
    }

    /**
     * <hudson.tasks.Shell>
     *     <command>echo Hello</command>
     * </hudson.tasks.Shell>
     */
    def shell(String commandStr) {
        def nodeBuilder = new NodeBuilder()
        stepNodes << nodeBuilder.'hudson.tasks.Shell' {
            'command' commandStr
        }
    }

    /**
     * <hudson.tasks.BatchFile>
     *     <command>echo Hello from Windows</command>
     * </hudson.tasks.BatchFile>
     */
    def batchFile(String commandStr) {
        def nodeBuilder = new NodeBuilder()
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
    def gradle(Closure gradleClosure) {
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

    def gradle(String tasks = null, String switches = null, Boolean useWrapper = true, Closure configure = null) {
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
    def sbt(String sbtNameArg, String actionsArg = null, String sbtFlagsArg=null,  String jvmFlagsArg=null,
            String subdirPathArg=null, Closure configure = null) {

        def nodeBuilder = new NodeBuilder()

        def sbtNode = nodeBuilder.'org.jvnet.hudson.plugins.SbtPluginBuilder' {
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
     * <javaposse.jobdsl.plugin.ExecuteDslScripts plugin="job-dsl@1.16">
     *     <targets>sbt-template.groovy</targets>
     *     <usingScriptText>false</usingScriptText>
     *     <ignoreExisting>false</ignoreExisting>
     *     <removedJobAction>IGNORE</removedJobAction>
     * </javaposse.jobdsl.plugin.ExecuteDslScripts>
     */
    def dsl(Closure configure = null) {
        DslContext context = new DslContext()
        ContextHelper.executeInContext(configure, context)
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
        ContextHelper.executeInContext(antClosure, antContext)

        def targetList = []

        if (targetsArg) {
            targetList.addAll targetsArg.contains('\n') ? targetsArg.split('\n') : targetsArg.split(' ')
        }
        targetList.addAll antContext.targets

        def antOptsList = antContext.antOpts

        def propertiesList = []
        propertiesList += antContext.props

        def nodeBuilder = NodeBuilder.newInstance()
        def antNode = nodeBuilder.'hudson.tasks.Ant' {
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
    def groovyCommand(String command, Closure groovyClosure = null) {
        groovy(command, true, null, groovyClosure)
    }

    def groovyCommand(String command, String groovyName, Closure groovyClosure = null) {
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
    def groovyScriptFile(String fileName, Closure groovyClosure = null) {
        groovy(fileName, false, null, groovyClosure)
    }

    def groovyScriptFile(String fileName, String groovyName, Closure groovyClosure = null) {
        groovy(fileName, false, groovyName, groovyClosure)
    }

    protected groovyScriptSource(String commandOrFileName, boolean isCommand) {
        def nodeBuilder = new NodeBuilder()
        nodeBuilder.scriptSource(class: "hudson.plugins.groovy.${isCommand ? 'String' : 'File'}ScriptSource") {
            if (isCommand) {
                command commandOrFileName
            } else {
                scriptFile commandOrFileName
            }
        }
    }

    protected groovy(String commandOrFileName, boolean isCommand, String groovyInstallation, Closure groovyClosure) {
        def groovyContext = new GroovyContext()
        ContextHelper.executeInContext(groovyClosure, groovyContext)

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
     * <hudson.plugins.groovy.SystemGroovy>
     *     <scriptSource class="hudson.plugins.groovy.StringScriptSource">
     *         <command>System Groovy</command>
     *     </scriptSource>
     *     <bindings/>
     *     <classpath/>
     * </hudson.plugins.groovy.SystemGroovy>
     */
    def systemGroovyCommand(String command, Closure systemGroovyClosure = null) {
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
    def systemGroovyScriptFile(String fileName, Closure systemGroovyClosure = null) {
        systemGroovy(fileName, false, systemGroovyClosure)
    }

    protected systemGroovy(String commandOrFileName, boolean isCommand, Closure systemGroovyClosure) {
        def systemGroovyContext = new SystemGroovyContext()
        ContextHelper.executeInContext(systemGroovyClosure, systemGroovyContext)

        def systemGroovyNode = NodeBuilder.newInstance().'hudson.plugins.groovy.SystemGroovy' {
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
    def maven(Closure closure) {
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
            usePrivateRepository mavenContext.localRepositoryLocation == LocalToWorkspace ? 'true' : 'false'
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

    def maven(String targetsArg = null, String pomArg = null, Closure configure = null) {
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
        ContextHelper.executeInContext(grailsClosure, grailsContext)

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
     * Upstream build that triggered this job
     * <hudson.plugins.copyartifact.CopyArtifact>
     *     <projectName>jryan-odin-test</projectName>
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
    def copyArtifacts(String jobName, String includeGlob, Closure copyArtifactClosure) {
        copyArtifacts(jobName, includeGlob, '', copyArtifactClosure)
    }

    def copyArtifacts(String jobName, String includeGlob, String targetPath, Closure copyArtifactClosure) {
        copyArtifacts(jobName, includeGlob, targetPath, false, copyArtifactClosure)
    }

    def copyArtifacts(String jobName, String includeGlob, String targetPath = '', boolean flattenFiles,
                      Closure copyArtifactClosure) {
        copyArtifacts(jobName, includeGlob, targetPath, flattenFiles, false, copyArtifactClosure)
    }

    def copyArtifacts(String jobName, String includeGlob, String targetPath = '', boolean flattenFiles,
                      boolean optionalAllowed, Closure copyArtifactClosure) {
        CopyArtifactContext copyArtifactContext = new CopyArtifactContext()
        ContextHelper.executeInContext(copyArtifactClosure, copyArtifactContext)

        if (!copyArtifactContext.selectedSelector) {
            throw new IllegalArgumentException('A selector has to be select in the closure argument')
        }

        def nodeBuilder = NodeBuilder.newInstance()
        def copyArtifactNode = nodeBuilder.'hudson.plugins.copyartifact.CopyArtifact' {
            projectName jobName // Older name for field
            project jobName // Newer name for field
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
     * phaseName will have to be provided in the closure
     *
     * <com.tikal.jenkins.plugins.multijob.MultiJobBuilder>
     *   <phaseName>name-of-phase</phaseName>
     *   <phaseJobs>
     *     <com.tikal.jenkins.plugins.multijob.PhaseJobsConfig>
     *       <jobName>job-in-phase</jobName>
     *       <currParams>true</currParams>
     *       <exposedSCM>false</exposedSCM>
     *       <disableJob>false</disableJob>
     *       <configs class="empty-list"/>
     *       <killPhaseOnJobResultCondition>FAILURE</killPhaseOnJobResultCondition>
     *     </com.tikal.jenkins.plugins.multijob.PhaseJobsConfig>
     *   </phaseJobs>
     *   <continuationCondition>COMPLETED</continuationCondition>
     * </com.tikal.jenkins.plugins.multijob.MultiJobBuilder>
     */
    def phase(Closure phaseContext) {
        phase(null, 'SUCCESSFUL', phaseContext)
    }

    def phase(String phaseName, Closure phaseContext = null) {
        phase(phaseName, 'SUCCESSFUL', phaseContext)
    }

    def phase(String name, String continuationConditionArg, Closure phaseClosure) {
        PhaseContext phaseContext = new PhaseContext(jobManagement, name, continuationConditionArg)
        ContextHelper.executeInContext(phaseClosure, phaseContext)

        Preconditions.checkArgument(phaseContext.phaseName as Boolean, 'A phase needs a name')
        Preconditions.checkArgument(
                VALID_CONTINUATION_CONDITIONS.contains(phaseContext.continuationCondition),
                "Continuation Condition needs to be one of these values: ${VALID_CONTINUATION_CONDITIONS.join(', ')}"
        )

        VersionNumber multiJobPluginVersion = jobManagement.getPluginVersion('jenkins-multijob-plugin')

        stepNodes << new NodeBuilder().'com.tikal.jenkins.plugins.multijob.MultiJobBuilder' {
            phaseName phaseContext.phaseName
            continuationCondition phaseContext.continuationCondition
            phaseJobs {
                phaseContext.jobsInPhase.each { PhaseJobContext jobInPhase ->
                    'com.tikal.jenkins.plugins.multijob.PhaseJobsConfig' {
                        jobName jobInPhase.jobName
                        currParams jobInPhase.currentJobParameters
                        exposedSCM jobInPhase.exposedScm
                        if (multiJobPluginVersion?.isNewerThan(new VersionNumber('1.10'))) {
                            disableJob jobInPhase.disableJob
                            killPhaseOnJobResultCondition jobInPhase.killPhaseCondition
                        }
                        if (jobInPhase.hasConfig()) {
                            configs(jobInPhase.configAsNode().children())
                        } else {
                            configs('class': 'java.util.Collections$EmptyList')
                        }
                    }
                }
            }
        }
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
             // Important that there are no spaces for comma delimited values, plugin doesn't trim, so we will
            projects(projectList.tokenize(',')*.trim().join(','))
            warningOnly(warningOnlyBool)
        }
        stepNodes << preReqNode
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
    def downstreamParameterized(Closure downstreamClosure) {
        DownstreamContext downstreamContext = new DownstreamContext()
        ContextHelper.executeInContext(downstreamClosure, downstreamContext)

        def stepNode = downstreamContext.createDownstreamNode(true)
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
    def conditionalSteps(Closure conditionalStepsClosure) {
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
    def environmentVariables(Closure envClosure) {
        StepEnvironmentVariableContext envContext = new StepEnvironmentVariableContext()
        ContextHelper.executeInContext(envClosure, envContext)

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
        Preconditions.checkArgument(!isNullOrEmpty(remoteJenkins), 'remoteJenkins must be specified')
        Preconditions.checkArgument(!isNullOrEmpty(jobName), 'jobName must be specified')

        ParameterizedRemoteTriggerContext context = new ParameterizedRemoteTriggerContext()
        ContextHelper.executeInContext(closure, context)

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

    /**
     * <org.jvnet.hudson.plugins.exclusion.CriticalBlockStart/>
     * ...
     * <org.jvnet.hudson.plugins.exclusion.CriticalBlockEnd/>
     */
    def criticalBlock(Closure closure) {
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
    def rake(Closure rakeClosure = null) {
        rake(null, rakeClosure)
    }

    def rake(String tasksArg, Closure rakeClosure = null) {
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
    def vSpherePowerOff(String server, String vm) {
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
    def vSpherePowerOn(String server, String vm) {
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
    def vSphereRevertToSnapshot(String server, String vm, String snapshot) {
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
     * <jenkins.plugins.http__request.HttpRequest plugin="http_request@1.8.5">
     *     <url>https://rtfm.freelancer.com</url>
     *     <httpMode>POST</httpMode>
     *     <authentication>RTFM</authentication>
     *     <returnCodeBuildRelevant>true</returnCodeBuildRelevant>
     *     <logResponseBody>false</logResponseBody>
     * </jenkins.plugins.http__request.HttpRequest>
     */
    def httpRequest(String requestUrl = null, Closure closure) {
        HttpRequestContext context = new HttpRequestContext()
        context.url = requestUrl
        ContextHelper.executeInContext(closure, context)

        stepNodes << new NodeBuilder().'jenkins.plugins.http__request.HttpRequest' {
            url(context.url)

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
}
