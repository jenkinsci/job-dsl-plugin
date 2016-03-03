package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context

class GitSubmoduleOptionsContext implements Context {
    boolean disable
    boolean recursive
    boolean tracking

    /**
     * Disables submodules processing. Defaults to {@code false}.
     */
    void disable(boolean disable = true) {
        this.disable = disable
    }

    /**
     * Retrieves all submodules recursively. Defaults to {@code false}.
     */
    void recursive(boolean recursive = true) {
        this.recursive = recursive
    }

    /**
     * Retrieves the tip of the configured branch in {@code .gitmodules}. Defaults to {@code false}.
     */
    void tracking(boolean tracking = true) {
        this.tracking = tracking
    }
}
