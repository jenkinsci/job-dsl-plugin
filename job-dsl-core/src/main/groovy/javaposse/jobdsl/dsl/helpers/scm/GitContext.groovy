package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class GitContext extends AbstractContext {
    private final Item item

    List<Node> remoteConfigs = []
    List<String> branches = []
    boolean createTag = false
    boolean clean = false
    boolean wipeOutWorkspace = false
    boolean remotePoll = false
    boolean shallowClone = false
    boolean pruneBranches = false
    boolean ignoreNotifyCommit = false
    boolean recursiveSubmodules = false
    boolean trackingSubmodules = false
    String localBranch
    String relativeTargetDir
    String reference = ''
    Closure configureBlock
    final GitBrowserContext gitBrowserContext = new GitBrowserContext(jobManagement)
    Integer cloneTimeout
    GitExtensionContext extensionContext = new GitExtensionContext(jobManagement, item)
    final StrategyContext strategyContext = new StrategyContext(jobManagement)

    GitContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        this.item = item
    }

    /**
     * Adds a remote. Can be repeated to add multiple remotes.
     */
    void remote(@DslContext(RemoteContext) Closure remoteClosure) {
        RemoteContext remoteContext = new RemoteContext(item)
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
                credentialsId(remoteContext.credentials)
            }
        }

        if (remoteContext.browser) {
            gitBrowserContext.browser = remoteContext.browser
        }
    }

    /**
     * Sets the strategy that Jenkins will use to choose what branches to build in what order.
     *
     * @since 1.30
     */
    @Deprecated
    void strategy(@DslContext(StrategyContext) Closure strategyClosure) {
        executeInContext(strategyClosure, strategyContext)
    }

    /**
     * Allows to perform a merge to a particular branch before building.
     */
    @Deprecated
    void mergeOptions(String remote = null, String branch) {
        extensions {
            delegate.mergeOptions {
                delegate.remote(remote)
                delegate.branch(branch)
            }
        }
    }

    /**
     * Allows to perform a merge to a particular branch before building.
     * Can be called multiple times to merge more than one branch.
     *
     * @since 1.37
     */
    @Deprecated
    void mergeOptions(@DslContext(GitMergeOptionsContext) Closure gitMergeOptionsClosure) {
        extensions {
            delegate.mergeOptions(gitMergeOptionsClosure)
        }
    }

    /**
     * Specify the branches to examine for changes and to build.
     */
    void branch(String branch) {
        this.branches.add(branch)
    }

    /**
     * Specify the branches to examine for changes and to build.
     */
    void branches(String... branches) {
        this.branches.addAll(branches)
    }

    /**
     * Create a tag in the workspace for every build to unambiguously mark the commit that was built.
     * Defaults to {@code false}.
     */
    @Deprecated
    void createTag(boolean createTag = true) {
        this.createTag = createTag
    }

    /**
     * Clean up the workspace after every checkout by deleting all untracked files and directories, including those
     * which are specified in {@code .gitignore}. Defaults to {@code false}.
     */
    @Deprecated
    void clean(boolean clean = true) {
        this.clean = clean
    }

    /**
     * Delete the contents of the workspace before building, ensuring a fully fresh workspace.
     * Defaults to {@code false}.
     */
    @Deprecated
    void wipeOutWorkspace(boolean wipeOutWorkspace = true) {
        this.wipeOutWorkspace = wipeOutWorkspace
    }

    /**
     * Uses {@code git ls-remote} polling mechanism to compare the latest built commit SHA with the remote branch
     * without cloning a local copy of the repo. Defaults to {@code false}.
     */
    @Deprecated
    void remotePoll(boolean remotePoll = true) {
        this.remotePoll = remotePoll
    }

    /**
     * Perform shallow clone, so that git will not download history of the project. Defaults to {@code false}.
     */
    @Deprecated
    void shallowClone(boolean shallowClone = true) {
        this.shallowClone = shallowClone
    }

    /**
     * Retrieve all submodules recursively. Defaults to {@code false}.
     *
     * @since 1.33
     */
    @Deprecated
    void recursiveSubmodules(boolean recursive = true) {
        this.recursiveSubmodules = recursive
    }

    /**
     * Update tracking submodules to tip of branch. Defaults to {@code false}.
     *
     * @since 1.40
     */
    @Deprecated
    void trackingSubmodules(boolean tracking = true) {
        this.trackingSubmodules = tracking
    }

    /**
     * Prunes obsolete local branches. Defaults to {@code false}.
     */
    @Deprecated
    void pruneBranches(boolean pruneBranches = true) {
        this.pruneBranches = pruneBranches
    }

    /**
     * If given, checkout the revision to build as HEAD on this branch.
     * @since 1.25
     */
    @Deprecated
    void localBranch(String localBranch) {
        this.localBranch = localBranch
    }

    /**
     * Specify a local directory (relative to the workspace root) where the Git repository will be checked out.
     */
    @Deprecated
    void relativeTargetDir(String relativeTargetDir) {
        this.relativeTargetDir = relativeTargetDir
    }

    /**
     * Specify a folder containing a repository that will be used by Git as a reference during clone operations.
     */
    @Deprecated
    void reference(String reference) {
        this.reference = reference
    }

    /**
     * Specify a timeout (in minutes) for clone and fetch operations.
     *
     * @since 1.28
     */
    @Deprecated
    void cloneTimeout(int cloneTimeout) {
        this.cloneTimeout = cloneTimeout
    }

    /**
     * Adds a repository browser for browsing the details of changes in an external system.
     *
     * @since 1.26
     */
    void browser(@DslContext(GitBrowserContext) Closure gitBrowserClosure) {
        executeInContext(gitBrowserClosure, gitBrowserContext)
    }

    /**
     * If set, the repository will be ignored when the notifyCommit-URL is accessed. Defaults to {@code false}.
     *
     * @since 1.33
     */
    @Deprecated
    void ignoreNotifyCommit(boolean ignoreNotifyCommit = true) {
        this.ignoreNotifyCommit = ignoreNotifyCommit
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code scm} node is passed into the configure block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        this.configureBlock = configureBlock
    }

    /**
     * Adds additional behaviors.
     *
     * @since 1.44
     */
    void extensions(@DslContext(GitExtensionContext) Closure closure) {
        executeInContext(closure, extensionContext)
    }
}
