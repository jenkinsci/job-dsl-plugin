package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.StepContext

class PostBuildScriptsContext implements Context {
    final StepContext stepContext
    boolean onlyIfBuildSucceeds = true

    PostBuildScriptsContext(JobManagement jobManagement, Item item) {
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
}
