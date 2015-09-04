package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class XvncContext extends AbstractContext {
    boolean takeScreenshot = false
    boolean useXauthority = true

    XvncContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Takes a screenshot upon completion of the build. Defaults to {@code false}.
     */
    void takeScreenshot(boolean takeScreenshot = true) {
        this.takeScreenshot = takeScreenshot
    }

    /**
     * Creates a dedicated Xauthority file per build. Defaults to {@code true}.
     */
    @RequiresPlugin(id = 'xvnc', minimumVersion = '1.16')
    void useXauthority(boolean useXauthority = true) {
        this.useXauthority = useXauthority
    }
}
