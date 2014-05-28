package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class CloneWorkspaceContext implements Context {

    String workspaceExcludeGlob = ''
    String criteria = 'Any' // 'Not Failed', 'Successful'
    String archiveMethod = 'TAR' // 'ZIP'
    boolean overrideDefaultExcludes = false

    void workspaceExcludeGlob(String workspaceExcludeGlob) {
        this.workspaceExcludeGlob = workspaceExcludeGlob
    }

    void criteria(String criteria) {
        this.criteria = criteria
    }

    void archiveMethod(String archiveMethod) {
        this.archiveMethod = archiveMethod
    }

    void overrideDefaultExcludes(boolean overrideDefaultExcludes) {
        this.overrideDefaultExcludes = overrideDefaultExcludes
    }
}