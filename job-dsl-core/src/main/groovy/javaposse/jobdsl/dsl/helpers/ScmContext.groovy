package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import hudson.plugins.perforce.PerforcePasswordEncryptor
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.scm.ClearCaseContext
import javaposse.jobdsl.dsl.helpers.scm.GitContext

import static javaposse.jobdsl.dsl.helpers.AbstractContextHelper.executeInContext
import static javaposse.jobdsl.dsl.helpers.publisher.PublisherContext.validCloneWorkspaceCriteria

class ScmContext implements Context {
    boolean multiEnabled
    List<Node> scmNodes = []
    List<WithXmlAction> withXmlActions = []
    JobManagement jobManagement

    ScmContext(multiEnabled = false, withXmlActions = [], jobManagement = null) {
        this.multiEnabled = multiEnabled
        this.withXmlActions = withXmlActions
        this.jobManagement = jobManagement
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
        Preconditions.checkState(multiEnabled || scmNodes.size() < 1, 'Outside "multiscm", only one SCM can be specified')
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

        def nodeBuilder = new NodeBuilder()

        Node scmNode = nodeBuilder.scm(class: 'hudson.plugins.mercurial.MercurialSCM') {
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
       <useShallowClone>false</useShallowClone>
       <includedRegions/>
       <scmName/>
     </hudson.plugins.git.GitSCM>
     */
    def git(Closure gitClosure) {
        validateMulti()

        GitContext gitContext = new GitContext(withXmlActions, jobManagement)
        executeInContext(gitClosure, gitContext)

        if (gitContext.branches.empty) {
            gitContext.branches << '**'
        }

        def nodeBuilder = new NodeBuilder()

        Node gitNode = nodeBuilder.scm(class: 'hudson.plugins.git.GitSCM') {
            userRemoteConfigs(gitContext.remoteConfigs)
            branches {
                gitContext.branches.each { String branch ->
                    'hudson.plugins.git.BranchSpec' {
                        name(branch)
                    }
                }
            }
            configVersion '2'
            disableSubmodules 'false'
            recursiveSubmodules 'false'
            doGenerateSubmoduleConfigurations 'false'
            authorOrCommitter 'false'
            clean gitContext.clean
            wipeOutWorkspace gitContext.wipeOutWorkspace
            pruneBranches 'false'
            remotePoll gitContext.remotePoll
            ignoreNotifyCommit 'false'
            gitTool 'Default'
            if (gitContext.relativeTargetDir) {
                relativeTargetDir gitContext.relativeTargetDir
            }
            if (gitContext.reference) {
                reference gitContext.reference
            }
            skipTag !gitContext.createTag
            if (gitContext.shallowClone) {
                useShallowClone gitContext.shallowClone
            }
        }

        if (gitContext.browser) {
            gitNode.children().add(gitContext.browser)
        }

        if (gitContext.mergeOptions) {
            gitNode.children().add(gitContext.mergeOptions)
        }

        // Apply Context
        if (gitContext.withXmlClosure) {
            WithXmlAction action = new WithXmlAction(gitContext.withXmlClosure)
            action.execute(gitNode)
        }
        scmNodes << gitNode
    }

    /**
     * @param url
     * @param branch
     * @param configure
     * @return
     */
    def git(String url, Closure configure = null) {
        git(url, null, configure)
    }

    def git(String url, String branch, Closure configure = null) {
        git {
            remote {
                delegate.url(url)
            }
            if (branch) {
                delegate.branch(branch)
            }
            if (configure) {
                delegate.configure(configure)
            }
            delegate.createTag()
        }
    }

    def github(String ownerAndProject, String branch = null, String protocol = "https", Closure closure) {
        github(ownerAndProject, branch, protocol, "github.com", closure)
    }

    def github(String ownerAndProject, String branch = null, String protocol = "https", String host = "github.com", Closure closure = null) {
        git {
            remote {
                delegate.github(ownerAndProject, protocol, host)
            }
            if (branch) {
                delegate.branch(branch)
            }
            if (closure) {
                delegate.configure(closure)
            }
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

        def nodeBuilder = new NodeBuilder()

        Node svnNode = nodeBuilder.scm(class: 'hudson.scm.SubversionSCM') {
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
            workspaceUpdater(class: 'hudson.scm.subversion.UpdateUpdater')
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

        def nodeBuilder = new NodeBuilder()

        PerforcePasswordEncryptor encryptor = new PerforcePasswordEncryptor();
        String cleanPassword = encryptor.appearsToBeAnEncryptedPassword(password)?password:encryptor.encryptString(password)

        Node p4Node = nodeBuilder.scm(class: 'hudson.plugins.perforce.PerforceSCM') {
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

    /**
     * <scm class="hudson.plugins.cloneworkspace.CloneWorkspaceSCM">
     *   <parentJobName>test-job</parentJobName>
     *   <criteria>Successful</criteria>
     * </scm>
     */
    def cloneWorkspace(String parentProject, String criteriaArg = 'Any') {
        Preconditions.checkNotNull(parentProject)
        Preconditions.checkArgument(
                validCloneWorkspaceCriteria.contains(criteriaArg),
                "Clone Workspace Criteria needs to be one of these values: ${validCloneWorkspaceCriteria.join(',')}")
        validateMulti()

        scmNodes << NodeBuilder.newInstance().scm(class: 'hudson.plugins.cloneworkspace.CloneWorkspaceSCM') {
            parentJobName(parentProject)
            criteria(criteriaArg)
        }
    }

    /**
     * <scm class="hudson.plugins.clearcase.ClearCaseSCM">
     *     <changeset>BRANCH</changeset>
     *     <createDynView>false</createDynView>
     *     <excludedRegions/>
     *     <extractLoadRules>false</extractLoadRules>
     *     <filteringOutDestroySubBranchEvent>false</filteringOutDestroySubBranchEvent>
     *     <freezeCode>false</freezeCode>
     *     <loadRules/>
     *     <loadRulesForPolling/>
     *     <mkviewOptionalParam/>
     *     <multiSitePollBuffer>0</multiSitePollBuffer>
     *     <recreateView>false</recreateView>
     *     <removeViewOnRename>false</removeViewOnRename>
     *     <useDynamicView>false</useDynamicView>
     *     <useOtherLoadRulesForPolling>false</useOtherLoadRulesForPolling>
     *     <useUpdate>true</useUpdate>
     *     <viewDrive>/view</viewDrive>
     *     <viewName>
     *     Jenkins_${USER_NAME}_${NODE_NAME}_${JOB_NAME}${DASH_WORKSPACE_NUMBER}
     *     </viewName>
     *     <viewPath>view</viewPath>
     *     <branch/>
     *     <configSpec/>
     *     <configSpecFileName/>
     *     <doNotUpdateConfigSpec>false</doNotUpdateConfigSpec>
     *     <extractConfigSpec>false</extractConfigSpec>
     *     <label/>
     *     <refreshConfigSpec>false</refreshConfigSpec>
     *     <refreshConfigSpecCommand/>
     *     <useTimeRule>false</useTimeRule>
     * </scm>
     *
     * See http://wiki.jenkins-ci.org/display/JENKINS/ClearCase+Plugin
     */
    def baseClearCase(Closure closure = null) {
        validateMulti()

        ClearCaseContext context = new ClearCaseContext()
        executeInContext(closure, context)

        scmNodes << NodeBuilder.newInstance().scm(class: 'hudson.plugins.clearcase.ClearCaseSCM') {
            changeset('BRANCH')
            createDynView(false)
            excludedRegions('')
            extractLoadRules(false)
            filteringOutDestroySubBranchEvent(false)
            freezeCode(false)
            loadRules(context.loadRules.join('\n'))
            loadRulesForPolling('')
            mkviewOptionalParam(context.mkviewOptionalParameter.join('\n'))
            multiSitePollBuffer(0)
            recreateView(false)
            removeViewOnRename(false)
            useDynamicView(false)
            useOtherLoadRulesForPolling(false)
            useUpdate(true)
            viewDrive('/view')
            viewName(context.viewName)
            viewPath(context.viewPath)
            branch('')
            configSpec(context.configSpec.join('\n'))
            configSpecFileName('')
            doNotUpdateConfigSpec(false)
            extractConfigSpec(false)
            label('')
            refreshConfigSpec(false)
            refreshConfigSpecCommand('')
            useTimeRule(false)
        }
    }
}
