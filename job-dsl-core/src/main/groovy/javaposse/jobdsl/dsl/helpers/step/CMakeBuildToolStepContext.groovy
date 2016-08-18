package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class CMakeBuildToolStepContext implements Context {
    final List<String> args = []
    final Map<String, String> vars = [:]
    boolean useCmake = false

    /**
     * Specifies arguments to pass to the build tool or cmake (separated by spaces). Can be called multiple times to
     * add more arguments.
     */
    void args(String args) {
        this.args << args
    }

    /**
     * Specifies extra environment variables to pass to the build tool as name-value pairs here. Can be called multiple
     * times to add more environment variables.
     */
    void vars(String name, Object value) {
        this.vars[name] = value?.toString()
    }

    /**
     * If set to true, run the build tool by invoking {@code cmake --build <dir>}.
     * Defaults to {@code false}.
     */
    void useCmake(boolean useCmake = true) {
        this.useCmake = useCmake
    }
}
