package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class LogFileSizeCheckerContext implements Context {
    int maxSize = 0
    boolean failBuild = false

    /**
     * Sets the maximum allowed size for the output log.
     */
    void maxSize(int maxSize) {
        Preconditions.checkArgument(maxSize > 0, 'Invalid max size, max size > 0 expected')
        this.maxSize = maxSize
    }

    /**
     * Fails the build when the log size exceeds the maximum size. Defaults to {@code false}.
     */
    void failBuild(boolean failBuild = true) {
        this.failBuild = failBuild
    }
}
