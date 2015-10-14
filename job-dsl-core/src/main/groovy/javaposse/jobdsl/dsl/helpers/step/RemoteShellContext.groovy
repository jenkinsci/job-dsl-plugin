package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions

class RemoteShellContext extends AbstractContext {
    String siteName
    List<String> commands = []

    protected RemoteShellContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds remote shell commands.
     */
    void command(String... commands) {
        this.commands.addAll(commands)
    }

    /**
     * Adds remote shell commands.
     */
    void command(Iterable<String> commands) {
        commands.each { this.commands << it }
    }

    /**
     * Sets the remote shell's site name.
     */
    void siteName(String siteName) {
        Preconditions.checkNotNull(siteName, 'siteName must be specified')
        this.siteName = siteName
    }
}
