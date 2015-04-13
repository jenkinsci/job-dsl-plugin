package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

class ScmTriggerContext implements Context {
    boolean ignorePostCommitHooks

    /**
     * @since 1.31
     */
    void ignorePostCommitHooks(boolean ignorePostCommitHooks = true) {
        this.ignorePostCommitHooks = ignorePostCommitHooks
    }
}
