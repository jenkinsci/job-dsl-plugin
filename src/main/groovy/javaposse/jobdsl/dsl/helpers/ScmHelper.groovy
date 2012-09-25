package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction
import com.google.common.base.Preconditions

/**
 scm {
 git(String url, Closure configure) // configure is optional
 perforce(Closure configure)
 subverison(Closure configure)
 }

 git {
 String url
 String branch
 // TODO Pull all fields from GitSCM
 }

 subversion {
 String url
 // Strategy
 // TODO Pull all fields from SubversionSCM
 }
 */
class ScmHelper extends AbstractHelper<ScmContext> {

    ScmHelper(List<WithXmlAction> withXmlActions) {
        this.withXmlActions = withXmlActions
    }

    static class ScmContext implements Context {
        // Orphan <scm> Node that can be attached to project in the withXmlAction
        Node scmNode

        ScmContext() {
        }

        ScmContext(Node scmNode) {
            this.scmNode = scmNode
        }

        /**
         * Generate configuration for Mercurial.
         *
         <scm class="hudson.plugins.mercurial.MercurialSCM">
            <source>http://selenic.com/repo/hello</source>
            <modules>sample-module1 sample-module2</modules>
            <subdir>path-to-check-out-into</subdir>
            <clean>true</clean>
            <browser class="hudson.plugins.mercurial.browser.HgWeb">
              <url>http://selenic.com/repo/hello/</url>
            </browser>
          </scm>
         */
        def hg(String url, String branch = null, Closure configure = null) {
            Preconditions.checkNotNull(url)
            // TODO Validate url as a Mercurial url (e.g. http, https or ssh)

            if (scmNode != null) {
                throw new RuntimeException('Multiple calls scm')
            }

            // TODO Attempt to update existing scm node
            def nodeBuilder = new NodeBuilder()

            scmNode = nodeBuilder.scm(class:'hudson.plugins.mercurial.MercurialSCM') {
                source url
                modules ''
                clean false
            }
            scmNode.appendNode('branch', branch?:'')

            // Apply Context
            if (configure) {
                WithXmlAction action = new WithXmlAction(configure)
                action.execute(scmNode)
            }
        }

        /**
         *
         * @param url
         * @param branch
         * @param configure
         * @return
         */
        def git(String url, String branch = null, Closure configure = null) {
            Preconditions.checkNotNull(url)
            // TODO Validate url as a git url (e.g. https or git)

            if (scmNode != null) {
                throw new RuntimeException('Multiple calls scm')
            }

            // TODO Attempt to update existing scm node
            def nodeBuilder = new NodeBuilder()

            scmNode = nodeBuilder.scm(class:'hudson.plugins.git.GitSCM') {
//                userRemoteConfigs {
//                    'hudson.plugins.git.UserRemoteConfig' {
//                        'url' 'url'
//                    }
//                }
                // Can't put here because the name is "name"
//                branches {
//                    'hudson.plugins.git.BranchSpec' {
//                        'name' branch?:'master'
//                    }
//                }
                disableSubmodules 'false'
                recursiveSubmodules 'false'
                doGenerateSubmoduleConfigurations 'false'
                authorOrCommitter 'false'
                clean 'false'
                wipeOutWorkspace 'false'
                pruneBranches 'false'
                remotePoll 'false'
                ignoreNotifyCommit 'false'
                //buildChooser class="hudson.plugins.git.util.DefaultBuildChooser"
                gitTool 'Default'
                //submoduleCfg 'class="list"'
                //relativeTargetDir
                //reference
                //excludedRegions
                //excludedUsers
                //gitConfigName
                //gitConfigEmail
                skipTag 'false'
                //includedRegions
                //scmName
            }

            scmNode.appendNode('userRemoteConfigs').appendNode('hudson.plugins.git.UserRemoteConfig').appendNode('url', url)
            scmNode.appendNode('branches').appendNode('hudson.plugins.git.BranchSpec').appendNode('name', branch?:'master')

            // Apply Context
            if (configure) {
                WithXmlAction action = new WithXmlAction(configure)
                action.execute(scmNode)
            }
        }

        /**
         <scm class="hudson.scm.SubversionSCM">
           <locations>
             <hudson.scm.SubversionSCM_-ModuleLocation>
               <remote>http://svn/repo</remote>
               <local>.</local>
             </hudson.scm.SubversionSCM_-ModuleLocation>
           </locations>
           <excludedRegions/>
           <includedRegions/>
           <excludedUsers/>
           <excludedRevprop/>
           <excludedCommitMessages/>
           <workspaceUpdater class="hudson.scm.subversion.UpdateUpdater"/>
         </scm>
         */
//        def svn() {
//
//        }
        /**
         <scm class="hudson.plugins.perforce.PerforceSCM">
           <p4User>rolem</p4User>
           <p4Passwd></p4Passwd>
           <p4Port>perforce:1666</p4Port>
           <p4Client>builds-workspace</p4Client>
           <projectPath>//depot/webapplication/...
           //depot/Tools/build/...</projectPath>
           <projectOptions>noallwrite clobber nocompress unlocked nomodtime rmdir</projectOptions>
           <p4Exe>p4</p4Exe>
           <p4SysDrive>C:</p4SysDrive>
           <p4SysRoot>C:\WINDOWS</p4SysRoot>
           <useClientSpec>false</useClientSpec>
           <forceSync>false</forceSync>
           <alwaysForceSync>false</alwaysForceSync>
           <dontUpdateServer>false</dontUpdateServer>
           <disableAutoSync>false</disableAutoSync>
           <disableSyncOnly>false</disableSyncOnly>
           <useOldClientName>false</useOldClientName>
           <updateView>true</updateView>
           <dontRenameClient>false</dontRenameClient>
           <updateCounterValue>false</updateCounterValue>
           <dontUpdateClient>false</dontUpdateClient>
           <exposeP4Passwd>false</exposeP4Passwd>
           <wipeBeforeBuild>true</wipeBeforeBuild>
           <wipeRepoBeforeBuild>false</wipeRepoBeforeBuild>
           <firstChange>-1</firstChange>
           <slaveClientNameFormat>${basename}-${nodename}</slaveClientNameFormat>
           <lineEndValue></lineEndValue>
           <useViewMask>false</useViewMask>
           <useViewMaskForPolling>false</useViewMaskForPolling>
           <useViewMaskForSyncing>false</useViewMaskForSyncing>
           <pollOnlyOnMaster>true</pollOnlyOnMaster>
         </scm>
         */
//        def perforce() {
//
//        }
    }

    def scm(Closure closure) {
        execute(closure, new ScmContext())
    }

    Closure generateWithXmlClosure(ScmContext context) {
        return { Node project ->
            def scm = project/scm // TODO This will create it if it doesn't exist, seems like we wouldn't need to do this, but dealing with NodeList is a pain
            if (scm) {
                // There can only be only one SCM, so remove if there
                project.remove(scm)
            }

            // Assuming append the only child
            project << context.scmNode
        }
    }
}