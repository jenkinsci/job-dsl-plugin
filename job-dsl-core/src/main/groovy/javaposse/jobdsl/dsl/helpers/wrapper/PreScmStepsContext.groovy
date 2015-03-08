package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.StepContext

class PreScmStepsContext implements Context {
    final StepContext stepContext
    boolean failOnError

    PreScmStepsContext(JobManagement jobManagement) {
        this.stepContext = new StepContext(jobManagement)
    }

    void steps(@DslContext(StepContext) Closure closure) {
        ContextHelper.executeInContext(closure, stepContext)
    }

    void failOnError(boolean failOnError = true) {
        this.failOnError = failOnError
    }
}
