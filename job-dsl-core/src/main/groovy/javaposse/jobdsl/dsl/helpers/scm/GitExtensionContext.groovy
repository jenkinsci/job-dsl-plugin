package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

@ContextType('hudson.plugins.git.extensions.GitSCMExtension')
class GitExtensionContext extends AbstractExtensibleContext {
    final List<Node> extensions = []

    GitExtensionContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        extensions << node
    }

    /**
     * Allows to perform a merge to a particular branch before building.
     * Can be called multiple times to merge more than one branch.
     */
    void mergeOptions(@DslContext(GitMergeOptionsContext) Closure gitMergeOptionsClosure) {
        GitMergeOptionsContext gitMergeOptionsContext = new GitMergeOptionsContext(jobManagement)
        executeInContext(gitMergeOptionsClosure, gitMergeOptionsContext)

        extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.PreBuildMerge' {
            options {
                mergeRemote(gitMergeOptionsContext.remote ?: '')
                mergeTarget(gitMergeOptionsContext.branch ?: '')
                mergeStrategy(gitMergeOptionsContext.strategy)
                fastForwardMode(gitMergeOptionsContext.fastForwardMode.name())
            }
        }
    }

    /**
     * Cleans up the workspace after every checkout by deleting all untracked files and directories, including those
     * which are specified in {@code .gitignore}.
     */
    void cleanAfterCheckout() {
        extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.CleanCheckout'()
    }

    /**
     * Clean up the workspace before every checkout by deleting all untracked files and directories, including those
     * which are specified in {@code .gitignore}.
     */
    void cleanBeforeCheckout() {
        extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.CleanBeforeCheckout'()
    }

    /**
     * Specifies behaviors for cloning repositories.
     */
    void cloneOptions(@DslContext(GitCloneOptionsContext) Closure closure) {
        GitCloneOptionsContext context = new GitCloneOptionsContext(jobManagement)
        executeInContext(closure, context)

        extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.CloneOption' {
            shallow(context.shallow)
            noTags(context.noTags)
            reference(context.reference ?: '')
            if (context.timeout != null) {
                timeout(context.timeout)
            }
            honorRefspec(context.honorRefspec)
        }
    }

    /**
     * Specifies behaviors for handling sub-modules.
     */
    void submoduleOptions(@DslContext(GitSubmoduleOptionsContext) Closure closure) {
        GitSubmoduleOptionsContext context = new GitSubmoduleOptionsContext(jobManagement)
        executeInContext(closure, context)

        extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.SubmoduleOption' {
            disableSubmodules(context.disable)
            recursiveSubmodules(context.recursive)
            trackingSubmodules(context.tracking)
            reference(context.reference ?: '')
            if (context.timeout != null) {
                timeout(context.timeout)
            }
            if (jobManagement.isMinimumPluginVersionInstalled('git', '3.0.0')) {
                parentCredentials(context.parentCredentials)
            }
        }
    }

    /**
     * Delete the contents of the workspace before building, ensuring a fully fresh workspace.
     */
    void wipeOutWorkspace() {
        extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.WipeWorkspace'()
    }

    /**
     * Polls by using the workspace and disables the {@code git ls-remote} polling mechanism.
     */
    void disableRemotePoll() {
        extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.DisableRemotePoll'()
    }

    /**
     * Prunes obsolete local branches.
     */
    void pruneBranches() {
        extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.PruneStaleBranch'()
    }

    /**
     * If given, checkout the revision to build as HEAD on this branch.
     *
     * @since 1.53
     */
    void localBranch() {
        localBranch(null)
    }

    /**
     * If given, checkout the revision to build as HEAD on this branch.
     */
    void localBranch(String branch) {
        extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.LocalBranch' {
            if (branch) {
                delegate.localBranch(branch)
            }
        }
    }

    /**
     * Specifies a local directory (relative to the workspace root) where the Git repository will be checked out.
     */
    void relativeTargetDirectory(String relativeTargetDirectory) {
        extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.RelativeTargetDirectory' {
            relativeTargetDir(relativeTargetDirectory)
        }
    }

    /**
     * If set, the repository will be ignored when the notifyCommit-URL is accessed.
     */
    void ignoreNotifyCommit() {
        extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.IgnoreNotifyCommit'()
    }

    /**
     * Creates a tag in the workspace for every build to unambiguously mark the commit that was built.
     */
    void perBuildTag() {
        extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.PerBuildTag'()
    }

    /**
     * Sets the strategy that Jenkins will use to choose what branches to build in what order.
     */
    void choosingStrategy(@DslContext(StrategyContext) Closure strategyClosure) {
        StrategyContext strategyContext = new StrategyContext(jobManagement)
        executeInContext(strategyClosure, strategyContext)

        extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.BuildChooserSetting'(
                [strategyContext.buildChooser]
        )
    }
}
