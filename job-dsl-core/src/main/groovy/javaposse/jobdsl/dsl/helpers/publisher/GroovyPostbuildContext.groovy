package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class GroovyPostbuildContext extends AbstractContext {
    String script
    PublisherContext.Behavior behavior = PublisherContext.Behavior.DoNothing
    boolean sandbox
    List<String> classpath = []

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
     * Specifies an additional classpath entry. Can be called multiple times to add more entries.
     *
     * @since 1.49
     */
    void classpath(String... entries) {
        entries.each {
            URL url
            try {
                url = new URL(it)
            } catch (MalformedURLException ignore) {
                url = new File(it).toURI().toURL()
            }
            classpath << url.toString()
        }
    }
}
