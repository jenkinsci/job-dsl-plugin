package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class GroovyPostbuildContext extends AbstractContext {
    String script
    PublisherContext.Behavior behavior = PublisherContext.Behavior.DoNothing
    boolean sandbox
    List<String> classpath

    GroovyPostbuildContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets the Groovy script to execute.
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String)} to read scripts from files.
     */
    void script(String script) {
        this.script = script
    }

    /**
     * Specifies the behavior when the script fails. Defaults to {@code Behavior.DoNothing}.
     */
    void behavior(PublisherContext.Behavior behavior) {
        this.behavior = behavior
    }

    /**
     * If set, executes the the script in a sandbox environment. Defaults to {@code false}.
     */
    void sandbox(boolean sandbox = true) {
        this.sandbox = sandbox
    }

    /**
     * Specify the additional classpath for post build script
     */
    void classpath(List<String> classpath) {
        this.classpath = classpath
    }
}
