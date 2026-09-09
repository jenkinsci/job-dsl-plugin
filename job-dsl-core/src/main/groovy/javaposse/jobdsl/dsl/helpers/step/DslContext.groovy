package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class DslContext implements Context {
    private static final Set<String> REMOVE_JOB_ACTIONS = ['IGNORE', 'DISABLE', 'DELETE']
    private static final Set<String> REMOVE_VIEW_ACTIONS = ['IGNORE', 'DELETE']
    private static final Set<String> REMOVE_CONFIG_FILES_ACTIONS = ['IGNORE', 'DELETE']
    private static final Set<String> LOOKUP_STRATEGIES = ['JENKINS_ROOT', 'SEED_JOB']

    String scriptText
    String removedJobAction = 'IGNORE'
    String removedViewAction = 'IGNORE'
    String removedConfigFilesAction = 'IGNORE'
    List<String> externalScripts = []
    boolean ignoreExisting = false
    boolean ignoreMissingFiles = false
    boolean sandbox = false
    boolean failOnMissingPlugin = false
    boolean failOnSeedCollision = false
    boolean unstableOnDeprecation = false
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
     * Specifies the action to be taken for config files that are no longer referenced by DSL scripts.
     *
     * Must be either {@code 'IGNORE'} (default) or {@code 'DELETE'}.
     *
     * @since 1.95
     */
    void removeConfigFilesAction(String action) {
        Preconditions.checkArgument(
                REMOVE_CONFIG_FILES_ACTIONS.contains(action),
                "removeConfigFilesAction must be one of: ${REMOVE_CONFIG_FILES_ACTIONS.join(', ')}"
        )
        this.removedConfigFilesAction = action
    }

    /**
     * Ignores missing DSL script files instead of failing the build. Defaults to {@code false}.
     *
     * @since 1.95
     */
    void ignoreMissingFiles(boolean ignore = true) {
        this.ignoreMissingFiles = ignore
    }

    /**
     * Runs the DSL scripts in the script security sandbox. Defaults to {@code false}.
     *
     * @since 1.95
     */
    void sandbox(boolean sandbox = true) {
        this.sandbox = sandbox
    }

    /**
     * Fails the build instead of marking it unstable when a plugin must be installed or updated to support a
     * feature used by the DSL scripts. Defaults to {@code false}.
     *
     * @since 1.95
     */
    void failOnMissingPlugin(boolean fail = true) {
        this.failOnMissingPlugin = fail
    }

    /**
     * Fails the build when a generated item has the same name as an item managed by another seed job.
     * Defaults to {@code false}.
     *
     * @since 1.95
     */
    void failOnSeedCollision(boolean fail = true) {
        this.failOnSeedCollision = fail
    }

    /**
     * Marks the build as unstable when deprecated DSL features are used, instead of only logging a warning.
     * Defaults to {@code false}.
     *
     * @since 1.95
     */
    void unstableOnDeprecation(boolean unstable = true) {
        this.unstableOnDeprecation = unstable
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
