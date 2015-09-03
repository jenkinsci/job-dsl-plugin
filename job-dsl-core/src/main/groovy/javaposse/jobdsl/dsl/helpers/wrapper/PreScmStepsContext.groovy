package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.StepContext

class PreScmStepsContext implements Context {
    final StepContext stepContext
    boolean failOnError

    PreScmStepsContext(JobManagement jobManagement, Item item) {
        this.stepContext = new StepContext(jobManagement, item)
    }

    /**
     * Adds build steps to be run before SCM checkout.
     */
    void steps(@DslContext(StepContext) Closure closure) {
        ContextHelper.executeInContext(closure, stepContext)
    }

    /**
     * Fails the build when one of the steps fails. Defaults to {@code false}.
     */
    void failOnError(boolean failOnError = true) {
        this.failOnError = failOnError
    }
}
