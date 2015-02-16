package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement

class SvnLocationContext implements Context {
    private final JobManagement jobManagement
    String directory = '.'
    String credentials
    SvnDepth depth = SvnDepth.INFINITY

    SvnLocationContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void directory(String directory) {
        this.directory = directory
    }

    void credentials(String credentials) {
        jobManagement.requireMinimumPluginVersion('subversion', '2.0')

        this.credentials = jobManagement.getCredentialsId(credentials)
    }

    void depth(SvnDepth depth) {
        this.depth = depth
    }
}
