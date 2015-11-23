package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context

class OrphanedItemStrategyContext implements Context {
    boolean pruneDeadBranches
    int daysToKeep
    int numToKeep

    /**
     * Sets the workflow orphaned item strategy days to keep.
     */
    void daysToKeep(int daysToKeep) {
        this.daysToKeep = daysToKeep
    }

    /**
     * Sets the workflow orphaned item strategy num to keep.
     */
    void numToKeep(int numToKeep) {
        this.numToKeep = numToKeep
    }

    /**
     * Enables to prune dead branches.
     */
    void pruneDeadBranches(boolean pruneDeadBranches = true) {
        this.pruneDeadBranches = pruneDeadBranches
    }
}
