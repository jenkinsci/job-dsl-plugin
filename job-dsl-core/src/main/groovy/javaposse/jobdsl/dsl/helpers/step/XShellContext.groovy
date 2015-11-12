package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class XShellContext extends AbstractContext {
    String commandLine
    boolean executableInWorkspaceDir = false
    String regexToKill = null
    int timeAllocated = 0

    XShellContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets the shell command.
     */
    void commandLine(String commandLine) {
        this.commandLine = commandLine
    }

    /**
     * Sets if the executable runs from current working directory. Defaults to {@code true}.
     */
    void executableInWorkspaceDir(boolean executableInWorkspaceDir = true) {
        this.executableInWorkspaceDir = executableInWorkspaceDir
    }

    /**
     * Set the regex which will parse the output from the step and if it matches
     * this regex then the step is killed.
     */
    void regexToKill(String regexToKill) {
        this.regexToKill = regexToKill
    }

    /**
     * Sets the amount of time to run this step.. If it goes over then the step is killed.
     */
    void timeAllocated(int timeAllocated) {
        this.timeAllocated = timeAllocated
    }
}
