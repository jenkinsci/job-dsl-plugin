package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class CloneWorkspaceContext implements Context {
    String workspaceExcludeGlob = ''
    String criteria = 'Any' // 'Not Failed', 'Successful'
    String archiveMethod = 'TAR' // 'ZIP'
    boolean overrideDefaultExcludes = false

    /**
     * Specifies files to exclude from the cloned workspace.
     */
    void workspaceExcludeGlob(String workspaceExcludeGlob) {
        this.workspaceExcludeGlob = workspaceExcludeGlob
    }

    /**
     * Selects the criteria for builds to be archived. Must be one of {@code 'Any'} (default), {@code 'Not Failed'} or
     * {@code 'Successful'}.
     */
    void criteria(String criteria) {
        this.criteria = criteria
    }

    /**
     * Sets the archive method. Must be either {@code 'TAR'} (default) or {@code 'ZIP'}.
     */
    void archiveMethod(String archiveMethod) {
        this.archiveMethod = archiveMethod
    }

    /**
     * Overrides default Ant excludes.
     */
    void overrideDefaultExcludes(boolean overrideDefaultExcludes) {
        this.overrideDefaultExcludes = overrideDefaultExcludes
    }
}
