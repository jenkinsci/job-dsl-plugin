package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class BatchFileContext implements Context {
    Integer unstableReturn

    /**
     * Marks the build as unstable instead of failed when the script exits with the given error level.
     *
     * Must not be {@code 0}.
     *
     * @since 1.95
     */
    void unstableReturn(int errorLevel) {
        Preconditions.checkArgument(errorLevel != 0, 'unstableReturn must not be 0')
        this.unstableReturn = errorLevel
    }
}
