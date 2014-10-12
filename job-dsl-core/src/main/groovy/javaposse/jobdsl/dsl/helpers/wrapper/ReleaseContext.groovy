package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.BuildParametersContext
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.ContextHelper
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

    def preBuildSteps(Closure closure) {
        def stepContext = new StepContext(jobManagement)
        ContextHelper.executeInContext(closure, stepContext)
        preBuildSteps.addAll(stepContext.stepNodes)
    }

    def postSuccessfulBuildSteps(Closure closure) {
        def stepContext = new StepContext(jobManagement)
        ContextHelper.executeInContext(closure, stepContext)
        postSuccessfulBuildSteps.addAll(stepContext.stepNodes)
    }

    def postBuildSteps(Closure closure) {
        def stepContext = new StepContext(jobManagement)
        ContextHelper.executeInContext(closure, stepContext)
        postBuildSteps.addAll(stepContext.stepNodes)
    }

    def postFailedBuildSteps(Closure closure) {
        def stepContext = new StepContext(jobManagement)
        ContextHelper.executeInContext(closure, stepContext)
        postFailedBuildSteps.addAll(stepContext.stepNodes)
    }

    def releaseVersionTemplate(String releaseVersionTemplate) {
        this.releaseVersionTemplate = releaseVersionTemplate
    }

    def doNotKeepLog(boolean doNotKeepLog = true) {
        this.doNotKeepLog = doNotKeepLog
    }

    def overrideBuildParameters(boolean overrideBuildParameters = true) {
        this.overrideBuildParameters = overrideBuildParameters
    }

    def configure(Closure closure) {
        this.configureBlock = closure
    }

    def parameters(Closure parametersClosure) {
        BuildParametersContext parametersContext = new BuildParametersContext()
        ContextHelper.executeInContext(parametersClosure, parametersContext)
        params.addAll(parametersContext.buildParameterNodes.values())
    }
}
