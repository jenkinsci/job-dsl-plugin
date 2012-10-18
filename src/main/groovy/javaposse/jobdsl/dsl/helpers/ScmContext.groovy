package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction
import com.google.common.base.Preconditions
import groovy.transform.PackageScope

/**
 * Created with IntelliJ IDEA.
 * User: jryan
 * Date: 10/16/12
 * Time: 10:24 AM
 * To change this template use File | Settings | File Templates.
 */
@PackageScope
class ScmContext implements Context {
    boolean multiEnabled
    List<Node> scmNodes = []

    ScmContext(multiEnabled = false) {
        this.multiEnabled = multiEnabled
    }

    // Package scope
    ScmContext(Node singleNode, multiEnabled = false) {
        this.multiEnabled = multiEnabled
        scmNodes << singleNode // Safe since this is the constructor
    }

    /**
     * Helper method for dealing with a single scm node
     */
    def getScmNode() {
        return scmNodes[0]
    }

    private validateMulti(){
        Preconditions.checkState(scmNodes.size() < (multiEnabled?10:1), 'Outside "multiscm", only one SCM can be specified'+multiEnabled+scmNodes.empty+scmNodes)
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
        validateMulti()
        Preconditions.checkNotNull(url)
        // TODO Validate url as a Mercurial url (e.g. http, https or ssh)

        // TODO Attempt to update existing scm node
        def nodeBuilder = new NodeBuilder()

        Node scmNode = nodeBuilder.scm(class:'hudson.plugins.mercurial.MercurialSCM') {
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

        scmNodes << scmNode
    }

    /**
     <hudson.plugins.git.GitSCM>
       <configVersion>2</configVersion>
       <userRemoteConfigs>
         <hudson.plugins.git.UserRemoteConfig>
           <name/>
           <refspec/>
           <url>git@github.com:jenkinsci/job-dsl-plugin.git</url>
         </hudson.plugins.git.UserRemoteConfig>
       </userRemoteConfigs>
       <branches>
         <hudson.plugins.git.BranchSpec>
           <name>**</name>
         </hudson.plugins.git.BranchSpec>
       </branches>
       <disableSubmodules>false</disableSubmodules>
       <recursiveSubmodules>false</recursiveSubmodules>
       <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
       <authorOrCommitter>false</authorOrCommitter>
       <clean>false</clean>
       <wipeOutWorkspace>false</wipeOutWorkspace>
       <pruneBranches>false</pruneBranches>
       <remotePoll>false</remotePoll>
       <ignoreNotifyCommit>false</ignoreNotifyCommit>
       <buildChooser class="hudson.plugins.git.util.DefaultBuildChooser"/>
       <gitTool>Default</gitTool>
       <submoduleCfg class="list"/>
       <relativeTargetDir/>
       <reference/>
       <excludedRegions/>
       <excludedUsers/>
       <gitConfigName/>
       <gitConfigEmail/>
       <skipTag>false</skipTag>
       <includedRegions/>
       <scmName/>
     </hudson.plugins.git.GitSCM>
     * @param url
     * @param branch
     * @param configure
     * @return
     */
    def git(String url, String branch = null, Closure configure = null) {
        Preconditions.checkNotNull(url)
        validateMulti()
        // TODO Validate url as a git url (e.g. https or git)

        // TODO Attempt to update existing scm node
        def nodeBuilder = new NodeBuilder()

        Node gitNode = nodeBuilder.scm(class:'hudson.plugins.git.GitSCM') {
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
            configVersion '2'
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

        gitNode.appendNode('userRemoteConfigs').appendNode('hudson.plugins.git.UserRemoteConfig').appendNode('url', url)
        gitNode.appendNode('branches').appendNode('hudson.plugins.git.BranchSpec').appendNode('name', branch?:'**')

        // Apply Context
        if (configure) {
            WithXmlAction action = new WithXmlAction(configure)
            action.execute(gitNode)
        }
        scmNodes << gitNode
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
