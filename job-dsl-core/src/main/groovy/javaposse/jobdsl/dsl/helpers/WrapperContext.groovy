package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import groovy.transform.Canonical
import javaposse.jobdsl.dsl.JobType

class WrapperContext implements Context {
    List<Node> wrapperNodes = []
    JobType type

    WrapperContext(JobType jobType) {
        this.type = jobType
    }

    WrapperContext(List<Node> wrapperNodes, JobType jobType) {
        this(jobType)
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

    /** Context to configure timeout */
    def static class TimeoutContext implements Context {

        Timeout type
        def limit  = 3
        def failBuild = false
        def writeDescription = false
        def percentage = 0

        TimeoutContext(Timeout type) {
            this.type = type
        }

        def limit(int limit) {
            this.limit = limit
        }

        def failBuild(boolean fail) {
            this.failBuild = fail
        }

        def writeDescription(boolean writeDesc) {
            this.writeDescription = writeDesc
        }

        def percentage(int percentage) {
            this.percentage = percentage
        }

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

    def static class Port {
        String port
        String username
        String password
    }


    @Canonical
    def static class PortsContext implements Context {
        def simplePorts = []
        def glassfishPorts = []
        def tomcatPorts = []

        def port(String port, String... ports) {
            simplePorts << new Port(port: port)
            ports.each {
                simplePorts << new Port(port: port)
            }
        }

        def glassfish(String port, String user, String password) {
            glassfishPorts << new Port(port: port, username: user, password: password)
        }

        def tomcat(String port, String password) {
            tomcatPorts << new Port(port: port, password: password)
        }
    }
}
