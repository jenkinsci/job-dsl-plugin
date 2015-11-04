package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class CopyArtifactUpstreamBuildSelectorContext extends AbstractContext {
    boolean fallbackToLastSuccessful
    boolean allowUpstreamDependencies

    CopyArtifactUpstreamBuildSelectorContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Use "Last successful build" as fallback.
     */
    void fallbackToLastSuccessful(boolean fallbackToLastSuccessful = true) {
        this.fallbackToLastSuccessful = fallbackToLastSuccessful
    }

    /**
     * Allow upstream build whose artifacts feed into this build. Defaults to {@code false}.
     */
    @RequiresPlugin(id = 'copyartifact', minimumVersion = '1.37')
    void allowUpstreamDependencies(boolean allowUpstreamDependencies = true) {
        this.allowUpstreamDependencies = allowUpstreamDependencies
    }
}
