package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class GitCloneOptionsContext extends AbstractContext {
    boolean shallow
    boolean noTags
    String reference
    Integer timeout
    boolean honorRefspec

    GitCloneOptionsContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Perform shallow clone, so that Git will not download history of the project. Defaults to {@code false}.
     */
    void shallow(boolean shallow = true) {
        this.shallow = shallow
    }

    /**
     * Do not check out tags. Defaults to {@code false}.
     *
     * @since 1.64
     */
    void noTags(boolean noTags = true) {
        this.noTags = noTags
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
     *
     * @since 1.52
     */
    void honorRefspec(boolean honorRefspec = true) {
        this.honorRefspec = honorRefspec
    }
}
