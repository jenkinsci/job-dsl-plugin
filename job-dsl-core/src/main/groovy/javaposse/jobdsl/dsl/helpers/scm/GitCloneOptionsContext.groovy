package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context

class GitCloneOptionsContext implements Context {
    boolean shallow
    String reference
    Integer timeout
    boolean honorRefspec

    /**
     * Perform shallow clone, so that Git will not download history of the project. Defaults to {@code false}.
     */
    void shallow(boolean shallow = true) {
        this.shallow = shallow
    }

    /**
     * Specify a folder containing a repository that will be used by Git as a reference during clone operations.
     */
    void reference(String reference) {
        this.reference = reference
    }

    /**
     * Specify a timeout (in minutes) for clone and fetch operations.
     */
    void timeout(Integer timeout) {
        this.timeout = timeout
    }

    /**
     * Honor refspec on initial clone.
     */
    void honorRefspec(boolean honorRefspec = true) {
        this.honorRefspec = honorRefspec
    }
}
