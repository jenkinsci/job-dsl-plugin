package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.WithXmlAction

/**
 step {
 shell(String command)
 groovy(String command)
 ant(String version, String targets, String buildFile, String properties, String javaOptions)
 ant(Closure configure)
 gradle(Closure configure)
 maven(Closure configure)
 }

 ant {
 String version
 String targets
 String buildFile
 String props // Don't want to name properties
 String javaOptions
 }

 gradle {
 boolean useWrapper
 // TODO
 }

 maven {
 String pomLocation
 // TODO
 }
 */
class StepHelper extends AbstractHelper<StepContext> {

    StepHelper(List<WithXmlAction> withXmlActions) {
        super(withXmlActions)
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
           <targets>clean build</targets>
           <antName>Ant 1.8</antName>
           <buildFile>build.xml</buildFile>
         </hudson.tasks.Ant>
         */
//        def ant() {
//
//        }

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
//        def groovy() {
//
//        }

        /**
         <hudson.plugins.groovy.SystemGroovy>
           <scriptSource class="hudson.plugins.groovy.StringScriptSource">
             <command>System Groovy</command>
           </scriptSource>
           <bindings/>
           <classpath/>
         </hudson.plugins.groovy.SystemGroovy>
         */
//        def systemGroovy() {
//
//        }

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
    }

    def steps(Closure closure) {
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