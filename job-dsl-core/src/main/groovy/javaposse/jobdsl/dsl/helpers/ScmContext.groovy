package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import hudson.plugins.perforce.PerforcePasswordEncryptor
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.scm.GitContext

import static javaposse.jobdsl.dsl.helpers.AbstractContextHelper.executeInContext
import static javaposse.jobdsl.dsl.helpers.publisher.PublisherContext.getValidCloneWorkspaceCriteria

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

        // TODO Attempt to update existing scm node
        def nodeBuilder = new NodeBuilder()

        Node gitNode = nodeBuilder.scm(class:'hudson.plugins.git.GitSCM') {
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
            //buildChooser class="hudson.plugins.git.util.DefaultBuildChooser"
            gitTool 'Default'
            //submoduleCfg 'class="list"'
            if (gitContext.relativeTargetDir) {
                relativeTargetDir gitContext.relativeTargetDir
            }
            if (gitContext.reference) {
                reference gitContext.reference
            }
            //excludedRegions
            //excludedUsers
            //gitConfigName
            //gitConfigEmail
            skipTag !gitContext.createTag
            //includedRegions
            //scmName
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
    
    def svn(Closure svnClosure) {
        validateMulti()
        
        SvnContext svnContext = new SvnContext()
        AbstractContextHelper.executeInContext(svnClosure, svnContext)

        Preconditions.checkState(svnContext.locations.size() != 0, 'One or more locations must be specified')

        def nodeBuilder = NodeBuilder.newInstance()
        Node svnNode = nodeBuilder.scm(class:'hudson.scm.SubversionSCM') {
            locations {
                svnContext.locations.each { currLoc ->
                    'hudson.scm.SubversionSCM_-ModuleLocation' {
                        remote currLoc.url
                        local currLoc.local
                    }
                }
            }
            workspaceUpdater(class:svnContext.checkoutstrategy.className)
            excludedRegions svnContext.excludedregions.join("\n")
            includedRegions svnContext.includedregions.join("\n")
            excludedUsers svnContext.excludedusers.join("\n")
            excludedCommitMessages svnContext.excludedcommitmsgs.join("\n")
            excludedRevprop svnContext.excludedrevprop
        }

        scmNodes << svnNode
    }

    def static class SvnContext implements Context {
        def static class Location {
            String url = null
            String local = '.'
        }

        def locations = []
        def checkoutstrategy = CheckoutStrategy.Update
        def excludedregions = []
        def includedregions = []
        def excludedusers = []
        def excludedcommitmsgs = []
        def excludedrevprop = ''

        /*
         * At least one location MUST be specified.
         * Additional locations can be specified by calling location() multiple times.
         *   svnUrl   - What to checkout from SVN.
         *   localDir - Destination directory relative to workspace.
         *              If not specified, defaults to '.'.
         */
        def location(String svnUrl, String localDir = '.') {
            locations << new Location(url:svnUrl, local:localDir)
        }

        /*
         * The checkout strategy that should be used.  This is a global setting for all
         * locations.
         *   strategy - Strategy to use. Possible values:
         *                CheckoutStrategy.Update
         *                CheckoutStrategy.Checkout
         *                CheckoutStrategy.UpdateWithClean
         *                CheckoutStrategy.UpdateWithRevert
         */
        def checkoutStrategy(CheckoutStrategy strategy) {
            checkoutstrategy = strategy
        }

        /*
         * Add an excluded region.  Each call to excludedRegion() adds to the list of
         * excluded regions.
         * If excluded regions are configured, and Jenkins is set to poll for changes,
         * Jenkins will ignore any files and/or folders that match the specified
         * patterns when determining if a build needs to be triggered.
         *   pattern - RegEx
         */
        def excludedRegion(String pattern) {
            excludedregions << pattern
        }

        /*
         * Add a list of excluded regions.  Each call to excludedRegions() adds to the
         * list of excluded regions.
         * If excluded regions are configured, and Jenkins is set to poll for changes,
         * Jenkins will ignore any files and/or folders that match the specified
         * patterns when determining if a build needs to be triggered.
         *   patterns - RegEx
         */
        def excludedRegions(Iterable<String> patterns) {
            patterns.each {
                excludedRegion(it)
            }
        }

        /*
         * Add an included region.  Each call to includedRegion() adds to the list of
         * included regions.
         * If included regions are configured, and Jenkins is set to poll for changes,
         * Jenkins will ignore any files and/or folders that do _not_ match the specified
         * patterns when determining if a build needs to be triggered.
         *   pattern - RegEx
         */
        def includedRegion(String pattern) {
            includedregions << pattern
        }

        /*
         * Add a list of included regions.  Each call to includedRegions() adds to the
         * list of included regions.
         * If included regions are configured, and Jenkins is set to poll for changes,
         * Jenkins will ignore any files and/or folders that do _not_ match the specified
         * patterns when determining if a build needs to be triggered.
         *   patterns - RegEx
         */
        def includedRegions(Iterable<String> patterns) {
            patterns.each {
                includedRegion(it)
            }
        }

        /*
         * Add an excluded user.  Each call to excludedUser() adds to the list of
         * excluded users.
         * If excluded users are configured, and Jenkins is set to poll for changes,
         * Jenkins will ignore any revisions committed by the specified users when
         * determining if a build needs to be triggered.
         *   user - User to ignore when triggering builds
         */
        def excludedUser(String user) {
            excludedusers << user
        }

        /*
         * Add a list of excluded users.  Each call to excludedUsers() adds to the
         * list of excluded users.
         * If excluded users are configured, and Jenkins is set to poll for changes,
         * Jenkins will ignore any revisions committed by the specified users when
         * determining if a build needs to be triggered.
         *   users - Users to ignore when triggering builds
         */
        def excludedUsers(Iterable<String> users) {
            users.each {
                excludedUser(it)
            }
        }

        /*
         * Add an exluded commit message.  Each call to excludedCommitMsg() adds to the list of
         * excluded commit messages.
         * If excluded messages are configured, and Jenkins is set to poll for changes,
         * Jenkins will ignore any revisions with commit messages that match the specified
         * patterns when determining if a build needs to be triggered.
         *   pattern - RegEx
         */
        def excludedCommitMsg(String pattern) {
            excludedcommitmsgs << pattern
        }

        /*
         * Add a list of excluded commit messages.  Each call to excludedCommitMsgs() adds to the
         * list of excluded commit messages.
         * If excluded messages are configured, and Jenkins is set to poll for changes,
         * Jenkins will ignore any revisions with commit messages that match the specified
         * patterns when determining if a build needs to be triggered.
         *   patterns - RegEx
         */
        def excludedCommitMsgs(Iterable<String> patterns) {
            patterns.each {
                excludedCommitMsg(it)
            }
        }

        /*
         * Set an excluded revision property.
         * If an excluded revision property is set, and Jenkins is set to poll for changes,
         * Jenkins will ignore any revisions that are marked with the specified
         * revision property when determining if a build needs to be triggered.
         * This only works in Subversion 1.5 servers or greater.
         *   pattern - RegEx
         */
        def excludedRevProp(String revisionProperty) {
            excludedrevprop = revisionProperty
        }
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
}
