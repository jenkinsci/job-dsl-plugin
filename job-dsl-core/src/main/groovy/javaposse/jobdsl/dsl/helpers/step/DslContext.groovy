package javaposse.jobdsl.dsl.helpers.step

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.Context

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

    void removeViewAction(String action) {
        Preconditions.checkArgument(
                REMOVE_VIEW_ACTIONS.contains(action),
                "removeViewAction must be one of: ${REMOVE_VIEW_ACTIONS.join(', ')}"
        )
        this.removedViewAction = action
    }

    /**
     * @since 1.29
     */
    void additionalClasspath(String classpath) {
        this.additionalClasspath = classpath
    }

    /**
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
