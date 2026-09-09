package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class ShellContext implements Context {
    Integer unstableReturn

    /**
     * Marks the build as unstable instead of failed when the script exits with the given code.
     *
     * Must be between 1 and 255.
     *
     * @since 1.95
     */
    void unstableReturn(int exitCode) {
        Preconditions.checkArgument(exitCode >= 1 && exitCode <= 255, 'unstableReturn must be between 1 and 255')
        this.unstableReturn = exitCode
    }
}
