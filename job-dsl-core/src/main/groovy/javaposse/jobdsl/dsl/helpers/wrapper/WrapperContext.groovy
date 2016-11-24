package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.RequiresPlugins

@ContextType('hudson.tasks.BuildWrapper')
class WrapperContext extends AbstractExtensibleContext {
    List<Node> wrapperNodes = []

    WrapperContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        wrapperNodes << node
    }

    /**
     * Adds timestamps to the console log.
     */
    @RequiresPlugin(id = 'timestamper')
    void timestamps() {
        wrapperNodes << new NodeBuilder().'hudson.plugins.timestamper.TimestamperBuildWrapper'()
    }

    /**
     * Build the job on the same node as another job and optionally use the same workspace as the other job.
     *
     * @param jobName name of the job
     * @param useSameWorkspace set to <code>true</code> to share the workspace with the given job
     */
    @RequiresPlugin(id = 'job-node-stalker')
    void runOnSameNodeAs(String jobName, boolean useSameWorkspace = false) {
        Preconditions.checkNotNull(jobName, 'Job name must not be null')

        wrapperNodes << new NodeBuilder().'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper' {
            job jobName
            shareWorkspace useSameWorkspace
        }
    }

    /**
     * Specifies the rbenv wrapper to be used during job execution.
     *
     * @since 1.27
     */
    @RequiresPlugins([
            @RequiresPlugin(id = 'rbenv'),
            @RequiresPlugin(id = 'ruby-runtime')
    ])
    void rbenv(String rubyVersion, @DslContext(RbenvContext) Closure rbenvClosure = null) {
        RbenvContext rbenvContext = new RbenvContext()
        ContextHelper.executeInContext(rbenvClosure, rbenvContext)

        wrapperNodes << new NodeBuilder().'ruby-proxy-object' {
            'ruby-object'('ruby-class': rubyWrapperClass, pluginid: 'rbenv') {
                pluginid('rbenv', [pluginid: 'rbenv', 'ruby-class': 'String'])
                object('ruby-class': 'RbenvWrapper', pluginid: 'rbenv') {
                    version(rubyVersion, [pluginid: 'rbenv', 'ruby-class': 'String'])
                    ignore__local__version(rbenvContext.ignoreLocalVersion, [pluginid: 'rbenv', 'ruby-class': 'String'])
                    gem__list(rbenvContext.gems.join(','), [pluginid: 'rbenv', 'ruby-class': 'String'])
                    rbenv__root(rbenvContext.root, [pluginid: 'rbenv', 'ruby-class': 'String'])
                    rbenv__repository(rbenvContext.rbenvRepository, [pluginid: 'rbenv', 'ruby-class': 'String'])
                    rbenv__revision(rbenvContext.rbenvRevision, [pluginid: 'rbenv', 'ruby-class': 'String'])
                    ruby__build__repository(
                            rbenvContext.rubyBuildRepository, [pluginid: 'rbenv', 'ruby-class': 'String']
                    )
                    ruby__build__revision(rbenvContext.rubyBuildRevision, [pluginid: 'rbenv', 'ruby-class': 'String'])
                }
            }
        }
    }

    /**
     * Configures the job to prepare a Ruby environment controlled by RVM for the build.
     *
     * @param rubySpecification Specification of the required ruby version,
     *                          optionally containing a gemset
     *                          (i.e. ruby-1.9.3, ruby-2.0.0@gemset-foo)
     */
    @RequiresPlugins([
            @RequiresPlugin(id = 'rvm'),
            @RequiresPlugin(id = 'ruby-runtime')
    ])
    void rvm(String rubySpecification) {
        jobManagement.logPluginDeprecationWarning('rvm', '0.6')

        Preconditions.checkArgument(rubySpecification as Boolean, 'Please specify at least the ruby version')

        wrapperNodes << new NodeBuilder().'ruby-proxy-object' {
            'ruby-object'('ruby-class': rubyWrapperClass, pluginid: 'rvm') {

                pluginid('rvm', [pluginid: 'rvm', 'ruby-class': 'String'])
                object('ruby-class': 'RvmWrapper', pluginid: 'rvm') {
                    impl(rubySpecification, [pluginid: 'rvm', 'ruby-class': 'String'])
                }
            }
        }
    }

    private String getRubyWrapperClass() {
        jobManagement.logPluginDeprecationWarning('ruby-runtime', '0.13')

        jobManagement.isMinimumPluginVersionInstalled('ruby-runtime', '0.13') ? 'Jenkins::Tasks::BuildWrapperProxy' :
                'Jenkins::Plugin::Proxies::BuildWrapper'
    }

    /**
     * Add a timeout to the build job.
     *
     * Defaults to a absolute timeout with a maximum build time of 3 minutes.
     *
     * @param timeoutClosure optional closure for configuring the timeout
     * @since 1.24
     */
    @RequiresPlugin(id = 'build-timeout', minimumVersion = '1.12')
    void timeout(@DslContext(TimeoutContext) Closure timeoutClosure = null) {
        TimeoutContext context = new TimeoutContext(jobManagement)
        ContextHelper.executeInContext(timeoutClosure, context)

        Node node = new Node(null, 'hudson.plugins.build__timeout.BuildTimeoutWrapper')
        node.append(context.strategy)
        node.appendNode('operationList', context.operations)
        wrapperNodes << node
    }

    /**
     * Allocate ports for build executions to prevent conflicts between build jobs competing for a single port number.
     */
    @RequiresPlugin(id = 'port-allocator')
    void allocatePorts(String[] portsArg, @DslContext(PortsContext) Closure closure = null) {
        PortsContext portContext = new PortsContext()
        ContextHelper.executeInContext(closure, portContext)

        wrapperNodes << new NodeBuilder().'org.jvnet.hudson.plugins.port__allocator.PortAllocator' {
            ports {
                if (portsArg) {
                    for (p in portsArg) {
                        'org.jvnet.hudson.plugins.port__allocator.DefaultPortType' {
                            name p
                        }
                    }
                }
                for (p in portContext.simplePorts) {
                    'org.jvnet.hudson.plugins.port__allocator.DefaultPortType' {
                        name p.port
                    }
                }
                for (p in portContext.glassfishPorts) {
                    'org.jvnet.hudson.plugins.port__allocator.GlassFishJmxPortType' {
                        name p.port
                        userName p.username
                        password p.password
                    }
                }
                for (p in portContext.tomcatPorts) {
                    'org.jvnet.hudson.plugins.port__allocator.TomcatShutdownPortType' {
                        name p.port
                        password p.password
                    }
                }
            }
        }
    }

    /**
     * Allocate ports for build executions to prevent conflicts between build jobs competing for a single port number.
     */
    @RequiresPlugin(id = 'port-allocator')
    void allocatePorts(@DslContext(PortsContext) Closure cl = null) {
        allocatePorts(new String[0], cl)
    }

    /**
     * Provide SSH credentials to builds via a ssh-agent in Jenkins.
     *
     * @param credentials name of the credentials to use
     */
    @RequiresPlugin(id = 'ssh-agent')
    void sshAgent(String... credentials) {
        Preconditions.checkNotNull(credentials, 'credentials must not be null')

        wrapperNodes << new NodeBuilder().'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' {
            credentials.each {
                user(it)
            }
        }
    }

    /**
     * Renders ANSI escape sequences, including color, to console output.
     *
     * @param colorMap name of colormap to use (eg: xterm)
     */
    @RequiresPlugin(id = 'ansicolor')
    void colorizeOutput(String colorMap = 'xterm') {
        wrapperNodes << new NodeBuilder().'hudson.plugins.ansicolor.AnsiColorBuildWrapper' {
            colorMapName(colorMap)
        }
    }

    /**
     * Run a Xvnc session during a build.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'xvnc')
    void xvnc(@DslContext(XvncContext) Closure xvncClosure = null) {
        XvncContext xvncContext = new XvncContext(jobManagement)
        ContextHelper.executeInContext(xvncClosure, xvncContext)

        wrapperNodes << new NodeBuilder().'hudson.plugins.xvnc.Xvnc' {
            takeScreenshot(xvncContext.takeScreenshot)
            if (jobManagement.isMinimumPluginVersionInstalled('xvnc', '1.16')) {
                useXauthority(xvncContext.useXauthority)
            }
        }
    }

    /**
     * Controls the Xvfb virtual frame buffer X11 server.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'xvfb')
    void xvfb(String installation, @DslContext(XvfbContext) Closure closure = null) {
        Preconditions.checkNotNullOrEmpty(installation, 'installation must not be null or empty')

        XvfbContext context = new XvfbContext()
        ContextHelper.executeInContext(closure, context)

        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.xvfb.XvfbBuildWrapper' {
            installationName(installation)
            screen(context.screen)
            debug(context.debug)
            timeout(context.timeout)
            displayNameOffset(context.displayNameOffset)
            shutdownWithBuild(context.shutdownWithBuild)
            autoDisplayName(context.autoDisplayName)
            if (context.assignedLabels) {
                assignedLabels(context.assignedLabels)
            }
            parallelBuild(context.parallelBuild)
        }
    }

    /**
     * Downloads the specified tools, if needed, and puts the path to each of them in the build's environment.
     *
     * @param tools Tool names to import into the environment. They will be transformed
     *              according to the rules used by the toolenv plugin.
     * @since 1.21
     */
    @RequiresPlugin(id = 'toolenv')
    void toolenv(String... tools) {
        wrapperNodes << new NodeBuilder().'hudson.plugins.toolenv.ToolEnvBuildWrapper' {
            vars(tools.collect { "${it.replaceAll(/[^a-zA-Z0-9_]/, '_').toUpperCase()}_HOME" }.join(','))
        }
    }

    /**
     * Injects environment variables into the build.
     *
     * @since 1.21
     */
    @RequiresPlugin(id = 'envinject')
    void environmentVariables(@DslContext(WrapperEnvironmentVariableContext) Closure envClosure) {
        WrapperEnvironmentVariableContext envContext = new WrapperEnvironmentVariableContext(jobManagement)
        ContextHelper.executeInContext(envClosure, envContext)

        wrapperNodes << new NodeBuilder().'EnvInjectBuildWrapper' {
            envContext.addInfoToBuilder(delegate)
        }
    }

    /**
     * Injects passwords as environment variables into the job.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'envinject', minimumVersion = '1.90')
    void injectPasswords(@DslContext(EnvInjectPasswordsContext) Closure injectPasswordsClosure) {
        EnvInjectPasswordsContext injectPasswordsContext = new EnvInjectPasswordsContext(jobManagement)
        ContextHelper.executeInContext(injectPasswordsClosure, injectPasswordsContext)

        wrapperNodes << new NodeBuilder().EnvInjectPasswordWrapper {
            injectGlobalPasswords(injectPasswordsContext.injectGlobalPasswords)
            maskPasswordParameters(injectPasswordsContext.maskPasswordParameters)
            passwordEntries()
        }
    }

    /**
     * Wrap the job with pre- and post-build steps which are only executed when a manual release build is triggered.
     *
     * @since 1.22
     */
    @RequiresPlugin(id = 'release')
    void release(@DslContext(ReleaseContext) Closure releaseClosure) {
        ReleaseContext releaseContext = new ReleaseContext(jobManagement, item)
        ContextHelper.executeInContext(releaseClosure, releaseContext)

        Node releaseNode = new NodeBuilder().'hudson.plugins.release.ReleaseWrapper' {
            releaseVersionTemplate(releaseContext.releaseVersionTemplate ?: '')
            doNotKeepLog(releaseContext.doNotKeepLog)
            overrideBuildParameters(releaseContext.overrideBuildParameters)
            parameterDefinitions(releaseContext.params)
            preBuildSteps(releaseContext.preBuildSteps)
            postSuccessfulBuildSteps(releaseContext.postSuccessfulBuildSteps)
            postBuildSteps(releaseContext.postBuildSteps)
            postFailedBuildSteps(releaseContext.postFailedBuildSteps)
        }

        ContextHelper.executeConfigureBlock(releaseNode, releaseContext.configureBlock)

        wrapperNodes << releaseNode
    }

    /**
     * Apply a Phabricator differential to the workspace before the build starts.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'phabricator-plugin', minimumVersion = '1.8.1')
    void phabricator(@DslContext(PhabricatorContext) Closure closure = null) {
        PhabricatorContext context = new PhabricatorContext()
        ContextHelper.executeInContext(closure, context)

        wrapperNodes << new NodeBuilder().'com.uber.jenkins.phabricator.PhabricatorBuildWrapper' {
            createCommit(context.createCommit)
            applyToMaster(context.applyToMaster)
            showBuildStartedMessage(context.showBuildStartedMessage)
        }
    }

    /**
     * Deletes files from the workspace before the build starts.
     *
     * @since 1.22
     */
    @RequiresPlugin(id = 'ws-cleanup')
    void preBuildCleanup(@DslContext(PreBuildCleanupContext) Closure closure = null) {
        PreBuildCleanupContext context = new PreBuildCleanupContext()
        ContextHelper.executeInContext(closure, context)

        wrapperNodes << new NodeBuilder().'hudson.plugins.ws__cleanup.PreBuildCleanup' {
            patterns(context.patternNodes)
            deleteDirs(context.deleteDirectories)
            cleanupParameter(context.cleanupParameter ?: '')
            externalDelete(context.deleteCommand ?: '')
        }
    }

    /**
     * Monitors the size of the output file of a build and aborts the build if the log file gets too big.
     *
     * @since 1.23
     */
    @RequiresPlugin(id = 'logfilesizechecker')
    void logSizeChecker(@DslContext(LogFileSizeCheckerContext) Closure closure = null) {
        LogFileSizeCheckerContext context = new LogFileSizeCheckerContext()
        ContextHelper.executeInContext(closure, context)

        wrapperNodes << new NodeBuilder().'hudson.plugins.logfilesizechecker.LogfilesizecheckerWrapper' {
            setOwn(context.maxSize > 0)
            maxLogSize(context.maxSize)
            failBuild(context.failBuild)
        }
    }

    /**
     * Sets the display name of a build.
     *
     * @param nameTemplate template defining the build name
     * @since 1.24
     */
    @RequiresPlugin(id = 'build-name-setter')
    void buildName(String nameTemplate) {
        Preconditions.checkNotNull(nameTemplate, 'Name template must not be null')

        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.buildnamesetter.BuildNameSetter' {
            template nameTemplate
        }
    }

    /**
     * Configures keychains for the build.
     *
     * @since 1.24
     */
    @RequiresPlugin(id = 'kpp-management-plugin')
    void keychains(@DslContext(KeychainsContext) Closure keychainsClosure) {
        KeychainsContext keychainsContext = new KeychainsContext()
        ContextHelper.executeInContext(keychainsClosure, keychainsContext)

        wrapperNodes << new NodeBuilder().'com.sic.plugins.kpp.KPPKeychainsBuildWrapper' {
            keychainCertificatePairs keychainsContext.keychains
            deleteKeychainsAfterBuild keychainsContext.delete
            overwriteExistingKeychains keychainsContext.overwrite
        }
    }

    /**
     * Make globally configured files available to the build.
     *
     * @since 1.28
     */
    @RequiresPlugin(id = 'config-file-provider')
    void configFiles(@DslContext(ConfigFilesContext) Closure configFilesClosure) {
        ConfigFilesContext configFilesContext = new ConfigFilesContext(jobManagement)
        ContextHelper.executeInContext(configFilesClosure, configFilesContext)

        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.configfiles.buildwrapper.ConfigFileBuildWrapper' {
            managedFiles {
                configFilesContext.configFiles.each { ConfigFileContext configFileContext ->
                    'org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile' {
                        fileId configFileContext.configFileId
                        targetLocation configFileContext.targetLocation ?: ''
                        variable configFileContext.variable ?: ''
                    }
                }
            }
        }
    }

    /**
     * Configures exclusion plugin resources that are required for the
     * {@link javaposse.jobdsl.dsl.helpers.step.StepContext#criticalBlock(groovy.lang.Closure) criticalBlock} step.
     *
     * @since 1.24
     */
    @RequiresPlugin(id = 'Exclusion')
    void exclusionResources(String... resourceNames) {
        exclusionResources(resourceNames.toList())
    }

    /**
     * Configures exclusion plugin resources that are required for the
     * {@link javaposse.jobdsl.dsl.helpers.step.StepContext#criticalBlock(groovy.lang.Closure) criticalBlock} step.
     *
     * @since 1.24
     */
    @RequiresPlugin(id = 'Exclusion')
    void exclusionResources(Iterable<String> resourceNames) {
        jobManagement.logPluginDeprecationWarning('Exclusion', '0.12')

        wrapperNodes << new NodeBuilder().'org.jvnet.hudson.plugins.exclusion.IdAllocator' {
            ids {
                resourceNames.each { String resourceName ->
                    'org.jvnet.hudson.plugins.exclusion.DefaultIdType' {
                        name(resourceName.toUpperCase(Locale.ENGLISH))
                    }
                }
            }
        }
    }

    /**
     * Creates a pipeline version based on the template and optionally sets that version as display name for the build.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'delivery-pipeline-plugin')
    void deliveryPipelineVersion(String template, boolean setDisplayName = false) {
        wrapperNodes << new NodeBuilder().'se.diabol.jenkins.pipeline.PipelineVersionContributor' {
            versionTemplate(template)
            updateDisplayName(setDisplayName)
        }
    }

    /**
     * Masks the passwords that occur in the console output.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'mask-passwords')
    void maskPasswords() {
        wrapperNodes << new NodeBuilder().'com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsBuildWrapper'()
    }

    /**
     * Adds a number of environment variables with information about the current user.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'build-user-vars-plugin')
    void buildUserVars() {
        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.builduser.BuildUser'()
    }

    /**
     * Sets up a NodeJS environment.
     *
     * @since 1.27
     */
    @RequiresPlugin(id = 'nodejs')
    void nodejs(String installation) {
        wrapperNodes << new NodeBuilder().'jenkins.plugins.nodejs.tools.NpmPackagesBuildWrapper' {
            nodeJSInstallationName(installation)
        }
    }

    /**
     * Sets up a Go environment.
     *
     * @since 1.27
     */
    @RequiresPlugin(id = 'golang')
    void golang(String version) {
        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.golang.GolangBuildWrapper' {
            goVersion(version)
        }
    }

    /**
     * Binds environment variables to credentials.
     *
     * @since 1.28
     */
    @RequiresPlugin(id = 'credentials-binding')
    void credentialsBinding(@DslContext(CredentialsBindingContext) Closure closure) {
        CredentialsBindingContext context = new CredentialsBindingContext(jobManagement, item)
        ContextHelper.executeInContext(closure, context)

        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.credentialsbinding.impl.SecretBuildWrapper' {
            bindings(context.nodes)
        }
    }

    /**
     * Installs custom tools.
     *
     * @since 1.30
     */
    @RequiresPlugin(id = 'custom-tools-plugin')
    void customTools(Iterable<String> tools, @DslContext(CustomToolsContext) Closure closure = null) {
        Preconditions.checkNotNull(tools, 'Please specify some tool names')

        CustomToolsContext context = new CustomToolsContext()
        ContextHelper.executeInContext(closure, context)

        wrapperNodes << new NodeBuilder().'com.cloudbees.jenkins.plugins.customtools.CustomToolInstallWrapper' {
            selectedTools {
                tools.each { tool ->
                    'com.cloudbees.jenkins.plugins.customtools.CustomToolInstallWrapper_-SelectedTool' {
                        name(tool)
                    }
                }
            }
            multiconfigOptions {
                skipMasterInstallation context.skipMasterInstallation
            }
            convertHomesToUppercase context.convertHomesToUppercase
        }
    }

    /**
     * Allows to run build steps before SCM checkout.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'preSCMbuildstep')
    void preScmSteps(@DslContext(PreScmStepsContext) Closure closure) {
        PreScmStepsContext context = new PreScmStepsContext(jobManagement, item)
        ContextHelper.executeInContext(closure, context)

        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.preSCMbuildstep.PreSCMBuildStepsWrapper' {
            buildSteps(context.stepContext.stepNodes)
            failOnError(context.failOnError)
        }
    }

    /**
     * Builds inside a Docker container.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'docker-custom-build-environment', minimumVersion = '1.6.2')
    void buildInDocker(@DslContext(BuildInDockerContext) Closure closure) {
        BuildInDockerContext context = new BuildInDockerContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        Node node = new NodeBuilder().'com.cloudbees.jenkins.plugins.okidocki.DockerBuildWrapper' {
            dockerHost {
                if (context.dockerHostURI) {
                    uri(context.dockerHostURI)
                }
                if (context.serverCredentials) {
                    credentialsId(context.serverCredentials)
                }
            }
            dockerRegistryCredentials(context.registryCredentials ?: '')
            verbose(context.verbose)
            volumes(context.volumes)
            privileged(context.privilegedMode)
            forcePull(context.forcePull)
            group(context.userGroup ?: '')
            command(context.startCommand ?: '')
        }
        node.append(context.selector)
        wrapperNodes << node
    }

    /**
     * Integrates SauceLabs Selenium testing.
     *
     * @since 1.40
     */
    @RequiresPlugin(id = 'sauce-ondemand', minimumVersion = '1.142')
    void sauceOnDemand(@DslContext(SauceOnDemandContext) Closure closure) {
        SauceOnDemandContext context = new SauceOnDemandContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        wrapperNodes << new NodeBuilder().'hudson.plugins.sauce__ondemand.SauceOnDemandBuildWrapper' {
            useGeneratedTunnelIdentifier(context.useGeneratedTunnelIdentifier)
            sendUsageData(context.sendUsageData)
            nativeAppPackage(context.nativeAppPackage ?: '')
            useChromeForAndroid(false)
            sauceConnectPath(context.sauceConnectPath ?: '')
            enableSauceConnect(context.enableSauceConnect)
            seleniumHost(context.seleniumHost ?: '')
            seleniumPort(context.seleniumPort ?: '')
            webDriverBrowsers {
                context.webDriverBrowsers.each { browserName ->
                    string(browserName)
                }
            }
            appiumBrowsers {
                context.appiumBrowsers.each { browserName ->
                    string(browserName)
                }
            }
            useLatestVersion(context.useLatestVersion)
            launchSauceConnectOnSlave(context.launchSauceConnectOnSlave)
            options(context.options ?: '')
            if (jobManagement.isMinimumPluginVersionInstalled('sauce-ondemand', '1.148')) {
                credentialId(context.credentialsId ?: '')
            }
            verboseLogging(context.verboseLogging)
            condition(class: 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun')
        }
    }

    /**
     * Generates JIRA release notes.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'jira', minimumVersion = '1.39')
    void generateJiraReleaseNotes(@DslContext(GenerateJiraReleaseNotesContext) Closure closure) {
        GenerateJiraReleaseNotesContext context = new GenerateJiraReleaseNotesContext()
        ContextHelper.executeInContext(closure, context)

        wrapperNodes << new NodeBuilder().'hudson.plugins.jira.JiraCreateReleaseNotes' {
            jiraEnvironmentVariable(context.environmentVariable ?: '')
            jiraProjectKey(context.projectKey ?: '')
            jiraRelease(context.release ?: '')
            jiraFilter(context.filter ?: '')
        }
    }
}
