package javaposse.jobdsl.dsl.helpers.scm

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.WithXmlAction

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class GitContext extends AbstractContext {
    private final List<WithXmlAction> withXmlActions

    List<Node> remoteConfigs = []
    List<String> branches = []
    boolean createTag = false
    boolean clean = false
    boolean wipeOutWorkspace = false
    boolean remotePoll = false
    boolean shallowClone = false
    boolean pruneBranches = false
    boolean ignoreNotifyCommit = false
    String localBranch
    String relativeTargetDir
    String reference = ''
    Closure withXmlClosure
    final GitBrowserContext gitBrowserContext = new GitBrowserContext()
    Node mergeOptions
    Integer cloneTimeout
    List<Node> extensions = []
    final StrategyContext strategyContext = new StrategyContext(jobManagement)

    GitContext(List<WithXmlAction> withXmlActions, JobManagement jobManagement) {
        super(jobManagement)
        this.withXmlActions = withXmlActions
    }

    void remote(@DslContext(RemoteContext) Closure remoteClosure) {
        RemoteContext remoteContext = new RemoteContext(withXmlActions)
        executeInContext(remoteClosure, remoteContext)

        remoteConfigs << NodeBuilder.newInstance().'hudson.plugins.git.UserRemoteConfig' {
            if (remoteContext.name) {
                name(remoteContext.name)
            }
            if (remoteContext.refspec) {
                refspec(remoteContext.refspec)
            }
            url(remoteContext.url)
            if (remoteContext.credentials) {
                credentialsId(jobManagement.getCredentialsId(remoteContext.credentials))
            }
        }

        if (remoteContext.browser) {
            gitBrowserContext.browser = remoteContext.browser
        }
    }

    /**
     * @since 1.30
     */
    void strategy(@DslContext(StrategyContext) Closure strategyClosure) {
        executeInContext(strategyClosure, strategyContext)
    }

    void mergeOptions(String remote = null, String branch) {
        if (jobManagement.getPluginVersion('git')?.isOlderThan(new VersionNumber('2.0.0'))) {
            mergeOptions = NodeBuilder.newInstance().'userMergeOptions' {
                mergeRemote(remote ?: '')
                mergeTarget(branch)
            }
        } else {
            extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.PreBuildMerge' {
                options {
                    mergeRemote(remote ?: '')
                    mergeTarget(branch)
                    mergeStrategy('default')
                }
            }
        }
    }

    void branch(String branch) {
        this.branches.add(branch)
    }

    void branches(String... branches) {
        this.branches.addAll(branches)
    }

    void createTag(boolean createTag = true) {
        this.createTag = createTag
    }

    void clean(boolean clean = true) {
        this.clean = clean
    }

    void wipeOutWorkspace(boolean wipeOutWorkspace = true) {
        this.wipeOutWorkspace = wipeOutWorkspace
    }

    void remotePoll(boolean remotePoll = true) {
        this.remotePoll = remotePoll
    }

    void shallowClone(boolean shallowClone = true) {
        this.shallowClone = shallowClone
    }

    void pruneBranches(boolean pruneBranches = true) {
        this.pruneBranches = pruneBranches
    }

    /**
     * @since 1.25
     */
    void localBranch(String localBranch) {
        this.localBranch = localBranch
    }

    void relativeTargetDir(String relativeTargetDir) {
        this.relativeTargetDir = relativeTargetDir
    }

    void reference(String reference) {
        this.reference = reference
    }

    /**
     * @since 1.28
     */
    @RequiresPlugin(id = 'git', minimumVersion = '2.0.0')
    void cloneTimeout(int cloneTimeout) {
        this.cloneTimeout = cloneTimeout
    }

    /**
     * @since 1.26
     */
    void browser(@DslContext(GitBrowserContext) Closure gitBrowserClosure) {
        executeInContext(gitBrowserClosure, gitBrowserContext)
    }

    /**
     * @since 1.33
     */
    void ignoreNotifyCommit(boolean ignoreNotifyCommit = true) {
        this.ignoreNotifyCommit = ignoreNotifyCommit
    }

    void configure(Closure withXmlClosure) {
        this.withXmlClosure = withXmlClosure
    }
}
