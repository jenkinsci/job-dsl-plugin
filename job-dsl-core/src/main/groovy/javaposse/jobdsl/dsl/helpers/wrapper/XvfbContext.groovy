package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class XvfbContext implements Context {
    String screen = '1024x768x24'
    boolean debug = false
    int timeout = 0
    int displayNameOffset = 1
    boolean shutdownWithBuild = false
    boolean autoDisplayName = false
    String assignedLabels
    boolean parallelBuild = false

    /**
     * Changes the screen resolution and pixel depth. Default to {@code '1024x768x24'}.
     */
    void screen(String screen) {
        this.screen = screen
    }

    /**
     * Redirects the output of Xvfb into the job log. Defaults to {@code false}.
     */
    void debug(boolean debug = true) {
        this.debug = debug
    }

    /**
     * Specifies the number of seconds to wait for Xvfb to start. Defaults to 0.
     */
    void timeout(int timeout) {
        this.timeout = timeout
    }

    /**
     * Specifies an offset to be added to the job's executor number which will form the display name. Defaults to 1.
     */
    void displayNameOffset(int displayNameOffset) {
        this.displayNameOffset = displayNameOffset
    }

    /**
     * Keeps Xvfb running for post build steps. Defaults to {@code false}.
     */
    void shutdownWithBuild(boolean shutdownWithBuild = true) {
        this.shutdownWithBuild = shutdownWithBuild
    }

    /**
     * Lets Xvfb choose the display number automatically. Defaults to {@code false}.
     */
    void autoDisplayName(boolean autoDisplayName = true) {
        this.autoDisplayName = autoDisplayName
    }

    /**
     * Starts Xvfb only on nodes with the specified labels.
     */
    void assignedLabels(String assignedLabels) {
        this.assignedLabels = assignedLabels
    }

    /**
     * If set, prevents collision when running multiple Jenkins nodes on the same machine. Defaults to {@code false}.
     */
    void parallelBuild(boolean parallelBuild = true) {
        this.parallelBuild = parallelBuild
    }
}
