package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class GitSubmoduleOptionsContext extends AbstractContext {
    boolean disable
    boolean recursive
    boolean tracking
    String reference

    protected GitSubmoduleOptionsContext(JobManagement jobManagement) {
        super(jobManagement)
    }

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

    /**
     * Specify a folder containing a repository that will be used by Git as a reference during clone operations.
     * @since 1.45
     */
    @RequiresPlugin(id = 'git', minimumVersion = '2.4.1')
    void reference(String reference) {
        this.reference = reference
    }
}
