package javaposse.jobdsl.dsl.helpers.step

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractExtensibleContext
import javaposse.jobdsl.dsl.helpers.common.DownstreamContext
import javaposse.jobdsl.dsl.helpers.common.PublishOverSshContext

import static com.google.common.base.Strings.isNullOrEmpty
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

    void shell(String commandStr) {
        stepNodes << new NodeBuilder().'hudson.tasks.Shell' {
            'command' commandStr
        }
    }

    void batchFile(String commandStr) {
        stepNodes << new NodeBuilder().'hudson.tasks.BatchFile' {
            'command' commandStr
        }
    }

    /**
     * @since 1.32
     */
    @RequiresPlugin(id = 'powershell', minimumVersion = '1.2')
    void powerShell(String command) {
        stepNodes << new NodeBuilder().'hudson.plugins.powershell.PowerShell' {
            delegate.command(command)
        }
    }

    /**
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
     * @since 1.27
     */
    @RequiresPlugin(id = 'gradle')
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
     * @since 1.16
     */
    @RequiresPlugin(id = 'sbt')
    void sbt(String sbtNameArg, String actionsArg = null, String sbtFlagsArg = null, String jvmFlagsArg = null,
             String subdirPathArg = null, Closure configure = null) {
        Preconditions.checkNotNull(sbtNameArg, 'Please provide the name of the SBT to use')

        Node sbtNode = new NodeBuilder().'org.jvnet.hudson.plugins.SbtPluginBuilder' {
            name sbtNameArg
            jvmFlags jvmFlagsArg ?: ''
            sbtFlags sbtFlagsArg ?: ''
            actions actionsArg ?: ''
            subdirPath subdirPathArg ?: ''
        }

        if (configure) {
            WithXmlAction action = new WithXmlAction(configure)
            action.execute(sbtNode)
        }

        stepNodes << sbtNode
    }

    /**
     * @since 1.16
     */
    void dsl(@DslContext(javaposse.jobdsl.dsl.helpers.step.DslContext) Closure configure) {
        javaposse.jobdsl.dsl.helpers.step.DslContext context = new javaposse.jobdsl.dsl.helpers.step.DslContext()
        ContextHelper.executeInContext(configure, context)

        stepNodes << new NodeBuilder().'javaposse.jobdsl.plugin.ExecuteDslScripts' {
            targets(context.externalScripts.join('\n'))
            usingScriptText(!isNullOrEmpty(context.scriptText))
            scriptText(context.scriptText ?: '')
            ignoreExisting(context.ignoreExisting)
            removedJobAction(context.removedJobAction)
            removedViewAction(context.removedViewAction)
            lookupStrategy(context.lookupStrategy)
            additionalClasspath(context.additionalClasspath ?: '')
        }
    }

    /**
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

    void ant(@DslContext(AntContext) Closure antClosure = null) {
        ant(null, null, null, antClosure)
    }

    void ant(String targetsStr, @DslContext(AntContext) Closure antClosure = null) {
        ant(targetsStr, null, null, antClosure)
    }

    void ant(String targetsStr, String buildFileStr, @DslContext(AntContext) Closure antClosure = null) {
        ant(targetsStr, buildFileStr, null, antClosure)
    }

    @RequiresPlugin(id = 'ant')
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

        Node antNode = new NodeBuilder().'hudson.tasks.Ant' {
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

    void groovyCommand(String command, @DslContext(GroovyContext) Closure groovyClosure = null) {
        groovy(command, true, null, groovyClosure)
    }

    void groovyCommand(String command, String groovyName, @DslContext(GroovyContext) Closure groovyClosure = null) {
        groovy(command, true, groovyName, groovyClosure)
    }

    void groovyScriptFile(String fileName, @DslContext(GroovyContext) Closure groovyClosure = null) {
        groovy(fileName, false, null, groovyClosure)
    }

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

    @RequiresPlugin(id = 'groovy')
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

    void systemGroovyCommand(String command, @DslContext(SystemGroovyContext) Closure systemGroovyClosure = null) {
        systemGroovy(command, true, systemGroovyClosure)
    }

    void systemGroovyScriptFile(String fileName, @DslContext(SystemGroovyContext) Closure systemGroovyClosure = null) {
        systemGroovy(fileName, false, systemGroovyClosure)
    }

    @RequiresPlugin(id = 'groovy')
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

    void grails(@DslContext(GrailsContext) Closure grailsClosure) {
        grails null, false, grailsClosure
    }

    void grails(String targetsArg, @DslContext(GrailsContext) Closure grailsClosure) {
        grails targetsArg, false, grailsClosure
    }

    @RequiresPlugin(id = 'grails')
    void grails(String targetsArg = null, boolean useWrapperArg = false,
                @DslContext(GrailsContext) Closure grailsClosure = null) {
        GrailsContext grailsContext = new GrailsContext(
                useWrapper: useWrapperArg
        )
        ContextHelper.executeInContext(grailsClosure, grailsContext)

        stepNodes << new NodeBuilder().'com.g2one.hudson.grails.GrailsBuilder' {
            targets targetsArg ?: grailsContext.targetsString
            name grailsContext.name
            grailsWorkDir grailsContext.grailsWorkDir
            projectWorkDir grailsContext.projectWorkDir
            projectBaseDir grailsContext.projectBaseDir
            serverPort grailsContext.serverPort
            'properties' grailsContext.propertiesString
            forceUpgrade grailsContext.forceUpgrade
            nonInteractive grailsContext.nonInteractive
            useWrapper grailsContext.useWrapper
        }
    }

    @Deprecated
    void copyArtifacts(String jobName, String includeGlob,
                       @DslContext(CopyArtifactSelectorContext) Closure copyArtifactClosure) {
        copyArtifacts(jobName, includeGlob, '', copyArtifactClosure)
    }

    @Deprecated
    void copyArtifacts(String jobName, String includeGlob, String targetPath,
                       @DslContext(CopyArtifactSelectorContext) Closure copyArtifactClosure) {
        copyArtifacts(jobName, includeGlob, targetPath, false, copyArtifactClosure)
    }

    @Deprecated
    void copyArtifacts(String jobName, String includeGlob, String targetPath = '', boolean flattenFiles,
                       @DslContext(CopyArtifactSelectorContext) Closure copyArtifactClosure) {
        copyArtifacts(jobName, includeGlob, targetPath, flattenFiles, false, copyArtifactClosure)
    }

    @Deprecated
    void copyArtifacts(String jobName, String includeGlob, String targetPath = '', boolean flattenFiles,
                       boolean optionalAllowed,
                       @DslContext(CopyArtifactSelectorContext) Closure copyArtifactClosure) {
        jobManagement.logDeprecationWarning()
        copyArtifacts(jobName) {
            delegate.includePatterns(includeGlob)
            delegate.targetDirectory(targetPath)
            delegate.flatten(flattenFiles)
            delegate.optional(optionalAllowed)
            delegate.buildSelector(copyArtifactClosure)
        }
    }

    /**
     * @since 1.33
     */
    @RequiresPlugin(id = 'copyartifact', minimumVersion = '1.26')
    void copyArtifacts(String jobName, @DslContext(CopyArtifactContext) Closure copyArtifactClosure = null) {
        jobManagement.logPluginDeprecationWarning('copyartifact', '1.31')

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
            if (!jobManagement.getPluginVersion('copyartifact')?.isOlderThan(new VersionNumber('1.29'))) {
                doNotFingerprintArtifacts(!copyArtifactContext.fingerprint)
            }
        }
        copyArtifactNode.append(copyArtifactContext.selectorContext.selector)
        stepNodes << copyArtifactNode
    }

    /**
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
     * @since 1.19
     */
    @RequiresPlugin(id = 'prereq-buildstep')
    void prerequisite(String projectList = '', boolean warningOnlyBool = false) {
        stepNodes << new NodeBuilder().'dk.hlyh.ciplugins.prereqbuildstep.PrereqBuilder' {
            // Important that there are no spaces for comma delimited values, plugin doesn't trim, so we will
            projects(projectList.tokenize(',')*.trim().join(','))
            warningOnly(warningOnlyBool)
        }
    }

    /**
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
     * @since 1.20
     */
    @RequiresPlugin(id = 'parameterized-trigger')
    void downstreamParameterized(@DslContext(DownstreamContext) Closure downstreamClosure) {
        DownstreamContext downstreamContext = new DownstreamContext(jobManagement)
        ContextHelper.executeInContext(downstreamClosure, downstreamContext)

        stepNodes << downstreamContext.createDownstreamNode(true)
    }

    /**
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
     * @since 1.25
     */
    void rake(@DslContext(RakeContext) Closure rakeClosure = null) {
        rake(null, rakeClosure)
    }

    /**
     * @since 1.25
     */
    @RequiresPlugin(id = 'rake')
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
     * @since 1.25
     */
    void vSpherePowerOff(String server, String vm) {
        vSphereBuildStep(server, 'PowerOff') {
            delegate.vm vm
            evenIfSuspended false
            shutdownGracefully false
        }
    }

    /**
     * @since 1.25
     */
    void vSpherePowerOn(String server, String vm) {
        vSphereBuildStep(server, 'PowerOn') {
            delegate.vm vm
            timeoutInSeconds 180
        }
    }

    /**
     * @since 1.25
     */
    void vSphereRevertToSnapshot(String server, String vm, String snapshot) {
        vSphereBuildStep(server, 'RevertToSnapshot') {
            delegate.vm vm
            snapshotName snapshot
        }
    }

    @RequiresPlugin(id = 'vsphere-cloud')
    private vSphereBuildStep(String server, String builder, Closure configuration) {
        Integer hash = jobManagement.getVSphereCloudHash(server)
        Preconditions.checkNotNull(hash, "vSphere server ${server} does not exist")

        stepNodes << new NodeBuilder().'org.jenkinsci.plugins.vsphere.VSphereBuildStepContainer' {
            buildStep(class: "org.jenkinsci.plugins.vsphere.builders.${builder}", configuration)
            serverName server
            serverHash hash
        }
    }

    /**
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
     * @since 1.36
     */
    @RequiresPlugin(id = 'xcode-plugin')
    void xcode(@DslContext(XCodeContext) Closure closure = null) {
        XCodeContext c = new XCodeContext()
        ContextHelper.executeInContext(closure, c)

        stepNodes << new NodeBuilder().'au.com.rayh.XCodeBuilder' {
            cleanBeforeBuild                 c.cleanBeforeBuild
            cleanTestReports                 c.cleanTestReports
            configuration                    c.configuration
            target                           c.target
            interpretTargetAsRegEx           c.interpretTargetAsRegEx
            sdk                              c.sdk
            symRoot                          c.symRoot
            configurationBuildDir            c.configurationBuildDir
            xcodeProjectPath                 c.xcodeProjectPath
            xcodeProjectFile                 c.xcodeProjectFile
            xcodebuildArguments              c.xcodebuildArguments
            xcodeSchema                      c.xcodeSchema
            xcodeWorkspaceFile               c.xcodeWorkspaceFile
            embeddedProfileFile              c.embeddedProfileFile
            cfBundleVersionValue             c.cfBundleVersionValue
            cfBundleShortVersionStringValue  c.cfBundleShortVersionStringValue
            buildIpa                         c.buildIpa
            generateArchive                  c.generateArchive
            unlockKeychain                   c.unlockKeychain
            keychainName                     c.keychainName
            keychainPath                     c.keychainPath
            keychainPwd                      c.keychainPwd
            codeSigningIdentity              c.codeSigningIdentity
            allowFailingBuildResults         c.allowFailingBuildResults
            ipaName                          c.ipaName
            ipaOutputDirectory               c.ipaOutputDirectory
            provideApplicationVersion        c.provideApplicationVersion
            changeBundleID                   c.changeBundleID
            bundleID                         c.bundleID
            bundleIDInfoPlistPath            c.bundleIDInfoPlistPath
            ipaManifestPlistUrl              c.ipaManifestPlistUrl
        }
    }

    /**
     * @since 1.36
     */
    @RequiresPlugin(id = 'xcode-plugin')
    void xcodeDevProfile(String idArg) {
        Preconditions.checkNotNullOrEmpty(idArg, 'id must be specified')

        stepNodes << new NodeBuilder().'au.com.rayh.DeveloperProfileLoader' {
            id idArg
        }
    }

    /**
     * @since 1.35
     */
    protected StepContext newInstance() {
        new StepContext(jobManagement, item)
    }
}
