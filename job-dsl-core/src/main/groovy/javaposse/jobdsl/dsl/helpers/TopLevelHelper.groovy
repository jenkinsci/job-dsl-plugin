package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import groovy.transform.Canonical
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

import static javaposse.jobdsl.dsl.helpers.TopLevelHelper.Timeout.absolute

class TopLevelHelper extends AbstractHelper {
    JobManagement jobManagement

    TopLevelHelper(List<WithXmlAction> withXmlActions, JobType jobType, JobManagement jobManagement) {
        super(withXmlActions, jobType)
        this.jobManagement = jobManagement
    }

    def description(String descriptionString) {
        execute {
            def descNode = methodMissing('description', descriptionString)
            it / descNode
        }
    }

    /**
     * "Restrict where this project can be run"
     * <assignedNode>FullTools&amp;&amp;RPM&amp;&amp;DC</assignedNode>
     * @param labelExpression Label of node to use, if null is passed in, the label is cleared out and it can roam
     * @return
     */
    boolean labelAlreadyAdded = false

    def label(String labelExpression = null) {
        Preconditions.checkState(!labelAlreadyAdded, "Label can only be appplied once")
        labelAlreadyAdded = true
        execute {
            if (labelExpression) {
                it / assignedNode(labelExpression)
                it / canRoam('false') // If canRoam is true, the label will not be used
            } else {
                it / assignedNode('')
                it / canRoam('true')
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
    def rvm(String rubySpecification) {
        Preconditions.checkArgument(rubySpecification as Boolean, "Please specify at least the ruby version")
        execute {
            it / buildWrappers / 'ruby-proxy-object' {
                'ruby-object'('ruby-class': 'Jenkins::Plugin::Proxies::BuildWrapper', pluginid: 'rvm') {

                    pluginid('rvm', [pluginid: 'rvm', 'ruby-class': 'String'])
                    object('ruby-class': 'RvmWrapper', pluginid: 'rvm') {
                        impl(rubySpecification, [pluginid: 'rvm', 'ruby-class': 'String'])
                    }
                }
            }
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
        execute {
            def pluginNode = it / buildWrappers / 'hudson.plugins.build__timeout.BuildTimeoutWrapper'
            pluginNode / timeoutMinutes(Integer.toString(timeoutInMinutes))
            pluginNode / failBuild(shouldFailBuild?'true':'false')
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

        execute {
            it / buildWrappers / 'hudson.plugins.build__timeout.BuildTimeoutWrapper' {
                timeoutMinutes ctx.limit
                failBuild ctx.failBuild
                writingDescription ctx.writeDescription
                timeoutPercentage ctx.percentage
                timeoutType ctx.type
                timeoutMinutesElasticDefault ctx.limit
            }
        }
    }


    /**
     * Add environment variables to the build.
     *
     * <project>
     *   <properties>
     *     <EnvInjectJobProperty>
     *       <info>
     *         <propertiesContent>TEST=foo BAR=123</propertiesContent>
     *         <loadFilesFromMaster>false</loadFilesFromMaster>
     *       </info>
     *       <on>true</on>
     *       <keepJenkinsSystemVariables>true</keepJenkinsSystemVariables>
     *       <keepBuildVariables>true</keepBuildVariables>
     *       <contributors/>
     *     </EnvInjectJobProperty>
     */
    def environmentVariables(Closure envClosure) {
        environmentVariables(null, envClosure)
    }

    def environmentVariables(Map<Object, Object> vars, Closure envClosure = null) {
        EnvironmentVariableContext envContext = new EnvironmentVariableContext()
        if (vars) {
            envContext.envs(vars)
        }
        AbstractContextHelper.executeInContext(envClosure, envContext)

        execute {
            it / 'properties' / 'EnvInjectJobProperty' {
                info {
                    propertiesContent(envContext.props.join('\n'))
                    if (envContext.groovyScript) {
                        groovyScriptContent(envContext.groovyScript)
                    }
                    loadFilesFromMaster(false)
                }
                on(true)
                keepJenkinsSystemVariables(true)
                keepBuildVariables(true)
                contributors()
            }
        }
    }

    def static class EnvironmentVariableContext implements Context {
        def props = []
        def groovyScript

        def env(Object key, Object value) {
            props << "${key}=${value}"
        }

        def envs(Map<Object, Object> map) {
            map.entrySet().each {
                env(it.key, it.value)
            }
        }

        def groovy(String script) {
            groovyScript = script
        }
    }

    /*
    <disabled>true</disabled>
     */

    def disabled(boolean shouldDisable = true) {
        execute {
            it / disabled(shouldDisable?'true':'false')
        }
    }

    /**
     <logRotator>
     <daysToKeep>14</daysToKeep>
     <numToKeep>50</numToKeep>
     <artifactDaysToKeep>5</artifactDaysToKeep>
     <artifactNumToKeep>20</artifactNumToKeep>
     </logRotator>

     TODO - Let them specify a closure to fill a context object, I think it would nicer than a bunch of int args
     */

    def logRotator(int daysToKeepInt = -1, int numToKeepInt = -1, int artifactDaysToKeepInt = -1, int artifactNumToKeepInt = -1) {
        execute {
            it / logRotator {
                daysToKeep daysToKeepInt.toString()
                numToKeep numToKeepInt.toString()
                artifactDaysToKeep artifactDaysToKeepInt.toString()
                artifactNumToKeep artifactNumToKeepInt.toString()
            }
        }
    }

    /**
     * Block build if certain jobs are running
     <properties>
         <hudson.plugins.buildblocker.BuildBlockerProperty>
             <useBuildBlocker>true</useBuildBlocker>  <!-- Always true -->
             <blockingJobs>JobA</blockingJobs>
         </hudson.plugins.buildblocker.BuildBlockerProperty>
     </properties>
     */
    def blockOn(Iterable<String> projectNames) {
        blockOn(projectNames.join('\n'))
    }

    /**
     * Block build if certain jobs are running.
     * @param projectName Can be regular expressions. Newline delimited.
     * @return
     */
    def blockOn(String projectName) {
        execute {
            it / 'properties' / 'hudson.plugins.buildblocker.BuildBlockerProperty' {
                useBuildBlocker 'true'
                blockingJobs projectName
            }
        }
    }

    /**
     * Name of the JDK installation to use for this job.
     * @param jdkArg name of the JDK installation to use for this job.
     */
    def jdk(String jdkArg) {
        execute {
            def jdkNode = methodMissing('jdk', jdkArg)
            it / jdkNode
        }
    }

    /**
     * Priority of this job.
     * Requires the <a href="https://wiki.jenkins-ci.org/display/JENKINS/Priority+Sorter+Plugin">Priority Sorter Plugin</a>.
     * Default value is 100.
     *
     * <properties>
     *   <hudson.queueSorter.PrioritySorterJobProperty plugin="PrioritySorter@1.3">
     *     <priority>100</priority>
     *   </hudson.queueSorter.PrioritySorterJobProperty>
     * </properties>
     */
    def priority(int value) {
        execute {
            def node = new Node(it / 'properties', 'hudson.queueSorter.PrioritySorterJobProperty')
            node.appendNode('priority', value)
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

        execute {
            it / 'buildWrappers' / 'org.jvnet.hudson.plugins.port__allocator.PortAllocator' / ports {

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
    /**
     * Adds a quiet period to the project.
     *
     * @param seconds number of seconds to wait
     */
    def quietPeriod(int seconds = 5) {
        execute {
            def node = methodMissing('quietPeriod', seconds)
            it / node
        }
    }

    /**
     * Sets the number of times the SCM checkout is retried on errors.
     *
     * @param times number of attempts
     */
    def checkoutRetryCount(int times = 3) {
        execute {
            def node = methodMissing('scmCheckoutRetryCount', times)
            it / node
        }
    }

    /**
     * Sets a display name for the project.
     *
     * @param displayName name to display
     */
    def displayName(String displayName) {
        def name = Preconditions.checkNotNull(displayName, 'Display name must not be null.')
        execute {
            def node = methodMissing('displayName', name)
            it / node
        }

    }

    /**
     * Configures a custom workspace for the project.
     *
     * @param workspacePath workspace path to use
     */
    def customWorkspace(String workspacePath) {
        def workspace = Preconditions.checkNotNull(workspacePath,"Workspace path must not be null")
        execute {
            def node = methodMissing('customWorkspace', workspace)
            it / node
        }

    }

    /**
     * Configures the job to block when upstream projects are building.
     *
     * @return
     */
    def blockOnUpstreamProjects() {
        execute {
            it / blockBuildWhenDownstreamBuilding(true)
        }
    }

    /**
     * Configures the job to block when downstream projects are building.
     * @return
     */
    def blockOnDownstreamProjects() {
        execute {
            it / blockBuildWhenUpstreamBuilding(true)
        }
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
        execute {
            it / buildWrappers / 'com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper' {
                job jobName
                shareWorkspace useSameWorkspace
            }
        }
    }

    /**
     * Configures the keep Dependencies Flag which can be set in the Fingerprinting action
     *
     * <keepDependencies>true</keepDependencies>
     */
    def keepDependencies(boolean keep = true) {
        execute {
            def node = methodMissing('keepDependencies', keep)
            it / node
        }
    }

    /**
     * <project>
     *     <buildWrappers>
     *         <com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper>
     *             <user>25899f16-1b91-4656-90cd-3f1c26ef6292</user>
     *         </com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper>
     *
     * Provide SSH credentials to builds via a ssh-agent in Jenkins.
     * @param credentials name of the credentials to use
     */
    def sshAgent(String credentials) {
        Preconditions.checkNotNull(credentials, "credentials must not be null")
        String id = jobManagement.getCredentialsId(credentials)
        Preconditions.checkNotNull(id, "credentials not found")
        execute {
            it / buildWrappers / 'com.cloudbees.jenkins.plugins.sshagent.SSHAgentBuildWrapper' {
                user id
            }
        }
    }
}