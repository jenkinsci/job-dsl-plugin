package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class ArchiveArtifactsContext extends AbstractContext {
    final List<String> patterns = []
    String excludes
    boolean allowEmpty
    boolean fingerprint
    boolean onlyIfSuccessful
    boolean defaultExcludes = true
    boolean caseSensitive = true
    boolean followSymlinks = true

    ArchiveArtifactsContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Specifies the files to archive. Can be called multiple times to add more patterns.
     */
    void pattern(String glob) {
        patterns << glob
    }

    /**
     * Specifies files that will not be archived.
     */
    void exclude(String glob) {
        excludes = glob
    }

    /**
     * If set, does not fail the build if archiving returns nothing. Defaults to {@code false}.
     */
    void allowEmpty(boolean allowEmpty = true) {
        this.allowEmpty = allowEmpty
    }

    /**
     * Fingerprints all archived artifacts. Defaults to {@code false}.
     *
     * @since 1.33
     */
    void fingerprint(boolean fingerprint = true) {
        this.fingerprint = fingerprint
    }

    /**
     * Archives artifacts only if the build is successful. Defaults to {@code false}.
     *
     * @since 1.33
     */
    void onlyIfSuccessful(boolean onlyIfSuccessful = true) {
        this.onlyIfSuccessful = onlyIfSuccessful
    }

    /**
     * Uses default excludes. Defaults to {@code true}.
     *
     * @since 1.33
     */
    void defaultExcludes(boolean defaultExcludes = true) {
        this.defaultExcludes = defaultExcludes
    }

    /**
     * Indicates whether symbolic links should be followed or not. Defaults to {@code true}.
     *
     * @since 1.82
     */
    void followSymlinks(boolean followSymlinks = true) {
        this.followSymlinks = followSymlinks
    }

    /**
     * Indicates whether include and exclude patterns should be considered as case sensitive. Defaults to {@code true}.
     *
     * @since 1.82
     */
    void caseSensitive(boolean caseSensitive = true) {
        this.caseSensitive = caseSensitive
    }
}
