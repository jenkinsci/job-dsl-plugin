package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.Context

class RebuildContext implements Context {
    boolean autoRebuild = false
    boolean rebuildDisabled = false

    /**
     * Rebuilds job without asking for parameters.
     */
    void autoRebuild(boolean autoRebuild = true) {
        this.autoRebuild = autoRebuild
    }

    /**
     * Disables job rebuilding.
     */
    void rebuildDisabled(boolean rebuildDisabled = true) {
        this.rebuildDisabled = rebuildDisabled
    }
}
