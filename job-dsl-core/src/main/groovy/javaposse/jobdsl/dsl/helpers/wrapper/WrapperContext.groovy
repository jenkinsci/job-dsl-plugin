package javaposse.jobdsl.dsl.helpers.wrapper

import com.google.common.base.Preconditions
import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction

class WrapperContext implements Context {
    List<Node> wrapperNodes = []
    JobManagement jobManagement

    WrapperContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void timestamps() {
        NodeBuilder nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'hudson.plugins.timestamper.TimestamperBuildWrapper'()
    }

    /**
     * <project>
     *   <buildWrappers>
     *     <com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper plugin="job-node-stalker@1.0.1">
     *       <job>test</job>
     *       <shareWorkspace>true</shareWorkspace>
     *     </com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper>
     *   </buildWrappers>

     * Build the job on the same node as another job and optionally use the same workspace as the other job.
     * @param jobName name of the job
     * @param useSameWorkspace set to <code>true</code> to share the workspace with the given job
     */
    void runOnSameNodeAs(String jobName, boolean useSameWorkspace = false) {
        Preconditions.checkNotNull(jobName, 'Job name must not be null')
        NodeBuilder nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper' {
            job jobName
            shareWorkspace useSameWorkspace
        }
    }

    /**
     * <ruby-proxy-object>
     *     <ruby-object ruby-class="Jenkins::Tasks::BuildWrapperProxy" pluginid="rbenv">
     *         <pluginid pluginid="rbenv" ruby-class="String">rbenv</pluginid>
     *         <object ruby-class="RbenvWrapper" pluginid="rbenv">
     *             <version pluginid="rbenv" ruby-class="String">1.9.3-p484</version>
     *             <ignore__local__version ruby-class="String" pluginid="rbenv">false</ignore__local__version>
     *             <gem__list pluginid="rbenv" ruby-class="String">bundler,rake</gem__list>
     *             <rbenv__root pluginid="rbenv" ruby-class="String">$HOME/.rbenv</rbenv__root>
     *             <rbenv__repository pluginid="rbenv" ruby-class="String">
     *                 https://github.com/sstephenson/rbenv.git
     *             </rbenv__repository>
     *             <rbenv__revision pluginid="rbenv" ruby-class="String">master</rbenv__revision>
     *             <ruby__build__repository pluginid="rbenv" ruby-class="String">
     *                 https://github.com/sstephenson/ruby-build.git
     *             </ruby__build__repository>
     *             <ruby__build__revision pluginid="rbenv" ruby-class="String">master</ruby__build__revision>
     *         </object>
     *     </ruby-object>
     * </ruby-proxy-object>
     */
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
     *
     * Generates XML:
     * <ruby-proxy-object>
     *   <ruby-object ruby-class="Jenkins::Plugin::Proxies::BuildWrapper" pluginid="rvm">
     *     <pluginid pluginid="rvm" ruby-class="String">rvm</pluginid>
     *     <object ruby-class="RvmWrapper" pluginid="rvm">
     *       <impl pluginid="rvm" ruby-class="String">ruby-1.9.2-p290</impl>
     *     </object>
     *   </ruby-object>
     * </ruby-proxy-object>
     */
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
     * <hudson.plugins.build__timeout.BuildTimeoutWrapper>
     *     <strategy class="hudson.plugins.build_timeout.impl.ElasticTimeOutStrategy">
     *        <timeoutPercentage>300</timeoutPercentage>
     *        <numberOfBuilds>3</numberOfBuilds>
     *        <timeoutMinutesElasticDefault>60</timeoutMinutesElasticDefault>
     *     </strategy>
     *     <operationList>
     *         <hudson.plugins.build__timeout.operations.FailOperation/>
     *         <hudson.plugins.build__timeout.operations.AbortOperation/>
     *         <hudson.plugins.build__timeout.operations.WriteDescriptionOperation>
     *             <description>arstrst</description>
     *         </hudson.plugins.build__timeout.operations.WriteDescriptionOperation>
     *     </operationList>
     * </hudson.plugins.build__timeout.BuildTimeoutWrapper>
     *
     * @param timeoutClosure optional closure for configuring the timeout
     */
    void timeout(@DslContext(TimeoutContext) Closure timeoutClosure = null) {
        jobManagement.requireMinimumPluginVersion('build-timeout', '1.12')

        TimeoutContext context = new TimeoutContext(jobManagement)
        ContextHelper.executeInContext(timeoutClosure, context)

        Node node = new Node(null, 'hudson.plugins.build__timeout.BuildTimeoutWrapper')
        node.append(context.strategy)
        node.appendNode('operationList', context.operations)
        wrapperNodes << node
    }

    /*
    <org.jvnet.hudson.plugins.port__allocator.PortAllocator plugin="port-allocator@1.5">
        <ports>
            <org.jvnet.hudson.plugins.port__allocator.DefaultPortType>
                <name>HTTP</name>
            </org.jvnet.hudson.plugins.port__allocator.DefaultPortType>
            <org.jvnet.hudson.plugins.port__allocator.DefaultPortType>
                <name>8080</name>
            </org.jvnet.hudson.plugins.port__allocator.DefaultPortType>
            <org.jvnet.hudson.plugins.port__allocator.GlassFishJmxPortType>
                <name>JMX_PORT</name>
                <userName>admin</userName>
                <password>adminadmin</password>
            </org.jvnet.hudson.plugins.port__allocator.GlassFishJmxPortType>
            <org.jvnet.hudson.plugins.port__allocator.TomcatShutdownPortType>
                <name>SHUTDOWN_PORT</name>
                <password>SHUTDOWN</password>
            </org.jvnet.hudson.plugins.port__allocator.TomcatShutdownPortType>
        </ports>
    </org.jvnet.hudson.plugins.port__allocator.PortAllocator>

     */

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
     * <pre>
     * {@code
     * <project>
     *     <buildWrappers>
     *         <com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper>
     *             <user>25899f16-1b91-4656-90cd-3f1c26ef6292</user>
     *         </com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper>
     * }
     * </pre>
     * Provide SSH credentials to builds via a ssh-agent in Jenkins.
     * @param credentials name of the credentials to use
     */
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
     * <pre>
     * {@code
     * <project>
     *     <buildWrappers>
     *         <hudson.plugins.ansicolor.AnsiColorBuildWrapper>
     *             <colorMapName>xterm</colorMapName>
     *         </hudson.plugins.ansicolor.AnsiColorBuildWrapper>
     *     </buildWrappers>
     * </project>
     * }
     *
     * Converts ANSI escape codes to colors.
     * @param colorMap name of colormap to use (eg: xterm)
     */
    void colorizeOutput(String colorMap = 'xterm') {
        wrapperNodes << new NodeBuilder().'hudson.plugins.ansicolor.AnsiColorBuildWrapper' {
            colorMapName(colorMap)
        }
    }

    /**
     * <pre>
     * {@code
     * <project>
     *     <buildWrappers>
     *         <hudson.plugins.xvnc.Xvnc>
     *             <takeScreenshot>false</takeScreenshot>
     *             <useXauthority>true</useXauthority>
     *         </hudson.plugins.xvnc.Xvnc>
     *     </buildWrappers>
     * </project>
     * }
     *
     * Runs build under XVNC.
     */
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

    void xvnc(boolean takeScreenshotAtEndOfBuild) {
        jobManagement.logDeprecationWarning()
        xvnc {
            takeScreenshot(takeScreenshotAtEndOfBuild)
        }
    }

    /**
     * <pre>
     * {@code
     * <project>
     *     <buildWrappers>
     *         <hudson.plugins.toolenv.ToolEnvBuildWrapper>
     *             <vars>ANT_1_8_2_HOME,MAVEN_3_0_HOME,DEFAULT_HOME</vars>
     *         </hudson.plugins.toolenv.ToolEnvBuildWrapper>
     *     </buildWrappers>
     * </project>
     * }
     * </pre>
     *
     * Lets you use "tools" in unusual ways, such as from shell scripts.
     *
     * Note that we do not check for validity of tool names.
     *
     * @param tools Tool names to import into the environment. They will be transformed
     *   according to the rules used by the toolenv plugin.
     */
    void toolenv(String... tools) {
        NodeBuilder nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'hudson.plugins.toolenv.ToolEnvBuildWrapper' {
            vars(tools.collect { "${it.replaceAll(/[^a-zA-Z0-9_]/, '_').toUpperCase()}_HOME" }.join(','))
        }
    }

    /**
     * <pre>
     *     {@code
     * <EnvInjectBuildWrapper>
     *   <info>
     *     <propertiesFilePath>some.properties</propertiesFilePath>
     *     <propertiesContent>REV=14</propertiesContent>
     *     <scriptFilePath>/test/script.sh</scriptFilePath>
     *     <scriptContent>echo 5</scriptContent>
     *     <loadFilesFromMaster>false</loadFilesFromMaster>
     *   </info>
     * </EnvInjectBuildWrapper>
     * }
     * </pre>
     * @param envClosure
     * @return
     */
    void environmentVariables(@DslContext(WrapperEnvironmentVariableContext) Closure envClosure) {
        WrapperEnvironmentVariableContext envContext = new WrapperEnvironmentVariableContext()
        ContextHelper.executeInContext(envClosure, envContext)

        Node envNode = new NodeBuilder().'EnvInjectBuildWrapper' {
            envContext.addInfoToBuilder(delegate)
        }

        wrapperNodes << envNode
    }

    /**
     * <pre>
     * {@code
     * <project>
     *     <buildWrappers>
     *         <EnvInjectPasswordWrapper>
     *             <injectGlobalPasswords>true</injectGlobalPasswords>
     *             <passwordEntries/>
     *         </EnvInjectPasswordWrapper>
     *     </buildWrappers>
     * </project>
     * }
     *
     * Injects global passwords into the job
     */
    void injectPasswords() {
        wrapperNodes << new NodeBuilder().'EnvInjectPasswordWrapper' {
            'injectGlobalPasswords'(true)
            'passwordEntries'()
        }
    }

    /**
     * {@code
     *  <project>
     *      <buildWrappers>
     *          <hudson.plugins.release.ReleaseWrapper>
     *              <releaseVersionTemplate>template</releaseVersionTemplate>
     *              <doNotKeepLog>true</doNotKeepLog>
     *              <overrideBuildParameters>false</overrideBuildParameters>
     *              <parameterDefinitions>
     *                  <hudson.model.BooleanParameterDefinition>
     *                      <name>booleanValue</name>
     *                      <description>ths description of the boolean value</description>
     *                      <defaultValue>true</defaultValue>
     *                  </hudson.model.BooleanParameterDefinition>
     *              </parameterDefinitions>
     *              <preBuildSteps>
     *                  <hudson.tasks.Maven>
     *                      <targets>install</targets>
     *                      <mavenName>(Default)</mavenName>
     *                  </hudson.tasks.Maven>
     *              </preBuildSteps>
     *              <postBuildSteps>
     *                  <hudson.tasks.Maven>
     *                      <targets>site</targets>
     *                      <mavenName>(Default)</mavenName>
     *                 </hudson.tasks.Maven>
     *              </postBuildSteps>
     *          </hudson.plugins.release.ReleaseWrapper>
     *      </buildWrappers>
     *  </project>
     * }
     * </pre>
     *
     * Lets you use "Jenkins Release Plugin" to perform steps inside a release action.
     *
     * @param releaseClosure attributes and steps used by the plugin
     */
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

    /**
     * <project>
     *     <buildWrappers>
     *         <hudson.plugins.ws__cleanup.PreBuildCleanup>
     *             <patterns>
     *                 <hudson.plugins.ws__cleanup.Pattern>
     *                     <pattern>*.class</pattern>
     *                     <type>INCLUDE</type>
     *                 </hudson.plugins.ws__cleanup.Pattern>
     *             </patterns>
     *             <deleteDirs>false</deleteDirs>
     *             <cleanupParameter/>
     *             <externalDelete/>
     *         </hudson.plugins.ws__cleanup.PreBuildCleanup>
     *     </buildWrappers>
     * </project>
     */
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
     *
     * <project>
     *     <buildWrappers>
     *         <hudson.plugins.logfilesizechecker.LogfilesizecheckerWrapper>
     *             <setOwn>true</setOwn>
     *             <maxLogSize>10</maxLogSize>
     *             <failBuild>true</failBuild>
     *         </hudson.plugins.logfilesizechecker.LogfilesizecheckerWrapper>
     *     </buildWrappers>
     * </project>
     */
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
     * <project>
     *     <buildWrappers>
     *         <org.jenkinsci.plugins.buildnamesetter.BuildNameSetter>
     *             <template>#${BUILD_NUMBER} on ${ENV,var="BRANCH"}</template>
     *         </org.jenkinsci.plugins.buildnamesetter.BuildNameSetter>
     *     </buildWrappers>
     * </project>
     *
     * @param nameTemplate template defining the build name. Tokens expansion
     *   mechanism is provided by the Token Macro Plugin.
     */
    void buildName(String nameTemplate) {
        Preconditions.checkNotNull(nameTemplate, 'Name template must not be null')

        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.buildnamesetter.BuildNameSetter' {
            template nameTemplate
        }
    }

    /**
     * <com.sic.plugins.kpp.KPPKeychainsBuildWrapper>
     *     <keychainCertificatePairs>
     *         <com.sic.plugins.kpp.model.KPPKeychainCertificatePair>
     *             <keychain></keychain>
     *             <codeSigningIdentity></codeSigningIdentity>
     *             <varPrefix></varPrefix>
     *         </com.sic.plugins.kpp.model.KPPKeychainCertificatePair>
     *     </keychainCertificatePairs>
     *     <deleteKeychainsAfterBuild>false</deleteKeychainsAfterBuild>
     *     <overwriteExistingKeychains>false</overwriteExistingKeychains>
     * </com.sic.plugins.kpp.KPPKeychainsBuildWrapper>
     */
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
     * <org.jenkinsci.plugins.configfiles.buildwrapper.ConfigFileBuildWrapper>
     *     <managedFiles>
     *         <org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile>
     *             <fileId>CustomConfig1417476679249</fileId>
     *             <targetLocation>/tmp/test.txt</targetLocation>
     *             <variable>FILE</variable>
     *         </org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile>
     *         <org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile>
     *             <fileId>CustomConfig1417476679250</fileId>
     *             <targetLocation>/tmp/other.txt</targetLocation>
     *             <variable>OTHER</variable>
     *         </org.jenkinsci.plugins.configfiles.buildwrapper.ManagedFile>
     *     </managedFiles>
     * </org.jenkinsci.plugins.configfiles.buildwrapper.ConfigFileBuildWrapper>
     */
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
     * <org.jvnet.hudson.plugins.exclusion.IdAllocator>
     *     <ids>
     *         <org.jvnet.hudson.plugins.exclusion.DefaultIdType>
     *             <name>example</name>
     *         </org.jvnet.hudson.plugins.exclusion.DefaultIdType>
     *     </ids>
     * </org.jvnet.hudson.plugins.exclusion.IdAllocator>
     */
    void exclusionResources(String... resourceNames) {
        exclusionResources(resourceNames.toList())
    }

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
     * <se.diabol.jenkins.pipeline.PipelineVersionContributor>
     *     <versionTemplate>1.0.${BUILD_NUMBER}</versionTemplate>
     *     <updateDisplayName>true</updateDisplayName>
     * </se.diabol.jenkins.pipeline.PipelineVersionContributor>
     */
    void deliveryPipelineVersion(String template, boolean setDisplayName = false) {
        wrapperNodes << new NodeBuilder().'se.diabol.jenkins.pipeline.PipelineVersionContributor' {
            versionTemplate(template)
            updateDisplayName(setDisplayName)
        }
    }

    /**
     * <com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsBuildWrapper>
     * </com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsBuildWrapper>
     */
    void maskPasswords() {
        wrapperNodes << new NodeBuilder().'com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsBuildWrapper'()
    }

    /**
     * <org.jenkinsci.plugins.builduser.BuildUser>
     * </org.jenkinsci.plugins.builduser.BuildUser>
     */
    void buildUserVars() {
        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.builduser.BuildUser'()
    }

    /**
     * <jenkins.plugins.nodejs.tools.NpmPackagesBuildWrapper>
     *     <nodeJSInstallationName>NodeJS 0.10.26</nodeJSInstallationName>
     * </jenkins.plugins.nodejs.tools.NpmPackagesBuildWrapper>
     */
    void nodejs(String installation) {
        wrapperNodes << new NodeBuilder().'jenkins.plugins.nodejs.tools.NpmPackagesBuildWrapper' {
            nodeJSInstallationName(installation)
        }
    }

    /**
     * <org.jenkinsci.plugins.golang.GolangBuildWrapper>
     *     <goVersion>Go 1.3.3</goVersion>
     * </org.jenkinsci.plugins.golang.GolangBuildWrapper>
     */
    void golang(String version) {
        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.golang.GolangBuildWrapper' {
            goVersion(version)
        }
    }

    /**
     * <org.jenkinsci.plugins.credentialsbinding.impl.SecretBuildWrapper>
     *     <bindings>
     *         <org.jenkinsci.plugins.credentialsbinding.impl.FileBinding>
     *             <variable>FOO</variable>
     *             <credentialsId>b1f273ef-4219-4fa0-9489-53dc08df58ef</credentialsId>
     *         </org.jenkinsci.plugins.credentialsbinding.impl.FileBinding>
     *         <org.jenkinsci.plugins.credentialsbinding.impl.StringBinding>
     *             <variable>BAR</variable>
     *             <credentialsId>b1f273ef-4219-4fa0-9489-53dc08df58ef</credentialsId>
     *         </org.jenkinsci.plugins.credentialsbinding.impl.StringBinding>
     *         <org.jenkinsci.plugins.credentialsbinding.impl.UsernamePasswordBinding>
     *             <variable>BAZ</variable>
     *             <credentialsId>7725107c-5110-45dc-84b4-2482b75022f1</credentialsId>
     *         </org.jenkinsci.plugins.credentialsbinding.impl.UsernamePasswordBinding>
     *         <org.jenkinsci.plugins.credentialsbinding.impl.ZipFileBinding>
     *             <variable>ZIP</variable>
     *             <credentialsId>b1f273ef-4219-4fa0-9489-53dc08df58ef</credentialsId>
     *         </org.jenkinsci.plugins.credentialsbinding.impl.ZipFileBinding>
     *     </bindings>
     * </org.jenkinsci.plugins.credentialsbinding.impl.SecretBuildWrapper>
     */
    void credentialsBinding(@DslContext(CredentialsBindingContext) Closure closure) {
        CredentialsBindingContext context = new CredentialsBindingContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        wrapperNodes << new NodeBuilder().'org.jenkinsci.plugins.credentialsbinding.impl.SecretBuildWrapper' {
            'bindings' {
                context.file.each { key, value ->
                    'org.jenkinsci.plugins.credentialsbinding.impl.FileBinding' {
                        variable(key)
                        credentialsId(value)
                    }
                }
                context.string.each { key, value ->
                    'org.jenkinsci.plugins.credentialsbinding.impl.StringBinding' {
                        variable(key)
                        credentialsId(value)
                    }
                }
                context.usernamePassword.each { key, value ->
                    'org.jenkinsci.plugins.credentialsbinding.impl.UsernamePasswordBinding' {
                        variable(key)
                        credentialsId(value)
                    }
                }
                context.zipFile.each { key, value ->
                    'org.jenkinsci.plugins.credentialsbinding.impl.ZipFileBinding' {
                        variable(key)
                        credentialsId(value)
                    }
                }
            }
        }
    }

    /**
     * <com.cloudbees.jenkins.plugins.customtools.CustomToolInstallWrapper>
     *     <selectedTools>
     *         <com.cloudbees.jenkins.plugins.customtools.CustomToolInstallWrapper_-SelectedTool>
     *             <name>FOO</name>
     *         </com.cloudbees.jenkins.plugins.customtools.CustomToolInstallWrapper_-SelectedTool>
     *         <com.cloudbees.jenkins.plugins.customtools.CustomToolInstallWrapper_-SelectedTool>
     *             <name>BAR</name>
     *         </com.cloudbees.jenkins.plugins.customtools.CustomToolInstallWrapper_-SelectedTool>
     *     </selectedTools>
     *     <multiconfigOptions>
     *         <skipMasterInstallation>false</skipMasterInstallation>
     *     </multiconfigOptions>
     *     <convertHomesToUppercase>true</convertHomesToUppercase>
     * </com.cloudbees.jenkins.plugins.customtools.CustomToolInstallWrapper>
     */
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
}
