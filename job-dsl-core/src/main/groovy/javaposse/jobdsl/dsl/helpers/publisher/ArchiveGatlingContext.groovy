package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class ArchiveGatlingContext extends AbstractContext {
    boolean enabled = true

    ArchiveGatlingContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Enables tracking of gatling load simulation results.
     * Defaults to {@code true}.
     */
    void enabled(boolean enabled = true) {
        this.enabled = enabled
    }
}
