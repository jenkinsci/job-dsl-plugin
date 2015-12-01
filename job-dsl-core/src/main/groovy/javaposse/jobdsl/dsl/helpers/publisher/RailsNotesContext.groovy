package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class RailsNotesContext implements Context {
    String rakeVersion = '(Default)'
    String rakeWorkingDir

    /**
     * Sets the Rake Version.
     */
    void rakeVersion(String rakeVersion) {
        this.rakeVersion = rakeVersion
    }

    /**
     * Sets the rake working directory.
     */
    void rakeWorkingDir(String rakeWorkingDir) {
        this.rakeWorkingDir = rakeWorkingDir
    }
}
