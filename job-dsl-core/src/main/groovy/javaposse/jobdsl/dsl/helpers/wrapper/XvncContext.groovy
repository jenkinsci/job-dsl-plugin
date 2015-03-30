package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class XvncContext implements Context {
    private final JobManagement jobManagement

    boolean takeScreenshot = false
    boolean useXauthority = true

    XvncContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void takeScreenshot(boolean takeScreenshot = true) {
        this.takeScreenshot = takeScreenshot
    }

    @RequiresPlugin(id = 'xvnc', minimumVersion = '1.16')
    void useXauthority(boolean useXauthority = true) {
        this.useXauthority = useXauthority
    }
}
