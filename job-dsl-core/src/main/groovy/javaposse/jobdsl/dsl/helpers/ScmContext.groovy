package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.scm.ClearCaseContext
import javaposse.jobdsl.dsl.helpers.scm.GitContext
import javaposse.jobdsl.dsl.helpers.scm.HgContext
import javaposse.jobdsl.dsl.helpers.scm.P4Context
import javaposse.jobdsl.dsl.helpers.scm.PerforcePasswordEncryptor
import javaposse.jobdsl.dsl.helpers.scm.RTCContext
import javaposse.jobdsl.dsl.helpers.scm.SvnContext

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext
import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNull
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty
import static javaposse.jobdsl.dsl.Preconditions.checkState
import static javaposse.jobdsl.dsl.helpers.publisher.PublisherContext.validCloneWorkspaceCriteria

@ContextType('hudson.scm.SCM')
class ScmContext extends AbstractExtensibleContext {
    private static final PerforcePasswordEncryptor PERFORCE_ENCRYPTOR = new PerforcePasswordEncryptor()

    final List<Node> scmNodes = []

    ScmContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        scmNodes << ContextHelper.toNamedNode('scm', node)
    }

    /**
     * Adds a Mercurial SCM source.
     *
     * The closure parameter expects a configure block for direct manipulation of the generated XML. The {@code scm}
     * node is passed into the configure block.
     */
    @RequiresPlugin(id = 'mercurial', minimumVersion = '1.50.1')
    void hg(String url, String branch = null, Closure configure = null) {
        hg(url) {
            delegate.branch(branch)
            delegate.configure(configure)
        }
    }

    /**
     * Adds a Mercurial SCM source.
     *
     * @since 1.33
     */
    @RequiresPlugin(id = 'mercurial', minimumVersion = '1.50.1')
    void hg(String url, @DslContext(HgContext) Closure hgClosure) {
        HgContext hgContext = new HgContext(jobManagement)
        executeInContext(hgClosure, hgContext)

        checkNotNullOrEmpty(url, 'url must be specified')
        checkArgument(!(hgContext.tag && hgContext.branch), 'either tag or branch should be used, not both')

        Node scmNode = new NodeBuilder().scm(class: 'hudson.plugins.mercurial.MercurialSCM') {
            source(url)
            modules(hgContext.modules.join(' '))
            revisionType(hgContext.tag ? 'TAG' : 'BRANCH')
            revision(hgContext.tag ?: hgContext.branch ?: 'default')
            clean(hgContext.clean)
            credentialsId(hgContext.credentialsId ?: '')
            disableChangeLog(hgContext.disableChangeLog)
        }
        if (hgContext.installation) {
            scmNode.appendNode('installation', hgContext.installation)
        }
        if (hgContext.subdirectory) {
            scmNode.appendNode('subdir', hgContext.subdirectory)
        }
        ContextHelper.executeConfigureBlock(scmNode, hgContext.configureBlock)
        scmNodes << scmNode
    }

    /**
     * Adds a Git SCM source.
     *
     * @since 1.20
     */
    @RequiresPlugin(id = 'git', minimumVersion = '2.5.3')
    void git(@DslContext(GitContext) Closure gitClosure) {
        GitContext gitContext = new GitContext(jobManagement, item)
        executeInContext(gitClosure, gitContext)

        if (gitContext.branches.empty) {
            gitContext.branches << '**'
        }

        Node gitNode = new NodeBuilder().scm(class: 'hudson.plugins.git.GitSCM') {
            userRemoteConfigs(gitContext.remoteConfigs)
            branches {
                gitContext.branches.each { String branch ->
                    'hudson.plugins.git.BranchSpec' {
                        name(branch)
                    }
                }
            }
            configVersion(2)
            doGenerateSubmoduleConfigurations(false)
            gitTool('Default')
            if (gitContext.extensionContext.extensions) {
                extensions(gitContext.extensionContext.extensions)
            }
        }

        if (gitContext.gitBrowserContext.browser) {
            gitNode.children().add(gitContext.gitBrowserContext.browser)
        }

        ContextHelper.executeConfigureBlock(gitNode, gitContext.configureBlock)

        scmNodes << gitNode
    }

    /**
     * Adds a Git SCM source. The "Create a tag for every build" option will be enabled by default.
     *
     * The closure parameter expects a configure block for direct manipulation of the generated XML. The {@code scm}
     * node is passed into the configure block.
     */
    @RequiresPlugin(id = 'git', minimumVersion = '2.5.3')
    void git(String url, Closure configure = null) {
        git(url, null, configure)
    }

    /**
     * Adds a Git SCM source. The "Create a tag for every build" option will be enabled by default.
     *
     * The closure parameter expects a configure block for direct manipulation of the generated XML. The {@code scm}
     * node is passed into the configure block.
     */
    @RequiresPlugin(id = 'git', minimumVersion = '2.5.3')
    void git(String url, String branch, Closure configure = null) {
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
            extensions {
                perBuildTag()
            }
        }
    }

    /**
     * Adds a Git SCM source for a GitHub repository.
     *
     * @since 1.15
     * @see #github(java.lang.String, java.lang.String, java.lang.String, java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'git', minimumVersion = '2.5.3')
    void github(String ownerAndProject, String branch = null, String protocol = 'https', Closure closure) {
        github(ownerAndProject, branch, protocol, 'github.com', closure)
    }

    /**
     * Adds a Git SCM source for a GitHub repository.
     *
     * The Git URL will be derived from the {@code ownerAndProject}, {@code protocol} and {@code host} parameters.
     * Supported protocols are {@code 'https'}, {@code 'ssh'} and {@code 'git'}.
     *
     * This will also configure the source browser to point to GitHub and set the GitHub project URL.
     *
     * The closure parameter expects a configure block for direct manipulation of the generated XML. The {@code scm}
     * node is passed into the configure block.
     *
     * @since 1.15
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    @RequiresPlugin(id = 'git', minimumVersion = '2.5.3')
    void github(String ownerAndProject, String branch = null, String protocol = 'https', String host = 'github.com',
                Closure closure = null) {
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
     * Adds a Subversion SCM source.
     *
     * The closure parameter expects a configure block for direct manipulation of the generated XML. The {@code scm}
     * node is passed into the configure block.
     */
    @RequiresPlugin(id = 'subversion', minimumVersion = '2.1')
    void svn(String svnUrl, Closure configure = null) {
        svn(svnUrl, '.', configure)
    }

    /**
     * Adds a Subversion SCM source.
     *
     * The closure parameter expects a configure block for direct manipulation of the generated XML. The {@code scm}
     * node is passed into the configure block.
     */
    @RequiresPlugin(id = 'subversion', minimumVersion = '2.1')
    void svn(String svnUrl, String localDir, Closure configure = null) {
        checkNotNull(svnUrl, 'svnUrl must not be null')
        checkNotNull(localDir, 'localDir must not be null')

        svn {
            location(svnUrl) {
                directory(localDir)
            }
            delegate.configure(configure)
        }
    }

    /**
     * Adds a Subversion SCM source.
     *
     * @since 1.30
     */
    @RequiresPlugin(id = 'subversion', minimumVersion = '2.1')
    void svn(@DslContext(SvnContext) Closure svnClosure) {
        SvnContext svnContext = new SvnContext(jobManagement)
        executeInContext(svnClosure, svnContext)

        checkState(!svnContext.locations.empty, 'One or more locations must be specified')

        Node svnNode = new NodeBuilder().scm(class: 'hudson.scm.SubversionSCM') {
            locations(svnContext.locations)
            workspaceUpdater(class: svnContext.checkoutStrategy.className)
            excludedRegions(svnContext.excludedRegions.join('\n'))
            includedRegions(svnContext.includedRegions.join('\n'))
            excludedUsers(svnContext.excludedUsers.join('\n'))
            excludedCommitMessages(svnContext.excludedCommitMessages.join('\n'))
            excludedRevprop(svnContext.excludedRevisionProperty ?: '')
        }

        ContextHelper.executeConfigureBlock(svnNode, svnContext.configureBlock)

        scmNodes << svnNode
    }

    /**
     * Add Perforce SCM source.
     *
     * @see #p4(java.lang.String, java.lang.String, java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'perforce')
    void p4(String viewspec, String user, Closure configure = null) {
        p4(viewspec, user, '', configure)
    }

    /**
     * Add Perforce SCM source.
     *
     * The client name will be set to {@code builds-${JOB_NAME}}.
     *
     * For security reasons, do not use a hard-coded password. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     *
     * The closure parameter expects a configure block for direct manipulation of the generated XML. The {@code scm}
     * node is passed into the configure block.
     *
     * The configure block must be used to set additional options.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    @RequiresPlugin(id = 'perforce')
    void p4(String viewspec, String user, String password, Closure configureBlock = null) {
        checkNotNull(viewspec, 'viewspec must not be null')

        Node p4Node = new NodeBuilder().scm(class: 'hudson.plugins.perforce.PerforceSCM') {
            p4User user
            p4Passwd PERFORCE_ENCRYPTOR.isEncrypted(password) ? password : PERFORCE_ENCRYPTOR.encrypt(password)
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

        ContextHelper.executeConfigureBlock(p4Node, configureBlock)

        scmNodes << p4Node
    }

    /**
     * Add Perforce SCM source using Perforce p4 plugin.
     *
     * This must use Jenkins credentials.
     *
     * The configure block must be used to set additional options.
     *
     * @since 1.43
     */
    @RequiresPlugin(id = 'p4', minimumVersion = '1.3.5')
    void perforceP4(String credentials, @DslContext(P4Context) Closure p4Closure) {
        checkNotNull(credentials, 'credentials must not be null')

        P4Context p4Context = new P4Context()
        executeInContext(p4Closure, p4Context)

        Node p4Node = new NodeBuilder().scm(class: 'org.jenkinsci.plugins.p4.PerforceScm') {
            credential(credentials)
            populate(class: 'org.jenkinsci.plugins.p4.populate.AutoCleanImpl') {
                have(true)
                force(false)
                modtime(false)
                quiet(true)
                pin()
                replace(true)
                delete(true)
            }
        }
        p4Node.children().add(p4Context.workspaceContext.workspaceConfig)

        ContextHelper.executeConfigureBlock(p4Node, p4Context.configureBlock)

        scmNodes << p4Node
    }

    /**
     * Add a SCM source which copies the workspace of another project.
     *
     * Valid criteria are {@code 'Any'}, {@code 'Not Failed'} and {@code 'Successful'}.
     *
     * @since 1.16
     */
    @RequiresPlugin(id = 'clone-workspace-scm')
    void cloneWorkspace(String parentProject, String criteria = 'Any') {
        checkNotNull(parentProject, 'parentProject must not be null')
        checkArgument(
                validCloneWorkspaceCriteria.contains(criteria),
                "Clone Workspace Criteria needs to be one of these values: ${validCloneWorkspaceCriteria.join(',')}"
        )

        scmNodes << new NodeBuilder().scm(class: 'hudson.plugins.cloneworkspace.CloneWorkspaceSCM') {
            parentJobName(parentProject)
            delegate.criteria(criteria)
        }
    }

    /**
     * Adds a ClearCase SCM source.
     *
     * @since 1.24
     */
    @RequiresPlugin(id = 'clearcase')
    void baseClearCase(@DslContext(ClearCaseContext) Closure closure = null) {
        ClearCaseContext context = new ClearCaseContext()
        executeInContext(closure, context)

        scmNodes << new NodeBuilder().scm(class: 'hudson.plugins.clearcase.ClearCaseSCM') {
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

    /**
     * Adds a Team Concert SCM source.
     *
     * @since 1.28
     */
    @RequiresPlugin(id = 'teamconcert')
    void rtc(@DslContext(RTCContext) Closure closure) {
        RTCContext context = new RTCContext(jobManagement)
        executeInContext(closure, context)

        checkArgument(context.buildType != null, 'Either buildDefinition or buildWorkspace must be specified')

        scmNodes << new NodeBuilder().scm(class: 'com.ibm.team.build.internal.hjplugin.RTCScm') {
            overrideGlobal(context.overrideGlobal)
            timeout(context.timeout)
            if (context.overrideGlobal) {
                buildTool(context.buildTool)
                serverURI(context.serverURI)
                credentialsId(context.credentialsId)
            }
            buildType(context.buildType)
            if (context.buildType == 'buildDefinition') {
                buildDefinition(context.buildDefinition)
            } else {
                buildWorkspace(context.buildWorkspace)
            }
            avoidUsingToolkit(false)
        }
    }
}
