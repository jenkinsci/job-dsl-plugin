package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Context

class DefaultOrphanedItemStrategyContext implements Context {
    int daysToKeep = -1
    int numToKeep = -1

    /**
     * Sets the number of days to keep old items. Defaults to {@code -1}.
     */
    void daysToKeep(int daysToKeep) {
        this.daysToKeep = daysToKeep
    }

    /**
     * Sets the number of old items to keep. Defaults to {@code -1}.
     */
    void numToKeep(int numToKeep) {
        this.numToKeep = numToKeep
    }
}
