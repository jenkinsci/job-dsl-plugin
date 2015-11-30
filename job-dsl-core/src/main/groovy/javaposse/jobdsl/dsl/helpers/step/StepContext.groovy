package javaposse.jobdsl.dsl.helpers.step

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractExtensibleContext
import javaposse.jobdsl.dsl.helpers.common.ArtifactDeployerContext
import javaposse.jobdsl.dsl.helpers.common.PublishOverSshContext

import static javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation.LOCAL_TO_WORKSPACE

class StepContext extends AbstractExtensibleContext {
    private final static VALID_BUILD_RESULTS = ['SUCCESS', 'UNSTABLE', 'FAILURE', 'ABORTED', 'CYCLE']

    final List<Node> stepNodes = []

    StepContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        stepNodes << node
    }

    /**
     * Runs a shell script.
     *
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String) readFileFromWorkspace} to read
     * the script from a file.
     */
    void shell(String command) {
        stepNodes << new NodeBuilder().'hudson.tasks.Shell' {
            delegate.command(command)
        }
    }

    /**
     * Runs a XShell command.
     *
     * @since 1.41
     */
    @RequiresPlugin(id = 'xshell', minimumVersion = '0.10')
    void xShell(@DslContext(XShellContext) Closure xShellClosure) {
        XShellContext xShellContext = new XShellContext(jobManagement)
        ContextHelper.executeInContext(xShellClosure, xShellContext)

        stepNodes << new NodeBuilder().'hudson.plugins.xshell.XShellBuilder' {
            executeFromWorkingDir(xShellContext.executableInWorkspaceDir)
            commandLine(xShellContext.commandLine ?: '')
            regexToKill(xShellContext.regexToKill ?: '')
            if (xShellContext.timeAllocated > 0) {
                timeAllocated(xShellContext.timeAllocated)
            }
        }
    }

    /**
     * Runs a remote shell script.
     *
     * @since 1.40
     */
    @RequiresPlugin(id = 'ssh', minimumVersion = '1.3')
    void remoteShell(String siteName, @DslContext(RemoteShellContext) Closure remoteShellClosure) {
        RemoteShellContext remoteShellContext = new RemoteShellContext(jobManagement)
        ContextHelper.executeInContext(remoteShellClosure, remoteShellContext)

        stepNodes << new NodeBuilder().'org.jvnet.hudson.plugins.SSHBuilder' {
            delegate.siteName(siteName)
            command(remoteShellContext.commands.join('\n'))
        }
    }

    /**
     * Runs a Windows batch script.
     *
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String) readFileFromWorkspace} to read
     * the script from a file.
     */
    void batchFile(String command) {
        stepNodes << new NodeBuilder().'hudson.tasks.BatchFile' {
            delegate.command(command)
        }
    }

    /**
     * Runs a Windows PowerShell script.
     *
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String) readFileFromWorkspace} to read
     * the script from a file.

     * @since 1.32
     */
    @RequiresPlugin(id = 'powershell', minimumVersion = '1.2')
    void powerShell(String command) {
        stepNodes << new NodeBuilder().'hudson.plugins.powershell.PowerShell' {
            delegate.command(command)
        }
    }

    /**
     * Set a build description based upon a regular expression test of the log file.
     * @since 1.31
     */
    @RequiresPlugin(id = 'description-setter', minimumVersion = '1.9')
    void buildDescription(String regexp, String description = null) {
        stepNodes << new NodeBuilder().'hudson.plugins.descriptionsetter.DescriptionSetterBuilder' {
            delegate.regexp(regexp ?: '')
            delegate.description(description ?: '')
        }
    }

    /**
     * Invokes a Gradle build script.
     *
     * @since 1.27
     */
    @RequiresPlugin(id = 'gradle')
    void gradle(@DslContext(GradleContext) Closure gradleClosure) {
        jobManagement.logPluginDeprecationWarning('gradle', '1.23')

        GradleContext gradleContext = new GradleContext(jobManagement)
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
            if (!jobManagement.getPluginVersion('gradle')?.isOlderThan(new VersionNumber('1.23'))) {
                useWorkspaceAsHome gradleContext.useWorkspaceAsHome
            }
        }

        if (gradleContext.configureBlock) {
            WithXmlAction action = new WithXmlAction(gradleContext.configureBlock)
            action.execute(gradleNode)
        }

        stepNodes << gradleNode
    }

    /**
     * Invokes a Gradle build script.
     *
     * The closure parameter expects a configure block for direct manipulation of the generated XML. The
     * {@code hudson.plugins.gradle.Gradle} node is passed into the configure block.
     */
    @RequiresPlugin(id = 'gradle')
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
     * Invokes the Scala Build Tool (SBT).
     *
     * The closure parameter expects a configure block for direct manipulation of the generated XML. The
     * {@code org.jvnet.hudson.plugins.SbtPluginBuilder} node is passed into the configure block.
     *
     * @since 1.16
     */
    @RequiresPlugin(id = 'sbt')
    void sbt(String sbtName, String actions = null, String sbtFlags = null, String jvmFlags = null,
             String subdirPath = null, Closure configure = null) {
        Preconditions.checkNotNull(sbtName, 'Please provide the name of the SBT to use')

        Node sbtNode = new NodeBuilder().'org.jvnet.hudson.plugins.SbtPluginBuilder' {
            name(sbtName)
            delegate.jvmFlags(jvmFlags ?: '')
            delegate.sbtFlags(sbtFlags ?: '')
            delegate.actions(actions ?: '')
            delegate.subdirPath(subdirPath ?: '')
        }

        if (configure) {
            WithXmlAction action = new WithXmlAction(configure)
            action.execute(sbtNode)
        }

        stepNodes << sbtNode
    }

    /**
     * Processes Job DSL scripts.
     *
     * @since 1.16
     */
    void dsl(@DslContext(javaposse.jobdsl.dsl.helpers.step.DslContext) Closure dslClosure) {
        javaposse.jobdsl.dsl.helpers.step.DslContext context = new javaposse.jobdsl.dsl.helpers.step.DslContext()
        ContextHelper.executeInContext(dslClosure, context)

        stepNodes << new NodeBuilder().'javaposse.jobdsl.plugin.ExecuteDslScripts' {
            targets(context.externalScripts.join('\n'))
            usingScriptText(context.scriptText as boolean)
            scriptText(context.scriptText ?: '')
            ignoreExisting(context.ignoreExisting)
            removedJobAction(context.removedJobAction)
            removedViewAction(context.removedViewAction)
            lookupStrategy(context.lookupStrategy)
            additionalClasspath(context.additionalClasspath ?: '')
        }
    }

    /**
     * Processes Job DSL scripts.
     *
     * @since 1.16
     */
    void dsl(String scriptText, String removedJobAction = null, boolean ignoreExisting = false) {
        dsl {
            text(scriptText)
            if (removedJobAction) {
                removeAction(removedJobAction)
            }
            delegate.ignoreExisting(ignoreExisting)
        }
    }

    /**
     * Processes Job DSL scripts.
     *
     * @since 1.16
     */
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
     * Invokes an Ant build script.
     */
    @RequiresPlugin(id = 'ant')
    void ant(@DslContext(AntContext) Closure antClosure = null) {
        ant(null, null, null, antClosure)
    }

    /**
     * Invokes an Ant build script.
     */
    @RequiresPlugin(id = 'ant')
    void ant(String targets, @DslContext(AntContext) Closure antClosure = null) {
        ant(targets, null, null, antClosure)
    }

    /**
     * Invokes an Ant build script.
     */
    @RequiresPlugin(id = 'ant')
    void ant(String targets, String buildFile, @DslContext(AntContext) Closure antClosure = null) {
        ant(targets, buildFile, null, antClosure)
    }

    /**
     * Invokes an Ant build script.
     */
    @RequiresPlugin(id = 'ant')
    void ant(String targets, String buildFile, String antInstallation,
             @DslContext(AntContext) Closure antClosure = null) {
        AntContext antContext = new AntContext()
        ContextHelper.executeInContext(antClosure, antContext)

        List<String> targetList = []

        if (targets) {
            targetList.addAll targets.contains('\n') ? targets.split('\n') : targets.split(' ')
        }
        targetList.addAll antContext.targets

        List<String> antOptsList = antContext.antOpts

        List<String> propertiesList = []
        propertiesList += antContext.props

        Node antNode = new NodeBuilder().'hudson.tasks.Ant' {
            delegate.targets(targetList.join(' '))

            antName antInstallation ?: antContext.antName ?: '(Default)'

            if (antOptsList) {
                antOpts antOptsList.join('\n')
            }

            if (buildFile || antContext.buildFile) {
                delegate.buildFile(buildFile ?: antContext.buildFile)
            }
        }

        if (propertiesList) {
            antNode.appendNode('properties', propertiesList.join('\n'))
        }

        stepNodes << antNode
    }

    /**
     * Executes a Groovy script.
     */
    @RequiresPlugin(id = 'groovy')
    void groovyCommand(String command, @DslContext(GroovyContext) Closure groovyClosure = null) {
        groovy(command, true, null, groovyClosure)
    }

    /**
     * Executes a Groovy script.
     */
    @RequiresPlugin(id = 'groovy')
    void groovyCommand(String command, String groovyName, @DslContext(GroovyContext) Closure groovyClosure = null) {
        groovy(command, true, groovyName, groovyClosure)
    }

    /**
     * Executes a Groovy script.
     */
    @RequiresPlugin(id = 'groovy')
    void groovyScriptFile(String fileName, @DslContext(GroovyContext) Closure groovyClosure = null) {
        groovy(fileName, false, null, groovyClosure)
    }

    /**
     * Executes a Groovy script.
     */
    @RequiresPlugin(id = 'groovy')
    void groovyScriptFile(String fileName, String groovyName, @DslContext(GroovyContext) Closure groovyClosure = null) {
        groovy(fileName, false, groovyName, groovyClosure)
    }

    protected groovyScriptSource(String commandOrFileName, boolean isCommand) {
        new NodeBuilder().scriptSource(class: "hudson.plugins.groovy.${isCommand ? 'String' : 'File'}ScriptSource") {
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

        Node groovyNode = new NodeBuilder().'hudson.plugins.groovy.Groovy' {
            groovyName groovyInstallation ?: groovyContext.groovyInstallation ?: '(Default)'
            parameters groovyContext.groovyParams.join(' ')
            scriptParameters groovyContext.scriptParams.join(' ')
            javaOpts groovyContext.javaOpts.join(' ')
            classPath groovyContext.classpathEntries.join(File.pathSeparator)
        }
        groovyNode.append(groovyScriptSource(commandOrFileName, isCommand))
        groovyNode.appendNode('properties', groovyContext.props.join('\n'))

        stepNodes << groovyNode
    }

    /**
     * Executes a system Groovy script.
     */
    @RequiresPlugin(id = 'groovy')
    void systemGroovyCommand(String command, @DslContext(SystemGroovyContext) Closure systemGroovyClosure = null) {
        systemGroovy(command, true, systemGroovyClosure)
    }

    /**
     * Executes a system Groovy script.
     */
    @RequiresPlugin(id = 'groovy')
    void systemGroovyScriptFile(String fileName, @DslContext(SystemGroovyContext) Closure systemGroovyClosure = null) {
        systemGroovy(fileName, false, systemGroovyClosure)
    }

    protected systemGroovy(String commandOrFileName, boolean isCommand, Closure systemGroovyClosure) {
        SystemGroovyContext systemGroovyContext = new SystemGroovyContext()
        ContextHelper.executeInContext(systemGroovyClosure, systemGroovyContext)

        Node systemGroovyNode = new NodeBuilder().'hudson.plugins.groovy.SystemGroovy' {
            bindings systemGroovyContext.bindings.collect { key, value -> "${key}=${value}" }.join('\n')
            classpath systemGroovyContext.classpathEntries.join(File.pathSeparator)
        }
        systemGroovyNode.append(groovyScriptSource(commandOrFileName, isCommand))

        stepNodes << systemGroovyNode
    }

    /**
     * Invokes a Maven build.
     *
     * @since 1.20
     */
    @RequiresPlugin(id = 'maven-plugin')
    void maven(@DslContext(MavenContext) Closure closure) {
        jobManagement.logPluginDeprecationWarning('maven-plugin', '2.3')

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
            if (mavenContext.providedGlobalSettingsId) {
                globalSettings(class: 'org.jenkinsci.plugins.configfiles.maven.job.MvnGlobalSettingsProvider') {
                    settingsConfigId(mavenContext.providedGlobalSettingsId)
                }
            }
        }

        if (mavenContext.configureBlock) {
            WithXmlAction action = new WithXmlAction(mavenContext.configureBlock)
            action.execute(mavenNode)
        }

        stepNodes << mavenNode
    }

    /**
     * Invokes a Maven build.
     *
     * The closure parameter expects a configure block for direct manipulation of the generated XML. The
     * {@code hudson.tasks.Maven} node is passed into the configure block.
     */
    @RequiresPlugin(id = 'maven-plugin')
    void maven(String targets = null, String pom = null, Closure configure = null) {
        maven {
            delegate.goals(targets)
            delegate.rootPOM(pom)
            delegate.configure(configure)
        }
    }

    /**
     * Builds a Grails project.
     */
    @RequiresPlugin(id = 'grails')
    void grails(@DslContext(GrailsContext) Closure grailsClosure) {
        grails null, false, grailsClosure
    }

    /**
     * Builds a Grails project.
     */
    @RequiresPlugin(id = 'grails')
    void grails(String targets, @DslContext(GrailsContext) Closure grailsClosure) {
        grails targets, false, grailsClosure
    }

    /**
     * Builds a Grails project.
     */
    @RequiresPlugin(id = 'grails')
    void grails(String targets = null, boolean useWrapper = false,
                @DslContext(GrailsContext) Closure grailsClosure = null) {
        GrailsContext grailsContext = new GrailsContext(
                useWrapper: useWrapper
        )
        ContextHelper.executeInContext(grailsClosure, grailsContext)

        stepNodes << new NodeBuilder().'com.g2one.hudson.grails.GrailsBuilder' {
            delegate.targets(targets ?: grailsContext.targets.join(' '))
            name grailsContext.name
            grailsWorkDir grailsContext.grailsWorkDir
            projectWorkDir grailsContext.projectWorkDir
            projectBaseDir grailsContext.projectBaseDir
            serverPort grailsContext.serverPort
            'properties' grailsContext.props.collect { k, v -> "$k=$v" }.join('\n')
            forceUpgrade grailsContext.forceUpgrade
            nonInteractive grailsContext.nonInteractive
            delegate.useWrapper(grailsContext.useWrapper)
        }
    }

    /**
     * Copies artifacts from another project.
     *
     * @since 1.33
     */
    @RequiresPlugin(id = 'copyartifact', minimumVersion = '1.31')
    void copyArtifacts(String jobName, @DslContext(CopyArtifactContext) Closure copyArtifactClosure = null) {
        CopyArtifactContext copyArtifactContext = new CopyArtifactContext(jobManagement)
        ContextHelper.executeInContext(copyArtifactClosure, copyArtifactContext)

        Node copyArtifactNode = new NodeBuilder().'hudson.plugins.copyartifact.CopyArtifact' {
            project(jobName)
            filter(copyArtifactContext.includePatterns.join(', '))
            target(copyArtifactContext.targetDirectory ?: '')
            if (copyArtifactContext.excludePatterns) {
                excludes(copyArtifactContext.excludePatterns.join(', '))
            }
            if (copyArtifactContext.flatten) {
                flatten(true)
            }
            if (copyArtifactContext.optional) {
                optional(true)
            }
            doNotFingerprintArtifacts(!copyArtifactContext.fingerprint)
        }
        copyArtifactNode.append(copyArtifactContext.selectorContext.selector)
        stepNodes << copyArtifactNode
    }

    /**
     * Resolves artifacts from a Maven repository.
     *
     * @since 1.29
     */
    @RequiresPlugin(id = 'repository-connector')
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
     * Verify the state of other jobs and fails the build if needed.
     *
     * @param projectList a comma delimited list of jobs to check
     * @param warningOnly if set to {@code true} then the build will not be failed even if the checks are failed
     * @since 1.19
     */
    @RequiresPlugin(id = 'prereq-buildstep')
    void prerequisite(String projectList = '', boolean warningOnly = false) {
        stepNodes << new NodeBuilder().'dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder' {
            // Important that there are no spaces for comma delimited values, plugin doesn't trim, so we will
            projects(projectList.tokenize(',')*.trim().join(','))
            delegate.warningOnly(warningOnly)
        }
    }

    /**
     * Send artifacts to an SSH server (using SFTP) and/or execute commands over SSH.
     *
     * @since 1.28
     */
    @RequiresPlugin(id = 'publish-over-ssh')
    void publishOverSsh(@DslContext(PublishOverSshContext) Closure publishOverSshClosure) {
        PublishOverSshContext publishOverSshContext = new PublishOverSshContext()
        ContextHelper.executeInContext(publishOverSshClosure, publishOverSshContext)

        Preconditions.checkArgument(!publishOverSshContext.servers.empty, 'At least 1 server must be configured')

        stepNodes << new NodeBuilder().'jenkins.plugins.publish__over__ssh.BapSshBuilderPlugin' {
            delegate.delegate {
                consolePrefix('SSH: ')
                currentNode.append(publishOverSshContext.node)
            }
        }
    }

    /**
     * Triggers new parametrized builds.
     *
     * @since 1.20
     */
    @RequiresPlugin(id = 'parameterized-trigger')
    void downstreamParameterized(@DslContext(DownstreamContext) Closure downstreamClosure) {
        jobManagement.logPluginDeprecationWarning('parameterized-trigger', '2.26')

        DownstreamContext downstreamContext = new DownstreamContext(jobManagement, item)
        ContextHelper.executeInContext(downstreamClosure, downstreamContext)

        stepNodes << new NodeBuilder().'hudson.plugins.parameterizedtrigger.TriggerBuilder' {
            configs(downstreamContext.configs)
        }
    }

    /**
     * Wraps any number of other build steps, controlling their execution based on a defined condition.
     *
     * @since 1.20
     */
    @RequiresPlugin(id = 'conditional-buildstep')
    void conditionalSteps(@DslContext(ConditionalStepsContext) Closure conditionalStepsClosure) {
        ConditionalStepsContext conditionalStepsContext = new ConditionalStepsContext(jobManagement, newInstance())
        ContextHelper.executeInContext(conditionalStepsClosure, conditionalStepsContext)

        stepNodes << new NodeBuilder().'org.jenkinsci.plugins.conditionalbuildstep.ConditionalBuilder' {
            runCondition(class: conditionalStepsContext.runCondition.conditionClass) {
                conditionalStepsContext.runCondition.addArgs(delegate)
            }
            runner(class: conditionalStepsContext.runnerClass)
            conditionalbuilders(conditionalStepsContext.stepContext.stepNodes)
        }
    }

    /**
     * Injects environment variables into the build.
     *
     * @since 1.21
     */
    @RequiresPlugin(id = 'envinject')
    void environmentVariables(@DslContext(StepEnvironmentVariableContext) Closure envClosure) {
        StepEnvironmentVariableContext envContext = new StepEnvironmentVariableContext(jobManagement)
        ContextHelper.executeInContext(envClosure, envContext)

        stepNodes << new NodeBuilder().'EnvInjectBuilder' {
            envContext.addInfoToBuilder(delegate)
        }
    }

    /**
     * Triggers a job on another Jenkins instance.
     *
     * @since 1.22
     */
    @RequiresPlugin(id = 'Parameterized-Remote-Trigger')
    void remoteTrigger(String remoteJenkins, String jobName,
                       @DslContext(ParameterizedRemoteTriggerContext) Closure closure = null) {
        Preconditions.checkNotNullOrEmpty(remoteJenkins, 'remoteJenkins must be specified')
        Preconditions.checkNotNullOrEmpty(jobName, 'jobName must be specified')

        ParameterizedRemoteTriggerContext context = new ParameterizedRemoteTriggerContext()
        ContextHelper.executeInContext(closure, context)

        List<String> jobParameters = context.parameters.collect { String key, String value -> "$key=$value" }

        stepNodes << new NodeBuilder().'org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteBuildConfiguration' {
            token(context.token ?: '')
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
     * Contains the build steps of the critical zone defined by the
     * {@link javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext#exclusionResources(java.lang.Iterable)
     * exclusionResources} wrapper.
     *
     * @since 1.24
     */
    @RequiresPlugin(id = 'Exclusion')
    void criticalBlock(@DslContext(StepContext) Closure closure) {
        StepContext stepContext = new StepContext(jobManagement, item)
        ContextHelper.executeInContext(closure, stepContext)

        stepNodes << new NodeBuilder().'org.jvnet.hudson.plugins.exclusion.CriticalBlockStart'()
        stepNodes.addAll(stepContext.stepNodes)
        stepNodes << new NodeBuilder().'org.jvnet.hudson.plugins.exclusion.CriticalBlockEnd'()
    }

    /**
     * Invokes Rake.
     *
     * @since 1.25
     */
    @RequiresPlugin(id = 'rake')
    void rake(@DslContext(RakeContext) Closure rakeClosure = null) {
        rake(null, rakeClosure)
    }

    /**
     * Invokes Rake.
     *
     * @since 1.25
     */
    @RequiresPlugin(id = 'rake')
    void rake(String tasks, @DslContext(RakeContext) Closure rakeClosure = null) {
        RakeContext rakeContext = new RakeContext()

        if (tasks) {
            rakeContext.task(tasks)
        }

        ContextHelper.executeInContext(rakeClosure, rakeContext)

        stepNodes << new NodeBuilder().'hudson.plugins.rake.Rake' {
            rakeInstallation rakeContext.installation
            rakeFile rakeContext.file
            rakeLibDir rakeContext.libDir
            rakeWorkingDir rakeContext.workingDir
            delegate.tasks(rakeContext.tasks.join(' '))
            silent rakeContext.silent
            bundleExec rakeContext.bundleExec
        }
    }

    /**
     * Set the build status.
     *
     * Must be one of {@code 'SUCCESS'}, {@code 'UNSTABLE'}, {@code 'FAILURE'}, {@code 'ABORTED'} or {@code 'CYCLE'}.
     *
     * @since 1.35
     */
    @RequiresPlugin(id = 'fail-the-build-plugin', minimumVersion = '1.0')
    void setBuildResult(String result) {
        Preconditions.checkArgument(
                VALID_BUILD_RESULTS.contains(result),
                "result must be on of ${VALID_BUILD_RESULTS.join(', ')}"
        )
        stepNodes << new NodeBuilder().'org.jenkins__ci.plugins.fail__the__build.FixResultBuilder' {
            'defaultResultName'(result)
        }
    }

    /**
     * This build step will power off the specified VM.
     *
     * @since 1.25
     */
    @RequiresPlugin(id = 'vsphere-cloud')
    void vSpherePowerOff(String server, String vm) {
        vSphereBuildStep(server, 'PowerOff') {
            delegate.vm(vm)
            evenIfSuspended(false)
            shutdownGracefully(false)
        }
    }

    /**
     * This build step will power on the specified VM. Uses a default timeout of 180 seconds.
     *
     * @since 1.25
     */
    @RequiresPlugin(id = 'vsphere-cloud')
    void vSpherePowerOn(String server, String vm) {
        vSpherePowerOn(server, vm, 180)
    }

    /**
     * This build step will power on the specified VM. The timeout must be specified in seconds.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'vsphere-cloud')
    void vSpherePowerOn(String server, String vm, int timeout) {
        vSphereBuildStep(server, 'PowerOn') {
            delegate.vm(vm)
            timeoutInSeconds(timeout)
        }
    }

    /**
     * This build step will revert the specified VM to the specified snapshot.
     *
     * @since 1.25
     */
    @RequiresPlugin(id = 'vsphere-cloud')
    void vSphereRevertToSnapshot(String server, String vm, String snapshot) {
        vSphereBuildStep(server, 'RevertToSnapshot') {
            delegate.vm(vm)
            snapshotName(snapshot)
        }
    }

    private vSphereBuildStep(String server, String builder, Closure configuration) {
        Integer hash = jobManagement.getVSphereCloudHash(server)
        Preconditions.checkNotNull(hash, "vSphere server ${server} does not exist")

        stepNodes << new NodeBuilder().'org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer' {
            buildStep(class: "org.jenkinsci.plugins.vsphere.builders.${builder}", configuration)
            serverName(server)
            serverHash(hash)
        }
    }

    /**
     * Adds a step which performs a HTTP request.
     *
     * @since 1.28
     */
    @RequiresPlugin(id = 'http_request')
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
     * Executes a NodeJS script. Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String)
     * readFileFromWorkspace} to read scripts from a file.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'nodejs')
    void nodejsCommand(String commandScript, String installation) {
        stepNodes << new NodeBuilder().'jenkins.plugins.nodejs.NodeJsCommandInterpreter' {
            command(commandScript)
            nodeJSInstallationName(installation)
        }
    }

    /**
     * Executes Clang scan-build against Mac or iPhone XCode projects or other scan-build compatible build tools.
     *
     * The {@link ClangScanBuildContext#workspace(java.lang.String) workspace},
     * {@link ClangScanBuildContext#scheme(java.lang.String) scheme} and
     * {@link ClangScanBuildContext#clangInstallationName(java.lang.String) clangInstallationName} options are
     * mandatory.
     *
     * @since 1.37
     */
    @RequiresPlugin(id = 'clang-scanbuild-plugin', minimumVersion = '1.6')
    void clangScanBuild(@DslContext(ClangScanBuildContext) Closure closure) {
        ClangScanBuildContext context = new ClangScanBuildContext()
        ContextHelper.executeInContext(closure, context)

        Preconditions.checkNotNullOrEmpty(context.workspace, 'workspace must be specified')
        Preconditions.checkNotNullOrEmpty(context.scheme, 'scheme must be specified')
        Preconditions.checkNotNullOrEmpty(context.clangInstallationName, 'clangInstallationName must be specified')

        stepNodes << new NodeBuilder().'jenkins.plugins.clangscanbuild.ClangScanBuildBuilder' {
            targetSdk(context.targetSdk ?: '')
            config(context.configuration ?: '')
            clangInstallationName(context.clangInstallationName)
            workspace(context.workspace)
            scheme(context.scheme)
            scanbuildargs(context.scanBuildArgs ?: '')
            xcodebuildargs(context.xcodeBuildArgs ?: '')
        }
    }

    /**
     * Builds Debian (.deb) packages.
     *
     * @param path refers to a path in the workspace where the 'debian' catalog is stored.
     * @since 1.31
     */
    @RequiresPlugin(id = 'debian-package-builder', minimumVersion = '1.6.6')
    void debianPackage(String path, @DslContext(DebianContext) Closure closure = null) {
        Preconditions.checkNotNullOrEmpty(path, 'path must be specified')

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

    /**
     * Builds the project with a declared Python installation.
     *
     * @since 1.38
     */
    @RequiresPlugin(id = 'shiningpanda', minimumVersion = '0.21')
    void python(@DslContext(PythonContext) Closure closure) {
        PythonContext context = new PythonContext()
        ContextHelper.executeInContext(closure, context)

        stepNodes << new NodeBuilder().'jenkins.plugins.shiningpanda.builders.PythonBuilder' {
            pythonName(context.pythonName ?: '')
            nature(context.nature)
            command(context.command ?: '')
            ignoreExitCode(context.ignoreExitCode)
        }
    }

    /**
     * Creates a virtualenv to build the project.
     */
    @RequiresPlugin(id = 'shiningpanda', minimumVersion = '0.21')
    void virtualenv(@DslContext(VirtualenvContext) Closure closure) {
        VirtualenvContext context = new VirtualenvContext()
        ContextHelper.executeInContext(closure, context)

        stepNodes << new NodeBuilder().'jenkins.plugins.shiningpanda.builders.VirtualenvBuilder' {
            pythonName(context.pythonName ?: '')
            home(context.name ?: '')
            clear(context.clear)
            systemSitePackages(context.systemSitePackages)
            nature(context.nature)
            command(context.command ?: '')
            ignoreExitCode(context.ignoreExitCode)
        }
    }

    /**
     * Builds and pushes a Docker based project to the Docker registry.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'docker-build-publish', minimumVersion = '1.0')
    void dockerBuildAndPublish(@DslContext(DockerBuildAndPublishContext) Closure closure) {
        DockerBuildAndPublishContext context = new DockerBuildAndPublishContext()
        ContextHelper.executeInContext(closure, context)

        stepNodes << new NodeBuilder().'com.cloudbees.dockerpublish.DockerBuilder' {
            server {
                if (context.dockerHostURI) {
                    uri(context.dockerHostURI)
                }
                if (context.serverCredentials) {
                    credentialsId(context.serverCredentials)
                }
            }
            registry {
                if (context.dockerRegistryURL) {
                    url(context.dockerRegistryURL)
                }
                if (context.registryCredentials) {
                    credentialsId(context.registryCredentials)
                }
            }
            repoName(context.repositoryName ?: '')
            noCache(context.noCache)
            forcePull(context.forcePull)
            dockerfilePath(context.dockerfileDirectory ?: '')
            skipBuild(context.skipBuild)
            skipDecorate(context.skipDecorate)
            repoTag(context.tag ?: '')
            skipPush(context.skipPush)
            createFingerprint(context.createFingerprints)
            skipTagLatest(context.skipTagAsLatest)
        }
    }

    /**
     * Deploys artifacts from the build workspace to remote locations.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'artifactdeployer', minimumVersion = '0.33')
    void artifactDeployer(@DslContext(ArtifactDeployerContext) Closure closure) {
        ArtifactDeployerContext context = new ArtifactDeployerContext()
        ContextHelper.executeInContext(closure, context)

        stepNodes << new NodeBuilder().'org.jenkinsci.plugins.artifactdeployer.ArtifactDeployerBuilder' {
            entry {
                includes(context.includes ?: '')
                basedir(context.baseDir ?: '')
                excludes(context.excludes ?: '')
                remote(context.remoteFileLocation ?: '')
                flatten(context.flatten)
                deleteRemote(context.cleanUp)
                deleteRemoteArtifacts(context.deleteRemoteArtifacts)
                deleteRemoteArtifactsByScript(context.deleteRemoteArtifactsByScript as boolean)
                if (context.deleteRemoteArtifactsByScript) {
                    groovyExpression(context.deleteRemoteArtifactsByScript)
                }
                failNoFilesDeploy(context.failIfNoFiles)
            }
        }
    }

    /**
     * Executes a centrally managed script.
     *
     * @since 1.40
     */
    @RequiresPlugin(id = 'managed-scripts', minimumVersion = '1.2.1')
    void managedScript(String scriptName, @DslContext(ManagedScriptContext) Closure closure = null) {
        String scriptId = jobManagement.getConfigFileId(ConfigFileType.ManagedScript, scriptName)
        Preconditions.checkNotNull(scriptId, "managed script with name '${scriptName}' not found")

        ManagedScriptContext context = new ManagedScriptContext()
        ContextHelper.executeInContext(closure, context)

        stepNodes << new NodeBuilder().'org.jenkinsci.plugins.managedscripts.ScriptBuildStep' {
            buildStepId(scriptId)
            buildStepArgs {
                context.arguments.each {
                    string(it)
                }
            }
            tokenized(context.tokenized)
        }
    }

    /**
     * Runs a Ruby command.
     *
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String) readFileFromWorkspace} to read
     * the script from a file.
     *
     * @since 1.41
     */
    @RequiresPlugin(id = 'ruby', minimumVersion = '1.2')
    void ruby(String command) {
        stepNodes << new NodeBuilder().'hudson.plugins.ruby.Ruby' {
            delegate.command(command ?: '')
        }
    }

    /**
     * @since 1.35
     */
    protected StepContext newInstance() {
        new StepContext(jobManagement, item)
    }
}
