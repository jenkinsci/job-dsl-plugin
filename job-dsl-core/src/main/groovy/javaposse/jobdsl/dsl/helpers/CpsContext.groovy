package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context

class CpsContext implements Context {
    String script
    String scriptPath
    boolean sandbox

    /**
     * Sets the workflow DSL script. Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String)}
     * to read the script from a file.
     */
    void script(String script) {
        this.script = script
    }

    /**
     * ScriptPath for Jenkinsfile which declares pipeline.
     */
    void scriptPath(String scriptPath) {
        this.scriptPath = scriptPath
    }

    /**
     * Enables the Groovy sandbox for the script.
     */
    void sandbox(boolean sandbox = true) {
        this.sandbox = sandbox
    }
}
