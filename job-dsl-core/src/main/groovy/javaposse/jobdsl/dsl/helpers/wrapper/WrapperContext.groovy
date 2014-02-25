package javaposse.jobdsl.dsl.helpers.wrapper

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

import static WrapperContext.Timeout.absolute

class WrapperContext implements Context {
    List<Node> wrapperNodes = []
    JobType type
    JobManagement jobManagement

    WrapperContext(JobType jobType, JobManagement jobManagement) {
        this.jobManagement = jobManagement
        this.type = jobType
    }

    WrapperContext(List<Node> wrapperNodes, JobType jobType, JobManagement jobManagement) {
        this(jobType, jobManagement)
        this.wrapperNodes = wrapperNodes
    }

    def timestamps() {
        def nodeBuilder = new NodeBuilder()
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
    def runOnSameNodeAs(String jobName, boolean useSameWorkspace = false) {
        Preconditions.checkNotNull(jobName, "Job name must not be null")
        def nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper' {
            job jobName
            shareWorkspace useSameWorkspace
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
    def rvm(String rubySpecification) {
        Preconditions.checkArgument(rubySpecification as Boolean, "Please specify at least the ruby version")
        def nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'ruby-proxy-object' {
            'ruby-object'('ruby-class': 'Jenkins::Plugin::Proxies::BuildWrapper', pluginid: 'rvm') {

                pluginid('rvm', [pluginid: 'rvm', 'ruby-class': 'String'])
                object('ruby-class': 'RvmWrapper', pluginid: 'rvm') {
                    impl(rubySpecification, [pluginid: 'rvm', 'ruby-class': 'String'])
                }
            }
        }
    }
    /** Enumeration of timeout types for parsing and error reporting*/
    def static enum Timeout {
        absolute,
        elastic,
        likelyStuck
    }

    /**
     * Add a timeout to the build job.
     *
     * May be an absolute, elastic or likely Stuck timeout.
     *
     * @param type type of timeout defaults to absolute
     *
     * @param timeoutClosure optional closure for configuring the timeout
     */
    def timeout(String type = absolute.toString(), Closure timeoutClosure = null) {
        Timeout ttype
        try {
            ttype = Timeout.valueOf(type)
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Timeout type must be one of: ${Timeout.values()}")
        }

        TimeoutContext ctx = new TimeoutContext(ttype)
        AbstractContextHelper.executeInContext(timeoutClosure, ctx)

        def nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'hudson.plugins.build__timeout.BuildTimeoutWrapper' {
            timeoutMinutes ctx.limit
            failBuild ctx.failBuild
            writingDescription ctx.writeDescription
            timeoutPercentage ctx.percentage
            timeoutType ctx.type
            timeoutMinutesElasticDefault ctx.limit
        }
    }


    /*
    <buildWrappers>
      <hudson.plugins.build__timeout.BuildTimeoutWrapper>
        <timeoutMinutes>15</timeoutMinutes>
        <failBuild>true</failBuild>
        <!-- Missing from DSL Call, Elastic and Likely stuck are radio buttons to Absolute -->
        <writingDescription>false</writingDescription>
        <timeoutPercentage>0</timeoutPercentage>
        <timeoutType>absolute</timeoutType>
        <timeoutMinutesElasticDefault>3</timeoutMinutesElasticDefault>
      </hudson.plugins.build__timeout.BuildTimeoutWrapper>
    </buildWrappers>
    */
    def timeout(Integer timeoutInMinutes, Boolean shouldFailBuild = true) {
        def nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'hudson.plugins.build__timeout.BuildTimeoutWrapper' {
            timeoutMinutes(Integer.toString(timeoutInMinutes))
            failBuild(shouldFailBuild?'true':'false')
        }
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
    def allocatePorts(String[] portsArg, Closure closure = null) {
        PortsContext portContext = new PortsContext()
        AbstractContextHelper.executeInContext(closure, portContext)

        def nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'org.jvnet.hudson.plugins.port__allocator.PortAllocator' {
            ports {
                if (portsArg)
                    for (p in portsArg)
                        'org.jvnet.hudson.plugins.port__allocator.DefaultPortType' {
                            name p
                        }

                for (p in portContext.simplePorts)
                    'org.jvnet.hudson.plugins.port__allocator.DefaultPortType' {
                        name p.port
                    }

                for (p in portContext.glassfishPorts)
                    'org.jvnet.hudson.plugins.port__allocator.GlassFishJmxPortType' {
                        name p.port
                        userName p.username
                        password p.password
                    }

                for (p in portContext.tomcatPorts)
                    'org.jvnet.hudson.plugins.port__allocator.TomcatShutdownPortType' {
                        name p.port
                        password p.password
                    }
            }
        }
    }

    def allocatePorts(Closure cl = null) {
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
    def sshAgent(String credentials) {
        Preconditions.checkNotNull(credentials, "credentials must not be null")
        String id = jobManagement.getCredentialsId(credentials)
        Preconditions.checkNotNull(id, "credentials not found")
        def nodeBuilder = new NodeBuilder()
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
    def colorizeOutput(String colorMap) {
        if (colorMap == null) {
            colorMap = "xterm"
        }

        def nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'hudson.plugins.ansicolor.AnsiColorBuildWrapper' {
            'colorMapName'(colorMap)
        }
    }

    /**
     * <pre>
     * {@code
     * <project>
     *     <buildWrappers>
     *         <hudson.plugins.xvnc.Xvnc>
     *             <takeScreenshot>false</takeScreenshot>
     *         </hudson.plugins.xvnc.Xvnc>
     *     </buildWrappers>
     * </project>
     * }
     *
     * Runs build under XVNC.
     * @param takeScreenshotAtEndOfBuild If a screenshot should be taken at the end of the build
     */
    def xvnc(boolean takeScreenshotAtEndOfBuild = false) {
        def nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'hudson.plugins.xvnc.Xvnc' {
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
    def toolenv(String... tools) {
        def nodeBuilder = new NodeBuilder()
        wrapperNodes << nodeBuilder.'hudson.plugins.toolenv.ToolEnvBuildWrapper' {
            vars(tools.collect { it.replaceAll(/[^a-zA-Z0-9_]/, "_").toUpperCase() + "_HOME" }.join(","))
        }
    }

    /**
     * <p>Configures a release using the m2release plugin.</p>
     * <p>By default the following values are applied. If an instance of a
     * closure is provided, the values from the closure will take effect.</p>
     * <pre>
     * {@code
     * <buildWrappers>
     * 	<org.jvnet.hudson.plugins.m2release.M2ReleaseBuildWrapper>
     * 		<scmUserEnvVar></scmUserEnvVar>
     * 		<scmPasswordEnvVar></scmPasswordEnvVar>
     * 		<releaseEnvVar>IS_M2RELEASEBUILD</releaseEnvVar>
     * 		<releaseGoals>-Dresume=false release:prepare release:perform</releaseGoals>
     * 		<dryRunGoals>-Dresume=false -DdryRun=true release:prepare</dryRunGoals>
     * 		<selectCustomScmCommentPrefix>false</selectCustomScmCommentPrefix>
     * 		<selectAppendHudsonUsername>false</selectAppendHudsonUsername>
     * 		<selectScmCredentials>false</selectScmCredentials>
     * 		<numberOfReleaseBuildsToKeep>1</numberOfReleaseBuildsToKeep>
     * 	</org.jvnet.hudson.plugins.m2release.M2ReleaseBuildWrapper>
     * </buildWrappers>
     *}
     * </pre>
     */
    def mavenRelease(Closure releaseClosure = null) {
        M2ReleaseContext context = new M2ReleaseContext()
        AbstractContextHelper.executeInContext(releaseClosure, context)

        def nodeBuilder = NodeBuilder.newInstance()
        def m2releaseNode = nodeBuilder.'org.jvnet.hudson.plugins.m2release.M2ReleaseBuildWrapper' {
            scmUserEnvVar context.scmUserEnvVar
            scmPasswordEnvVar context.scmPasswordEnvVar
            releaseEnvVar context.releaseEnvVar
            releaseGoals context.releaseGoals
            dryRunGoals context.dryRunGoals
            selectCustomScmCommentPrefix context.selectCustomScmCommentPrefix
            selectAppendHudsonUsername context.selectAppendHudsonUsername
            selectScmCredentials context.selectScmCredentials
            numberOfReleaseBuildsToKeep context.numberOfReleaseBuildsToKeep
        }

        wrapperNodes << m2releaseNode
    }
}
