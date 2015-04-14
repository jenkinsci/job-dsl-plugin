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

    void directory(String directory) {
        this.directory = directory
    }

    @RequiresPlugin(id = 'subversion', minimumVersion = '2.0')
    void credentials(String credentials) {
        this.credentials = jobManagement.getCredentialsId(credentials)
    }

    void depth(SvnDepth depth) {
        this.depth = depth
    }
}
