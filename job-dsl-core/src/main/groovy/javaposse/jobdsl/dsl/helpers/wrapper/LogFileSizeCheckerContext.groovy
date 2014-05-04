package javaposse.jobdsl.dsl.helpers.wrapper

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.helpers.Context

/**
 * DSL supporting the Log file size checker plugin.
 *
 * See https://wiki.jenkins-ci.org/display/JENKINS/Logfilesizechecker+Plugin.
 */
class LogFileSizeCheckerContext implements Context {

    boolean useOwn = false
    Integer maxSize = 0
    boolean fail = false

    void useOwn(boolean useOwn) {
        this.useOwn = useOwn
    }

    void maxSize(Integer maxSize) {
        Preconditions.checkArgument((maxSize > 0), 'Invalid max size, max size > 0 expected')
        this.maxSize = maxSize
    }

    void fail(boolean failBuild) {
        this.fail = failBuild
    }
}
