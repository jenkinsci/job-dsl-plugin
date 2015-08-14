package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class ArchiveArtifactsContext extends AbstractContext {
    final List<String> patterns = []
    String excludes
    boolean latestOnly
    boolean allowEmpty
    boolean fingerprint
    boolean onlyIfSuccessful
    boolean defaultExcludes = true

    ArchiveArtifactsContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void pattern(String glob) {
        patterns << glob
    }

    void exclude(String glob) {
        excludes = glob
    }

    @Deprecated
    void latestOnly(boolean latestOnly = true) {
        jobManagement.logDeprecationWarning()
        this.latestOnly = latestOnly
    }

    void allowEmpty(boolean allowEmpty = true) {
        this.allowEmpty = allowEmpty
    }

    /**
     * @since 1.33
     */
    void fingerprint(boolean fingerprint = true) {
        this.fingerprint = fingerprint
    }

    /**
     * @since 1.33
     */
    void onlyIfSuccessful(boolean onlyIfSuccessful = true) {
        this.onlyIfSuccessful = onlyIfSuccessful
    }

    /**
     * @since 1.33
     */
    void defaultExcludes(boolean defaultExcludes = true) {
        this.defaultExcludes = defaultExcludes
    }
}
