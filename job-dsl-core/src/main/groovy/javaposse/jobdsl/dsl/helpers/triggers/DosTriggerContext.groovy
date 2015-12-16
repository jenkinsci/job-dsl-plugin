package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

class DosTriggerContext implements Context {
    String triggerScript

    /**
     * It sets the script that will be executed periodically which indicates that a build should be started
     * when the script sets the CAUSE variable to something.
     *
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String) readFileFromWorkspace} to read
     * the script from a file.
     */
    void triggerScript(String triggerScript) {
        this.triggerScript = triggerScript
    }
}
