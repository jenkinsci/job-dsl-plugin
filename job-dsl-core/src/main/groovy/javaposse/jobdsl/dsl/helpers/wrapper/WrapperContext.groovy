package javaposse.jobdsl.dsl.helpers.wrapper

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.WithXmlAction

class WrapperContext implements Context {
    List<Node> wrapperNodes = []
    JobManagement jobManagement

    WrapperContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    @RequiresPlugin(id = 'timestamper')
    void timestamps() {
        NodeBuilder nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'hudson.plugins.timestamper.TimestamperBuildWrapper'()
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
        NodeBuilder nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper' {
            job jobName
            shareWorkspace useSameWorkspace
        }
    }

    /**
     * @since 1.27
     */
    @RequiresPlugin(id = 'rbenv')
    void rbenv(String rubyVersion, @DslContext(RbenvContext) Closure rbenvClosure = null) {
        RbenvContext rbenvContext = new RbenvContext()
        ContextHelper.executeInContext(rbenvClosure, rbenvContext)

        wrapperNodes << new NodeBuilder().'ruby-proxy-object' {
            'ruby-object'('ruby-class': 'Jenkins::Tasks::BuildWrapperProxy', pluginid: 'rbenv') {
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
     * Support for builds using a rvm environment.
     *
     * @param rubySpecification Specification of the required ruby version,
     *                          optionally containing a gemset
     *                          (i.e. ruby-1.9.3, ruby-2.0.0@gemset-foo)
     */
    @RequiresPlugin(id = 'rvm')
    void rvm(String rubySpecification) {
        Preconditions.checkArgument(rubySpecification as Boolean, 'Please specify at least the ruby version')
        NodeBuilder nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'ruby-proxy-object' {
            'ruby-object'('ruby-class': 'Jenkins::Plugin::Proxies::BuildWrapper', pluginid: 'rvm') {

                pluginid('rvm', [pluginid: 'rvm', 'ruby-class': 'String'])
                object('ruby-class': 'RvmWrapper', pluginid: 'rvm') {
                    impl(rubySpecification, [pluginid: 'rvm', 'ruby-class': 'String'])
                }
            }
        }
    }

    @Deprecated
    static enum Timeout {
        absolute('Absolute'),
        elastic('Elastic'),
        likelyStuck('LikelyStuck'),
        noActivity('NoActivity')

        final String className

        Timeout(String name) {
            className = "hudson.plugins.build_timeout.impl.${name}TimeOutStrategy"
        }
    }

    /**
     * Add a timeout to the build job.
     *
     * @param timeoutClosure optional closure for configuring the timeout
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

    @RequiresPlugin(id = 'port-allocator')
    void allocatePorts(String[] portsArg, @DslContext(PortsContext) Closure closure = null) {
        PortsContext portContext = new PortsContext()
        ContextHelper.executeInContext(closure, portContext)

        NodeBuilder nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'org.jvnet.hudson.plugins.port__allocator.PortAllocator' {
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

    void allocatePorts(@DslContext(PortsContext) Closure cl = null) {
        allocatePorts(new String[0], cl)
    }

    /**
     * Provide SSH credentials to builds via a ssh-agent in Jenkins.
     *
     * @param credentials name of the credentials to use
     */
    @RequiresPlugin(id = 'ssh-agent')
    void sshAgent(String credentials) {
        Preconditions.checkNotNull(credentials, 'credentials must not be null')
        String id = jobManagement.getCredentialsId(credentials)
        Preconditions.checkNotNull(id, 'credentials not found')
        NodeBuilder nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' {
            user id
        }
    }

    /**
     * Converts ANSI escape codes to colors.
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
     * Runs build under XVNC.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'xvnc')
    void xvnc(@DslContext(XvncContext) Closure xvncClosure = null) {
        XvncContext xvncContext = new XvncContext(jobManagement)
        ContextHelper.executeInContext(xvncClosure, xvncContext)

        wrapperNodes << new NodeBuilder().'hudson.plugins.xvnc.Xvnc' {
            takeScreenshot(xvncContext.takeScreenshot)
            if (!jobManagement.getPluginVersion('xvnc')?.isOlderThan(new VersionNumber('1.16'))) {
                useXauthority(xvncContext.useXauthority)
            }
        }
    }

    /**
     * @since 1.31
     */
    @RequiresPlugin(id = 'xvfb')
    void xvfb(String installation, @DslContext(XvfbContext) Closure closure = null) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(installation), 'installation must not be null or empty')

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
     * Lets you use "tools" in unusual ways, such as from shell scripts.
     *
     * Note that we do not check for validity of tool names.
     *
     * @param tools Tool names to import into the environment. They will be transformed
     *   according to the rules used by the toolenv plugin.
     */
    @RequiresPlugin(id = 'toolenv')
    void toolenv(String... tools) {
        NodeBuilder nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'hudson.plugins.toolenv.ToolEnvBuildWrapper' {
            vars(tools.collect { "${it.replaceAll(/[^a-zA-Z0-9_]/, '_').toUpperCase()}_HOME" }.join(','))
        }
    }

    @RequiresPlugin(id = 'envinject')
    void environmentVariables(@DslContext(WrapperEnvironmentVariableContext) Closure envClosure) {
        WrapperEnvironmentVariableContext envContext = new WrapperEnvironmentVariableContext()
        ContextHelper.executeInContext(envClosure, envContext)

        Node envNode = new NodeBuilder().'EnvInjectBuildWrapper' {
            envContext.addInfoToBuilder(delegate)
        }

        wrapperNodes << envNode
    }

    /**
     * Injects global passwords into the job
     */
    @RequiresPlugin(id = 'envinject')
    void injectPasswords() {
        wrapperNodes << new NodeBuilder().'EnvInjectPasswordWrapper' {
            'injectGlobalPasswords'(true)
            'passwordEntries'()
        }
    }

    /**
     * Lets you use "Jenkins Release Plugin" to perform steps inside a release action.
     *
     * @param releaseClosure attributes and steps used by the plugin
     */
    @RequiresPlugin(id = 'release')
    void release(@DslContext(ReleaseContext) Closure releaseClosure) {
        ReleaseContext releaseContext = new ReleaseContext(jobManagement)
        ContextHelper.executeInContext(releaseClosure, releaseContext)

        NodeBuilder nodeBuilder = new NodeBuilder()

        // plugin properties
        Node releaseNode = nodeBuilder.'hudson.plugins.release.ReleaseWrapper' {
            releaseVersionTemplate(releaseContext.releaseVersionTemplate ?: '')
            doNotKeepLog(releaseContext.doNotKeepLog)
            overrideBuildParameters(releaseContext.overrideBuildParameters)
            parameterDefinitions(releaseContext.params)
            preBuildSteps(releaseContext.preBuildSteps)
            postSuccessfulBuildSteps(releaseContext.postSuccessfulBuildSteps)
            postBuildSteps(releaseContext.postBuildSteps)
            postFailedBuildSteps(releaseContext.postFailedBuildSteps)
        }

        // Apply Context
        if (releaseContext.configureBlock) {
            WithXmlAction action = new WithXmlAction(releaseContext.configureBlock)
            action.execute(releaseNode)
        }

        wrapperNodes << releaseNode
    }

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
     * Configures the configuration for the Log File Size Checker build wrapper.
     * See https://wiki.jenkins-ci.org/display/JENKINS/Logfilesizechecker+Plugin
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
     * Enables the "Build Name Setter Plugin" build wrapper.
     * See https://wiki.jenkins-ci.org/display/JENKINS/Build+Name+Setter+Plugin
     *
     * @param nameTemplate template defining the build name. Tokens expansion
     *   mechanism is provided by the Token Macro Plugin.
     */
    @RequiresPlugin(id = 'build-name-setter')
    void buildName(String nameTemplate) {
        Preconditions.checkNotNull(nameTemplate, 'Name template must not be null')

        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.buildnamesetter.BuildNameSetter' {
            template nameTemplate
        }
    }

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

    void exclusionResources(String... resourceNames) {
        exclusionResources(resourceNames.toList())
    }

    @RequiresPlugin(id = 'Exclusion')
    void exclusionResources(Iterable<String> resourceNames) {
        wrapperNodes << new NodeBuilder().'org.jvnet.hudson.plugins.exclusion.IdAllocator' {
            ids {
                resourceNames.each { String resourceName ->
                    'org.jvnet.hudson.plugins.exclusion.DefaultIdType' {
                        name resourceName
                    }
                }
            }
        }
    }

    /**
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
     * @since 1.26
     */
    @RequiresPlugin(id = 'mask-passwords')
    void maskPasswords() {
        wrapperNodes << new NodeBuilder().'com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsBuildWrapper'()
    }

    /**
     * @since 1.26
     */
    @RequiresPlugin(id = 'build-user-vars-plugin')
    void buildUserVars() {
        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.builduser.BuildUser'()
    }

    /**
     * @since 1.27
     */
    @RequiresPlugin(id = 'nodejs')
    void nodejs(String installation) {
        wrapperNodes << new NodeBuilder().'jenkins.plugins.nodejs.tools.NpmPackagesBuildWrapper' {
            nodeJSInstallationName(installation)
        }
    }

    /**
     * @since 1.27
     */
    @RequiresPlugin(id = 'golang')
    void golang(String version) {
        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.golang.GolangBuildWrapper' {
            goVersion(version)
        }
    }

    /**
     * @since 1.28
     */
    @RequiresPlugin(id = 'credentials-binding')
    void credentialsBinding(@DslContext(CredentialsBindingContext) Closure closure) {
        CredentialsBindingContext context = new CredentialsBindingContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.credentialsbinding.impl.SecretBuildWrapper' {
            bindings(context.nodes)
        }
    }

    /**
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
     * @since 1.31
     */
    @RequiresPlugin(id = 'preSCMbuildstep')
    void preScmSteps(@DslContext(PreScmStepsContext) Closure closure) {
        PreScmStepsContext context = new PreScmStepsContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.preSCMbuildstep.PreSCMBuildStepsWrapper' {
            buildSteps(context.stepContext.stepNodes)
            failOnError(context.failOnError)
        }
    }
}
