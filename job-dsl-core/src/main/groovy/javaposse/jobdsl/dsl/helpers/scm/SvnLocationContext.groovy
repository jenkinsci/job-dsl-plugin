package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class SvnLocationContext extends AbstractContext {
    String directory = '.'
    String credentials
    SvnDepth depth = SvnDepth.INFINITY
    boolean ignoreExternals

    SvnLocationContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * The directoy to checkout to. By default, files are checked out into the workspace directory.
     */
    void directory(String directory) {
        this.directory = directory
    }

    /**
     * Sets credentials for authentication with the remote server.
     */
    void credentials(String credentials) {
        this.credentials = credentials
    }

    /**
     * Specifies the depth for sparse checkouts. Defaults to {@code SvnDepth.INFINITY}.
     */
    void depth(SvnDepth depth) {
        this.depth = depth
    }

    /**
     * If set, disables externals definition processing.
     *
     * @since 1.48
     */
    void ignoreExternals(boolean ignoreExternals = true) {
        this.ignoreExternals = ignoreExternals
    }
}
