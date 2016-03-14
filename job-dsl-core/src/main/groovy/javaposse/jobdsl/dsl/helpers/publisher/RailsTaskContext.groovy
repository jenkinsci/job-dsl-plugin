package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class RailsTaskContext implements Context {
    String rakeVersion = '(Default)'
    String rakeWorkingDirectory

    /**
     * Sets the Rake version. Defaults to {@code '(Default)'}.
     */
    void rakeVersion(String rakeVersion) {
        this.rakeVersion = rakeVersion
    }

    /**
     * Sets the working directory for Rake.
     */
    void rakeWorkingDirectory(String rakeWorkingDirectory) {
        this.rakeWorkingDirectory = rakeWorkingDirectory
    }
}
