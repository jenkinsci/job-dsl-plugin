package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.WithXmlAction

import static javaposse.jobdsl.dsl.JobParent.maven

class StepContextHelper extends AbstractContextHelper<StepContext> {

    Map<String, Object> jobArguments

    StepContextHelper(List<WithXmlAction> withXmlActions, Map<String, Object> jobArguments=[:]) {
        super(withXmlActions)
        this.jobArguments = jobArguments
    }

    static class StepContext implements Context {
        List<Node> stepNodes = []

        StepContext() {
        }

        StepContext(List<Node> stepNodes) {
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
            propertiesList += antContext.properties

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
            def properties = []
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
                properties << "${key}=${value}"
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
                pom pomArg?:''
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
           <name>Grails 2.0.3</name>
           <grailsWorkDir/>
           <projectWorkDir/>
           <projectBaseDir/>
           <serverPort/>
           <properties/>
           <forceUpgrade>false</forceUpgrade>
           <nonInteractive>true</nonInteractive>
         </com.g2one.hudson.grails.GrailsBuilder>
         */
//        def grails() {
//
//        }

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
    }

    def steps(Closure closure) {
        Preconditions.checkState(jobArguments['type'] != maven, "steps cannot be applied for Maven jobs")
        execute(closure, new StepContext())
    }

    Closure generateWithXmlClosure(StepContext context) {
        return { Node project ->
            def buildersNode
            if (project.builders.isEmpty()) {
                buildersNode = project.appendNode('builders')
            } else {
                buildersNode = project.builders[0]
            }
            context.stepNodes.each {
                buildersNode << it
            }
        }
    }
}
