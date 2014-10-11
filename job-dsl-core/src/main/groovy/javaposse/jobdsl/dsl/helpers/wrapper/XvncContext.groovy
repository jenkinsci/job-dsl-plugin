package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.Context

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

    void useXauthority(boolean useXauthority = true) {
        jobManagement.requireMinimumPluginVersion('xvnc', '1.16')
        this.useXauthority = useXauthority
    }
}
