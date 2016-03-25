package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class CMakeBuildToolStepContext implements Context {
    String args
    String vars
    boolean useCmake = false

    /**
     * Specifies arguments to pass to the build tool or cmake (separated by spaces).
     */
    void args(String args) {
        this.args = args
    }

    /**
     * Specifies extra environment variables to pass to the build tool as key-value pairs here.
     */
    void vars(String vars) {
        this.vars = vars
    }

    /**
     * If set to true, run the build tool by invoking cmake --build <dir>.
     * Defaults to {@code false}.
     */
    void useCmake(boolean useCmake = true) {
        this.useCmake = useCmake
    }

}
