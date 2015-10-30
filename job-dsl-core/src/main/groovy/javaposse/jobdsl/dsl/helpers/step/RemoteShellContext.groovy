package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class RemoteShellContext extends AbstractContext {
    List<String> commands = []

    protected RemoteShellContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds remote shell commands.
     *
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String) readFileFromWorkspace} to read
     * the script from a file.
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
}
