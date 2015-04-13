package javaposse.jobdsl.dsl.helpers.step

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.Context

class DslContext implements Context {
    private static final Set<String> REMOVE_JOB_ACTIONS = ['IGNORE', 'DISABLE', 'DELETE']

    String scriptText
    String removedJobAction = 'IGNORE'
    List<String> externalScripts = []
    boolean ignoreExisting = false
    String additionalClasspath

    void text(String text) {
        this.scriptText = Preconditions.checkNotNull(text)
    }

    void external(String... dslScripts) {
        externalScripts.addAll(dslScripts)
    }

    /**
     * @since 1.29
     */
    void external(Iterable<String> dslScripts) {
        dslScripts.each { externalScripts << it }
    }

    void ignoreExisting(boolean ignore = true) {
        this.ignoreExisting = ignore
    }

    void removeAction(String action) {
        Preconditions.checkArgument(
                REMOVE_JOB_ACTIONS.contains(action),
                "removeAction must be one of: ${REMOVE_JOB_ACTIONS.join(', ')}"
        )
        this.removedJobAction = action
    }

    /**
     * @since 1.29
     */
    void additionalClasspath(String classpath) {
        this.additionalClasspath = classpath
    }
}
