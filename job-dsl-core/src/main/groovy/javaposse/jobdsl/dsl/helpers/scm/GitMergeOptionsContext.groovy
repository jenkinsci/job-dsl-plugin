package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions

class GitMergeOptionsContext extends AbstractContext {
    private static final Set<String> VALID_STRATEGIES = [
            'default', 'resolve', 'recursive', 'octopus', 'ours', 'subtree'
    ]

    String remote
    String branch
    String strategy = 'default'
    FastForwardMergeMode fastForwardMode = FastForwardMergeMode.FF

    GitMergeOptionsContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets the name of the repository that contains the branch to merge.
     */
    void remote(String remote) {
        this.remote = remote
    }

    /**
     * Sets the name of the branch to merge.
     */
    void branch(String branch) {
        this.branch = branch
    }

    /**
     * Selects the merge strategy.
     *
     * Valid values are {@code 'default'} (default), {@code 'resolve'}, {@code 'recursive'}, {@code 'octopus'},
     * {@code 'ours'} and {@code 'subtree'}.
     */
    void strategy(String strategy) {
        Preconditions.checkArgument(
                VALID_STRATEGIES.contains(strategy),
                "strategy must be one of ${VALID_STRATEGIES.join(', ')}"
        )

        this.strategy = strategy
    }

    /**
     * Sets fast-forward merge mode. Defaults to {@code FastForwardMergeMode.FF}
     *
     * @since 1.45
     */
    void fastForwardMode(FastForwardMergeMode fastForwardMode) {
        this.fastForwardMode = fastForwardMode
    }

    enum FastForwardMergeMode {
        /**
         * When the merge resolves as a fast-forward, only update the branch pointer, without creating a merge commit.
         */
        FF,

        /**
         * Refuse to merge and exit with a non-zero status unless the current HEAD is already up-to-date or the merge
         * can be resolved as a fast-forward.
         */
        FF_ONLY,

        /**
         * Create a merge commit even when the merge resolves as a fast-forward.
         */
        NO_FF
    }
}
