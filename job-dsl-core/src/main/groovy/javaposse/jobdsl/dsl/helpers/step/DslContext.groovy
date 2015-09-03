package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class DslContext implements Context {
    private static final Set<String> REMOVE_JOB_ACTIONS = ['IGNORE', 'DISABLE', 'DELETE']
    private static final Set<String> REMOVE_VIEW_ACTIONS = ['IGNORE', 'DELETE']
    private static final Set<String> LOOKUP_STRATEGIES = ['JENKINS_ROOT', 'SEED_JOB']

    String scriptText
    String removedJobAction = 'IGNORE'
    String removedViewAction = 'IGNORE'
    List<String> externalScripts = []
    boolean ignoreExisting = false
    String additionalClasspath
    String lookupStrategy = 'JENKINS_ROOT'

    /**
     * Sets the Job DSL script.
     */
    void text(String text) {
        Preconditions.checkNotNull(text, 'text must be specified')
        this.scriptText = text
    }

    /**
     * Reads Job DSL scripts from the job's workspace.
     */
    void external(String... dslScripts) {
        externalScripts.addAll(dslScripts)
    }

    /**
     * Reads Job DSL scripts from the job's workspace.
     *
     * @since 1.29
     */
    void external(Iterable<String> dslScripts) {
        dslScripts.each { externalScripts << it }
    }

    /**
     * Ignores existing items when processing Job DSL scripts. Defaults to {@code false}.
     */
    void ignoreExisting(boolean ignore = true) {
        this.ignoreExisting = ignore
    }

    /**
     * Specifies the action to be taken for job that have been removed from DSL scripts.
     *
     * Must be one of {@code 'IGNORE'} (default), {@code 'DISABLE'} or {@code 'DELETE'}.
     */
    void removeAction(String action) {
        Preconditions.checkArgument(
                REMOVE_JOB_ACTIONS.contains(action),
                "removeAction must be one of: ${REMOVE_JOB_ACTIONS.join(', ')}"
        )
        this.removedJobAction = action
    }

    /**
     * Specifies the action to be taken for views that have been removed from DSL scripts.
     *
     * Must be either {@code 'IGNORE'} (default) or {@code 'DELETE'}.
     *
     * @since 1.35
     */
    void removeViewAction(String action) {
        Preconditions.checkArgument(
                REMOVE_VIEW_ACTIONS.contains(action),
                "removeViewAction must be one of: ${REMOVE_VIEW_ACTIONS.join(', ')}"
        )
        this.removedViewAction = action
    }

    /**
     * Adds entries to the classpath for DSL scripts.
     *
     * @since 1.29
     */
    void additionalClasspath(String classpath) {
        this.additionalClasspath = classpath
    }

    /**
     * Chooses the lookup strategy for relative job names.
     *
     * Must be either {@code 'JENKINS_ROOT'} (default) or {@code 'SEED_JOB'}.
     *
     * @since 1.33
     */
    void lookupStrategy(String lookupStrategy) {
        Preconditions.checkArgument(
                LOOKUP_STRATEGIES.contains(lookupStrategy),
                "lookupStrategy must be one of: ${LOOKUP_STRATEGIES.join(', ')}"
        )
        this.lookupStrategy = lookupStrategy
    }
}
