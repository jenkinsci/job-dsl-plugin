package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class SvnLocationContext extends AbstractContext {
    String directory = '.'
    String credentials
    SvnDepth depth = SvnDepth.INFINITY

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
    @RequiresPlugin(id = 'subversion', minimumVersion = '2.0')
    void credentials(String credentials) {
        this.credentials = jobManagement.getCredentialsId(credentials)
    }

    /**
     * Specifies the depth for sparse checkouts. Defaults to {@code SvnDepth.INFINITY}.
     */
    void depth(SvnDepth depth) {
        this.depth = depth
    }
}
