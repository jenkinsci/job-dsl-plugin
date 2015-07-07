package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

/**
 * DSL supporting the Log file size checker plugin.
 *
 * See https://wiki.jenkins-ci.org/display/JENKINS/Logfilesizechecker+Plugin.
 */
class LogFileSizeCheckerContext implements Context {
    int maxSize = 0
    boolean failBuild = false

    void maxSize(int maxSize) {
        Preconditions.checkArgument(maxSize > 0, 'Invalid max size, max size > 0 expected')
        this.maxSize = maxSize
    }

    void failBuild(boolean failBuild = true) {
        this.failBuild = failBuild
    }
}
