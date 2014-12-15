package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.BuildParametersContext
import javaposse.jobdsl.dsl.helpers.step.StepContext

class ReleaseContext implements Context {
    private final JobManagement jobManagement

    String releaseVersionTemplate
    boolean doNotKeepLog
    boolean overrideBuildParameters
    List<Node> params = []
    List<Node> preBuildSteps = []
    List<Node> postSuccessfulBuildSteps = []
    List<Node> postBuildSteps = []
    List<Node> postFailedBuildSteps = []
    Closure configureBlock

    ReleaseContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void preBuildSteps(@DslContext(StepContext) Closure closure) {
        StepContext stepContext = new StepContext(jobManagement)
        ContextHelper.executeInContext(closure, stepContext)
        preBuildSteps.addAll(stepContext.stepNodes)
    }

    void postSuccessfulBuildSteps(@DslContext(StepContext) Closure closure) {
        StepContext stepContext = new StepContext(jobManagement)
        ContextHelper.executeInContext(closure, stepContext)
        postSuccessfulBuildSteps.addAll(stepContext.stepNodes)
    }

    void postBuildSteps(@DslContext(StepContext) Closure closure) {
        StepContext stepContext = new StepContext(jobManagement)
        ContextHelper.executeInContext(closure, stepContext)
        postBuildSteps.addAll(stepContext.stepNodes)
    }

    void postFailedBuildSteps(@DslContext(StepContext) Closure closure) {
        StepContext stepContext = new StepContext(jobManagement)
        ContextHelper.executeInContext(closure, stepContext)
        postFailedBuildSteps.addAll(stepContext.stepNodes)
    }

    void releaseVersionTemplate(String releaseVersionTemplate) {
        this.releaseVersionTemplate = releaseVersionTemplate
    }

    void doNotKeepLog(boolean doNotKeepLog = true) {
        this.doNotKeepLog = doNotKeepLog
    }

    void overrideBuildParameters(boolean overrideBuildParameters = true) {
        this.overrideBuildParameters = overrideBuildParameters
    }

    void configure(Closure closure) {
        this.configureBlock = closure
    }

    void parameters(@DslContext(BuildParametersContext) Closure parametersClosure) {
        BuildParametersContext parametersContext = new BuildParametersContext()
        ContextHelper.executeInContext(parametersClosure, parametersContext)
        params.addAll(parametersContext.buildParameterNodes.values())
    }
}
