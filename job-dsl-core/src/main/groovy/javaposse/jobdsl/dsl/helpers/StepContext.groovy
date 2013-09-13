package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import groovy.transform.Canonical
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

import static javaposse.jobdsl.dsl.helpers.StepContext.DslContext.RemovedJobAction.IGNORE

class StepContext implements Context {
    List<Node> stepNodes = []
    JobType type

    StepContext(JobType jobType) {
        this.type = jobType
    }

    StepContext(List<Node> stepNodes, JobType jobType) {
        this(jobType)
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
    def gradle(String tasksArg = null, String switchesArg = null, Boolean useWrapperArg = true, Closure configure = null) {
        def nodeBuilder = new NodeBuilder()
        def gradleNode = nodeBuilder.'hudson.plugins.gradle.Gradle' {
            description ''
            switches switchesArg?:''
            tasks tasksArg?:''
            rootBuildScriptDir ''
            buildFile ''
            useWrapper useWrapperArg==null?'true':useWrapperArg.toString()
            wrapperScript ''
        }
        // Apply Context
        if (configure) {
            WithXmlAction action = new WithXmlAction(configure)
            action.execute(gradleNode)
        }
        stepNodes << gradleNode
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

    private void buildDslNode(context) {
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

    def static class DslContext implements Context {

        enum RemovedJobAction {
            IGNORE,
            DISABLE,
            DELETE
        }

        String scriptText = ''
        RemovedJobAction removedJobAction = IGNORE
        def externalScripts = []
        def ignoreExisting = false

        def text(String text) {
            this.scriptText = Preconditions.checkNotNull(text)
        }

        def useScriptText() {
            scriptText.length()>0
        }

        def external(String... dslScripts) {
            externalScripts.addAll(dslScripts)
        }

        def getTargets() {
            externalScripts.join('\n')
        }

        def ignoreExisting(boolean ignore = true) {
            this.ignoreExisting = ignore
        }

        def removeAction(String action) {

            try {
                this.removedJobAction = RemovedJobAction.valueOf(action)
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("removeAction must be one of: ${RemovedJobAction.values()}")
            }


        }

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

    def static class AntContext implements Context {
        def targets = []
        def props = []
        def buildFile = null
        def antOpts = []
        def antName = null

        def target(String target) {
            targets << target
        }

        def targets(Iterable<String> addlTargets) {
            addlTargets.each {
                target(it)
            }
        }

        def prop(Object key, Object value) {
            props << "${key}=${value}"
        }

        def props(Map<String, String> map) {
            map.entrySet().each {
                prop(it.key, it.value)
            }
        }

        def buildFile(String buildFile) {
            this.buildFile = buildFile
        }

        def javaOpt(String opt) {
            antOpts << opt
        }

        def javaOpts(Iterable<String> opts) {
            opts.each { javaOpt(it) }
        }

        def antInstallation(String antInstallationName) {
            antName = antInstallationName
        }
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

    private def groovyScriptSource(String commandOrFileName, boolean isCommand) {
        def nodeBuilder = new NodeBuilder()
        nodeBuilder.scriptSource(class: "hudson.plugins.groovy.${isCommand ? 'String' : 'File'}ScriptSource") {
            if (isCommand) {
                command commandOrFileName
            } else {
                scriptFile commandOrFileName
            }
        }
    }

    private def groovy(String commandOrFileName, boolean isCommand, String groovyInstallation, Closure groovyClosure) {
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

    def static abstract class AbstractGroovyContext implements Context {
        def classpathEntries = []

        def classpath(String classpath) {
            classpathEntries << classpath
        }
    }

    def static class GroovyContext extends AbstractGroovyContext {
        def groovyParams = []
        def scriptParams = []
        def props = []
        def javaOpts = []
        def groovyInstallation = null

        def groovyParam(String param) {
            groovyParams << param
        }

        def groovyParams(Iterable<String> params) {
            params.each { groovyParam(it) }
        }

        def scriptParam(String param) {
            scriptParams << param
        }

        def scriptParams(Iterable<String> params) {
            params.each { scriptParam(it) }
        }

        def prop(String key, String value) {
            props << "${key}=${value}"
        }

        def props(Map<String, String> map) {
            map.entrySet().each {
                prop(it.key, it.value)
            }
        }

        def javaOpt(String opt) {
            javaOpts << opt
        }

        def javaOpts(Iterable<String> opts) {
            opts.each { javaOpt(it) }
        }

        def groovyInstallation(String groovyInstallationName) {
            groovyInstallation = groovyInstallationName
        }
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

    private def systemGroovy(String commandOrFileName, boolean isCommand, Closure systemGroovyClosure) {
        def systemGroovyContext = new SystemGroovyContext()
        AbstractContextHelper.executeInContext(systemGroovyClosure, systemGroovyContext)

        def systemGroovyNode = NodeBuilder.newInstance().'hudson.plugins.groovy.SystemGroovy' {
            bindings systemGroovyContext.bindings.collect({ key, value -> "${key}=${value}" }).join('\n')
            classpath systemGroovyContext.classpathEntries.join(File.pathSeparator)
        }
        systemGroovyNode.append(groovyScriptSource(commandOrFileName, isCommand))

        stepNodes << systemGroovyNode
    }

    def static class SystemGroovyContext extends AbstractGroovyContext {
        Map<String, String> bindings = [:]

        def binding(String name, String value) {
            bindings[name] = value
        }
    }

    /**
     <hudson.tasks.Maven>
     <targets>install</targets>
     <mavenName>(Default)</mavenName>
     <pom>pom.xml</pom>
     <usePrivateRepository>false</usePrivateRepository>
     </hudson.tasks.Maven>
     */
    def maven(String targetsArg = null, String pomArg = null, Closure configure = null) {
        def nodeBuilder = new NodeBuilder()
        def mavenNode = nodeBuilder.'hudson.tasks.Maven' {
            targets targetsArg?:''
            mavenName '(Default)' // TODO
            if (pomArg) {
              pom pomArg
            }
            usePrivateRepository 'false'
        }
        // Apply Context
        if (configure) {
            WithXmlAction action = new WithXmlAction(configure)
            action.execute(mavenNode)
        }
        stepNodes << mavenNode

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

    def static class GrailsContext implements Context {
        List<String> targets = []
        String name = '(Default)'
        String grailsWorkDir = ''
        String projectWorkDir = ''
        String projectBaseDir = ''
        String serverPort = ''
        Map<String, String> props = [:]
        boolean forceUpgrade = false
        boolean nonInteractive = true
        boolean useWrapper = false

        def target(String target) {
            targets << target
        }

        def targets(Iterable<String> addlTargets) {
            addlTargets.each {
                target(it)
            }
        }

        String getTargetsString() {
            targets.join(' ')
        }

        def name(String name) {
            this.name = name
        }

        def grailsWorkDir(String grailsWorkDir) {
            this.grailsWorkDir = grailsWorkDir
        }

        def projectWorkDir(String projectWorkDir) {
            this.projectWorkDir = projectWorkDir
        }

        def projectBaseDir(String projectBaseDir) {
            this.projectBaseDir = projectBaseDir
        }

        def serverPort(String serverPort) {
            this.serverPort = serverPort
        }

        def prop(String key, String value) {
            props[key] = value
        }

        def props(Map<String, String> map) {
            props += map
        }

        String getPropertiesString() {
            props.collect { k, v -> "$k=$v" }.join('\n')
        }

        def forceUpgrade(boolean forceUpgrade) {
            this.forceUpgrade = forceUpgrade
        }

        def nonInteractive(boolean nonInteractive) {
            this.nonInteractive = nonInteractive
        }

        def useWrapper(boolean useWrapper) {
            this.useWrapper = useWrapper
        }

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
                    buildNumber Integer.toString(copyArtifactContext.buildNumber)
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

    def static class CopyArtifactContext implements Context {
        String selectedSelector
        boolean fallback
        String permalinkName
        int buildNumber
        String parameterName

        private void ensureFirst() {
            if (selectedSelector!=null) {
                throw new IllegalStateException("Only one selector can be chosen")
            }
        }
        /**
         * Upstream build that triggered this job
         * @arg fallback Use "Last successful build" as fallback
         * @return
         */
        def upstreamBuild(boolean fallback = false) {
            ensureFirst()
            this.fallback = fallback
            selectedSelector = 'TriggeredBuild'
        }

        /**
         * Latest successful build
         * @return
         */
        def latestSuccessful() {
            ensureFirst()
            selectedSelector = 'StatusBuild'
        }
        /**
         * Latest saved build (marked "keep forever")
         * @return
         */
        def latestSaved() {
            ensureFirst()
            selectedSelector = 'SavedBuild'
        }
        /**
         * Specified by permalink
         * @param linkName Values like lastBuild, lastStableBuild
         * @return
         */
        def permalink(String linkName) {
            ensureFirst()
            selectedSelector = 'PermalinkBuild'
            permalinkName = linkName
        }

        /**
         * Specific Build
         * @param buildNumber
         * @return
         */
        def buildNumber(int buildNumber) {
            ensureFirst()
            selectedSelector = 'SpecificBuild'
            this.buildNumber = buildNumber
        }

        /**
         * Copy from WORKSPACE of latest completed build
         * @return
         */
        def workspace() {
            ensureFirst()
            selectedSelector = 'Workspace'
        }

        /**
         * Specified by build parameter
         * @param parameterName
         * @return
         */
        def buildParameter(String parameterName) {
            ensureFirst()
            selectedSelector = 'ParameterizedBuild'
            this.parameterName = parameterName
        }
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
                            configs {
                                if (!jobInPhase.boolParams.isEmpty()) {
                                    'hudson.plugins.parameterizedtrigger.BooleanParameters'(plugin:'parameterized-trigger@2.17') {
                                        configs {
                                            jobInPhase.boolParams.each { k, v ->
                                                def boolConfigNode = 'hudson.plugins.parameterizedtrigger.BooleanParameterConfig' {
                                                    value(v?'true':'false')
                                                }
                                                boolConfigNode.appendNode('name', k)
                                            }
                                        }
                                    }
                                }
                                if (jobInPhase.fileParam) {
                                    'hudson.plugins.parameterizedtrigger.FileBuildParameters'(plugin:'parameterized-trigger@2.17') {
                                        propertiesFile jobInPhase.fileParam
                                        failTriggerOnMissing jobInPhase.failTriggerOnMissing?'true':'false'
                                    }
                                }
                                if (jobInPhase.nodeParam) {
                                    'hudson.plugins.parameterizedtrigger.NodeParameters'(plugin:'parameterized-trigger@2.17')
                                }
                                if (jobInPhase.currentJobParameters) {
                                    // Not sure how this differs from currParams
                                    'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'(plugin:'parameterized-trigger@2.17')
                                }
                                if (jobInPhase.matrixFilter) {
                                    'hudson.plugins.parameterizedtrigger.matrix.MatrixSubsetBuildParameters'(plugin:'parameterized-trigger@2.17') {
                                        filter jobInPhase.matrixFilter
                                    }
                                }
                                if (jobInPhase.subversionRevision != null) {
                                    'hudson.plugins.parameterizedtrigger.SubversionRevisionBuildParameters'(plugin:'parameterized-trigger@2.17') {
                                        includeUpstreamParameters jobInPhase.subversionRevision?'true':'false'
                                    }
                                }
                                if (jobInPhase.gitRevision != null) {
                                    'hudson.plugins.git.GitRevisionBuildParameters'(plugin:'git@1.3.0') {
                                        combineQueuedCommits jobInPhase.gitRevision?'true':'false'
                                    }
                                }
                                if (jobInPhase.props) {
                                    'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters'(plugin:'parameterized-trigger@2.17') {
                                        'properties'(jobInPhase.props.join('\n'))
                                    }
                                }
                            }
                        } else {
                            configs(class:'java.util.Collections$EmptyList')
                        }
                    }
                }
            }
        }
        stepNodes << multiJobPhaseNode
    }

    @Canonical
    static class PhaseContext implements Context {
        String phaseName
        String continuationCondition

        List<PhaseJobContext> jobsInPhase = []

        void phaseName(String phaseName) {
            this.phaseName = phaseName
        }

        void continuationCondition(String continuationCondition) {
            this.continuationCondition = continuationCondition
        }

        def job(String jobName, Closure phaseJobClosure = null) {
            job(jobName, true, true, phaseJobClosure)
        }

        def job(String jobName, boolean currentJobParameters, Closure phaseJobClosure = null) {
            job(jobName, currentJobParameters, true, phaseJobClosure)
        }

        def job(String jobName, boolean currentJobParameters, boolean exposedScm, Closure phaseJobClosure = null) {
            PhaseJobContext phaseJobContext = new PhaseJobContext(jobName, currentJobParameters, exposedScm)
            AbstractContextHelper.executeInContext(phaseJobClosure, phaseJobContext)

            jobsInPhase << phaseJobContext

            return phaseJobContext
        }
    }

    @Canonical
    static class PhaseJobContext implements Context {
        String jobName
        boolean currentJobParameters = true
        boolean exposedScm = true

        Map<String, Boolean> boolParams = [:]
        String fileParam
        boolean failTriggerOnMissing
        boolean nodeParam = false
        String matrixFilter
        Boolean subversionRevision
        Boolean gitRevision
        def props = []

        void jobName(String jobName) {
            this.jobName = jobName
        }

        def currentJobParameters(boolean currentJobParameters = true) {
            this.currentJobParameters = currentJobParameters
        }

        def exposedScm(boolean exposedScm = true) {
            this.exposedScm = exposedScm
        }

        def boolParam(String paramName, boolean defaultValue = false) {
            boolParams[paramName] = defaultValue
        }

        def fileParam(String propertyFile, boolean failTriggerOnMissing = false) {
            Preconditions.checkState(!fileParam, "File parameter already set with ${fileParam}")
            this.fileParam = propertyFile
            this.failTriggerOnMissing = failTriggerOnMissing
        }

        def sameNode(boolean nodeParam = true) {
            this.nodeParam = nodeParam
        }

        def matrixParam(String filter) {
            Preconditions.checkState(!matrixFilter, "Matrix parameter already set with ${matrixFilter}")
            this.matrixFilter = filter
        }

        def subversionRevision(boolean includeUpstreamParameters = false) {
            this.subversionRevision = includeUpstreamParameters
        }

        def gitRevision(boolean combineQueuedCommits = false) {
            this.gitRevision = combineQueuedCommits
        }

        def prop(Object key, Object value) {
            props << "${key}=${value}"
        }

        def props(Map<String, String> map) {
            map.entrySet().each {
                prop(it.key, it.value)
            }
        }

        def hasConfig() {
            return !boolParams.isEmpty() || fileParam || nodeParam || matrixFilter || subversionRevision != null || gitRevision != null || !props.isEmpty()
        }
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
    def buildConditionalStepSingle(String conditionNameArg = 'String', String firstArgument = null, String secondArgument = null, String ignoreCaseArgument = 'true', String conditionSuccess, Collection<String> conditionSuccessArguments, String conditionFailure = "Fail" ) {

        def nodeBuilder = new NodeBuilder()
        def attributes = [plugin:'conditional-buildstep@1.2.2']
        def buildConditionalStepSingleNode = nodeBuilder.'org.jenkinsci.plugins.conditionalbuildstep.singlestep.SingleConditionalBuilder'(attributes)

        def nodeBuilderCondition = new NodeBuilder()
        def conditionAttributes = [class:'org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition', plugin:'run-condition@0.10']
        def conditionNode = nodeBuilderCondition.'condition'(conditionAttributes) {
            arg1 firstArgument?:''
            arg2 secondArgument?:''
            ignoreCase ignoreCaseArgument?:'false'
        }

        buildConditionalStepSingleNode.append(conditionNode)
        
        if (conditionSuccess == 'shell') {
            def nodeBuilderSuccess = new NodeBuilder()
            def successAttributes = [class:'hudson.tasks.Shell']
            def successNode = nodeBuilderSuccess.'buildStep'(successAttributes) {
                command conditionSuccessArguments[0]
            }

            buildConditionalStepSingleNode.append(successNode)
        }
        
        if (conditionFailure == 'Fail') {
            def nodeBuilderFailure = new NodeBuilder()
            def failureAttributes = [class:'org.jenkins_ci.plugins.run_condition.BuildStepRunner$Fail', 'plugin':'run-condition@0.10']
            def failureNode = nodeBuilderFailure.'runner'(failureAttributes)
            buildConditionalStepSingleNode.append(failureNode)
        }

        stepNodes << buildConditionalStepSingleNode

    }
}
