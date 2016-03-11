package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class HgContext extends AbstractContext {
    String installation
    List<String> modules = []
    String subdirectory
    String branch
    String tag
    String credentialsId
    boolean clean
    boolean disableChangeLog
    Closure configureBlock

    HgContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Use a specific installation of Mercurial.
     */
    void installation(String installation) {
        this.installation = installation
    }

    /**
     * Checkout selected modules.
     */
    void modules(String... modules) {
        this.modules.addAll(modules)
    }

    /**
     * Checkout into subdirectory.
     */
    void subdirectory(String subdirectory) {
        this.subdirectory = subdirectory
    }

    /**
     * Checkout selected branch.
     */
    void branch(String branch) {
        this.branch = branch
    }

    /**
     * Checkout selected tag.
     */
    void tag(String tag) {
        this.tag = tag
    }

    /**
     * Use pre-defined credentials.
     */
    void credentials(String credentials) {
        this.credentialsId = credentials
    }

    /**
     * Do a clean checkout. Defaults to {@code false}.
     */
    void clean(boolean clean = true) {
        this.clean = clean
    }

    /**
     * Disable the change log. Defaults to {@code false}.
     */
    void disableChangeLog(boolean disableChangeLog = true) {
        this.disableChangeLog = disableChangeLog
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code scm} node is passed into the configure block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        this.configureBlock = configureBlock
    }
}
