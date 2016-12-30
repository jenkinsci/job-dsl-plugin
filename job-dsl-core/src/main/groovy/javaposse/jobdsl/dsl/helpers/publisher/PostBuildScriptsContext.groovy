package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.StepContext

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class PostBuildScriptsContext extends AbstractContext {
    private static final Set<String> VALID_EXECUTE_ON_MODES = [
            'MATRIX', 'AXES', 'BOTH'
    ]

    String executeOn = 'BOTH'
    final StepContext stepContext
    boolean onlyIfBuildSucceeds = true
    boolean onlyIfBuildFails
    boolean markBuildUnstable

    PostBuildScriptsContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        this.stepContext = new StepContext(jobManagement, item)
    }

    /**
     * Adds build steps to run at the end of the build.
     */
    void steps(@DslContext(StepContext) Closure stepClosure) {
        ContextHelper.executeInContext(stepClosure, stepContext)
    }

    /**
     * If set, runs the build steps only if the build was successful. Defaults to {@code true}.
     */
    void onlyIfBuildSucceeds(boolean onlyIfBuildSucceeds = true) {
        this.onlyIfBuildSucceeds = onlyIfBuildSucceeds
    }

    /**
     * If set, runs the build steps only if the build failed. Defaults to {@code false}.
     *
     * @since 1.42
     */
    void onlyIfBuildFails(boolean onlyIfBuildFails = true) {
        this.onlyIfBuildFails = onlyIfBuildFails
    }

    /**
     * If set, marks build as unstable instead of failed on script error. Defaults to {@code false}.
     *
     * @since 1.42
     */
    void markBuildUnstable(boolean markBuildUnstable = true) {
        this.markBuildUnstable = markBuildUnstable
    }

    /**
     * Specifies where to execute the script. Must be one of {@code 'BOTH'} (default), {@code 'MATRIX'}
     * or {@code 'AXES'}. This setting is only relevant for matrix jobs.
     *
     * @since 1.54
     */
    void executeOn(String executeOn) {
        checkArgument(
                VALID_EXECUTE_ON_MODES.contains(executeOn),
                "Execute script on needs to be one of these values: ${VALID_EXECUTE_ON_MODES.join(',')}"
        )
        this.executeOn = executeOn
    }
}
