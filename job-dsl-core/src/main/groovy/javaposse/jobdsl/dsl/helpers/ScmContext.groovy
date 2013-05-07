package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import groovy.transform.PackageScope
import javaposse.jobdsl.dsl.WithXmlAction
import hudson.plugins.perforce.PerforcePasswordEncryptor

class ScmContext implements Context {
    boolean multiEnabled
    List<Node> scmNodes = []
    List<WithXmlAction> withXmlActions = []

    ScmContext(multiEnabled = false, withXmlActions = []) {
        this.multiEnabled = multiEnabled
        this.withXmlActions = withXmlActions
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
        Preconditions.checkState(scmNodes.size() < (multiEnabled?10:1), 'Outside "multiscm", only one SCM can be specified')
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

    def github(String ownerAndProject, String branch = null, String protocol = "https", Closure closure) {
        github(ownerAndProject, branch, protocol, "github.com", closure)
    }

    def github(String ownerAndProject, String branch = null, String protocol = "https", String host = "github.com", Closure closure = null) {
        def url
        def webUrl = "https://${host}/${ownerAndProject}/"

        switch (protocol) {
            case 'https':
                url = "https://${host}/${ownerAndProject}.git"
                break
            case 'ssh':
                url = "git@${host}:${ownerAndProject}.git"
                break
            case 'git':
                url = "git://${host}/${ownerAndProject}.git"
                break
            default:
                throw new IllegalArgumentException("Invalid protocol ${protocol}. Only https, ssh or git are allowed.")
        }
        git(url, branch, closure)
        scmNodes.last().appendNode('browser', [class: 'hudson.plugins.git.browser.GithubWeb']).appendNode('url', webUrl)
        withXmlActions << new WithXmlAction({ project ->
            project / 'properties' / 'com.coravy.hudson.plugins.github.GithubProjectProperty' {
                projectUrl webUrl
            }
        })
    }

    /**
     <scm class="hudson.scm.SubversionSCM">
       <locations>
         <hudson.scm.SubversionSCM_-ModuleLocation>
           <remote>http://svn/repo</remote>
           <local>.</local>
         </hudson.scm.SubversionSCM_-ModuleLocation>
       </locations>
       <browser class="hudson.scm.browsers.ViewSVN">
         <url>http://mycompany.com/viewvn/repo_name</url>
       </browser>
       OR
       <browser class="hudson.scm.browsers.FishEyeSVN">
         <url>http://mycompany.com/viewvn/repo_name</url>
         <rootModule>my_root_module</rootModule>
       </browser>
       <excludedRegions/>
       <includedRegions/>
       <excludedUsers/>
       <excludedRevprop/>
       <excludedCommitMessages/>
       <workspaceUpdater class="hudson.scm.subversion.UpdateUpdater"/>
     </scm>
     */
    def svn(String svnUrl, Closure configure = null) {
        svn(svnUrl, '.', configure)
    }
    def svn(String svnUrl, String localDir, Closure configure = null) {
        Preconditions.checkNotNull(svnUrl)
        Preconditions.checkNotNull(localDir)
        validateMulti()
        // TODO Validate url as a svn url (e.g. https or http)

        // TODO Attempt to update existing scm node
        def nodeBuilder = new NodeBuilder()

        Node svnNode = nodeBuilder.scm(class:'hudson.scm.SubversionSCM') {
            locations {
                'hudson.scm.SubversionSCM_-ModuleLocation' {
                    remote "${svnUrl}"
                    local "${localDir}"
                }
            }

            excludedRegions ''
            includedRegions ''
            excludedUsers ''
            excludedRevprop ''
            excludedCommitMessages ''
            workspaceUpdater(class:'hudson.scm.subversion.UpdateUpdater')
        }

        // Apply Context
        if (configure) {
            WithXmlAction action = new WithXmlAction(configure)
            action.execute(svnNode)
        }
        scmNodes << svnNode

    }

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
    def p4(String viewspec, Closure configure = null) {
        return p4(viewspec, 'rolem', '', configure)
    }
    def p4(String viewspec, String user, Closure configure = null) {
        return p4(viewspec, user, '', configure)
    }
    def p4(String viewspec, String user, String password, Closure configure = null) {
            Preconditions.checkNotNull(viewspec)
        validateMulti()
        // TODO Validate viewspec as valid viewspec

        // TODO Attempt to update existing scm node
        def nodeBuilder = new NodeBuilder()

        PerforcePasswordEncryptor encryptor = new PerforcePasswordEncryptor();
        String cleanPassword = encryptor.appearsToBeAnEncryptedPassword(password)?password:encryptor.encryptString(password)

        Node p4Node = nodeBuilder.scm(class:'hudson.plugins.perforce.PerforceSCM') {
            p4User user
            p4Passwd cleanPassword
            p4Port 'perforce:1666'
            p4Client 'builds-${JOB_NAME}'
            projectPath "${viewspec}"
            projectOptions 'noallwrite clobber nocompress unlocked nomodtime rmdir'
            p4Tool 'p4'
            p4SysDrive 'C:'
            p4SysRoot 'C:\\WINDOWS'
            useClientSpec 'false'
            forceSync 'false'
            alwaysForceSync 'false'
            dontUpdateServer 'false'
            disableAutoSync 'false'
            disableSyncOnly 'false'
            useOldClientName 'false'
            updateView 'true'
            dontRenameClient 'false'
            updateCounterValue 'false'
            dontUpdateClient 'false'
            exposeP4Passwd 'false'
            wipeBeforeBuild 'true'
            wipeRepoBeforeBuild 'false'
            firstChange '-1'
            slaveClientNameFormat '${basename}-${nodename}'
            lineEndValue ''
            useViewMask 'false'
            useViewMaskForPolling 'false'
            useViewMaskForSyncing 'false'
            pollOnlyOnMaster 'true'
        }

        // Apply Context
        if (configure) {
            WithXmlAction action = new WithXmlAction(configure)
            action.execute(p4Node)
        }
        scmNodes << p4Node
    }
}
