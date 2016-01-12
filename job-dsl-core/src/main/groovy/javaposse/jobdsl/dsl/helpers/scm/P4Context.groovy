package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.Preconditions.checkNotNull

class P4Context extends AbstractContext {

    Node workspaceConfig
    Closure withXmlClosure

    P4Context(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Set up a manual workspace with workspace name and viewspec.
     */
    void manual(String workspaceName, String viewspec) {
        checkNotNull(workspaceName, 'workspaceName must not be null')
        checkNotNull(viewspec, 'viewspec must not be null')

        workspaceConfig = new NodeBuilder().workspace(class: 'org.jenkinsci.plugins.p4.workspace.ManualWorkspaceImpl') {
            charset 'none'
            pinHost 'false'
            name workspaceName
            spec {
                allwrite 'false'
                clobber 'false'
                compress 'false'
                locked 'false'
                modtime 'false'
                rmdir 'false'
                streamName ''
                line 'LOCAL'
                view viewspec
            }
        }
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code scm} node is passed into the configure block.
     *
     */
    void configure(Closure withXmlClosure) {
        this.withXmlClosure = withXmlClosure
    }
}
