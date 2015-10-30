package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class JobAuthorizationContext extends AuthorizationContext {
    boolean blocksInheritance

    JobAuthorizationContext(JobManagement jobManagement) {
        super(jobManagement, 'hudson.security.AuthorizationMatrixProperty')
    }

    /**
     * Blocks inheritance of the global authorization matrix.
     *
     * @since 1.35
     */
    @RequiresPlugin(id = 'matrix-auth', minimumVersion = '1.2')
    void blocksInheritance(boolean blocksInheritance = true) {
        this.blocksInheritance = blocksInheritance
    }
}
