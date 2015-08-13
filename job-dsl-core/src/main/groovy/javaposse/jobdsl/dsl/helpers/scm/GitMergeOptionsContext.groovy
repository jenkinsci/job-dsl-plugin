package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin

class GitMergeOptionsContext extends AbstractContext {
    private static final Set<String> VALID_STRATEGIES = [
            'default', 'resolve', 'recursive', 'octopus', 'ours', 'subtree'
    ]

    String remote
    String branch
    String strategy = 'default'

    GitMergeOptionsContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void remote(String remote) {
        this.remote = remote
    }

    void branch(String branch) {
        this.branch = branch
    }

    @RequiresPlugin(id = 'git', minimumVersion = '2.0.0')
    void strategy(String strategy) {
        Preconditions.checkArgument(
                VALID_STRATEGIES.contains(strategy),
                "strategy must be one of ${VALID_STRATEGIES.join(', ')}"
        )

        this.strategy = strategy
    }
}
