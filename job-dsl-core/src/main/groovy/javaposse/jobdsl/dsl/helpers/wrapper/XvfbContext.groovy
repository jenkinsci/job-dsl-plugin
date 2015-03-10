package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class XvfbContext implements Context {
    String installationName
    String screen = '1024x768x24'
    boolean debug = false
    int timeout = 0
    int displayNameOffset = 1
    boolean shutdownWithBuild = false
    boolean autoDisplayName = false
    String assignedLabels
    boolean parallelBuild = false

    XvfbContext(String installationName) {
        this.installationName = installationName
    }

    void installationName(String installationName) {
        this.installationName = installationName
    }

    void screen(String screen) {
        this.screen = screen
    }

    void debug(boolean debug = true) {
        this.debug = debug
    }

    void timeout(int timeout) {
        this.timeout = timeout
    }

    void displayNameOffset(int displayNameOffset) {
        this.displayNameOffset = displayNameOffset
    }

    void shutdownWithBuild(boolean shutdownWithBuild = true) {
        this.shutdownWithBuild = shutdownWithBuild
    }

    void autoDisplayName(boolean autoDisplayName = false) {
        this.autoDisplayName = false
    }

    void assignedLabels(String assignedLabels) {
        this.assignedLabels = assignedLabels
    }

    void parallelBuild(boolean parallelBuild = true) {
        this.parallelBuild = parallelBuild
    }
}