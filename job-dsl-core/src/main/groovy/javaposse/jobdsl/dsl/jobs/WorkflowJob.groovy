package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.ScmContext
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.dsl.helpers.toplevel.LockableResourcesContext
import javaposse.jobdsl.dsl.helpers.workflow.WorkflowDefinitionContext
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext

class WorkflowJob extends Job {
    WorkflowJob(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Adds a workflow definition.
     */
    void definition(@DslContext(WorkflowDefinitionContext) Closure definitionClosure) {
        WorkflowDefinitionContext context = new WorkflowDefinitionContext(jobManagement, this)
        ContextHelper.executeInContext(definitionClosure, context)

        configure { Node project ->
            Node definition = project / definition
            if (definition) {
                project.remove(definition)
            }
            project << context.definitionNode
        }
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void label(String labelExpression = null) {
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void lockableResources(String resources, @DslContext(LockableResourcesContext) Closure lockClosure = null) {
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void lockableResources(@DslContext(LockableResourcesContext) Closure lockClosure = null) {
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void weight(int weight) {
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void jdk(String jdk) {
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void checkoutRetryCount(int times = 3) {
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void customWorkspace(String workspacePath) {
    }

    @Deprecated
    @SuppressWarnings('EmptyMethod')
    void blockOnUpstreamProjects() {
    }

    @Deprecated
    @SuppressWarnings('EmptyMethod')
    void blockOnDownstreamProjects() {
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void concurrentBuild(boolean allowConcurrentBuild = true) {
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void batchTask(String name, String script) {
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void scm(@DslContext(ScmContext) Closure closure) {
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void multiscm(@DslContext(ScmContext) Closure closure) {
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void wrappers(@DslContext(WrapperContext) Closure closure) {
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void steps(@DslContext(StepContext) Closure closure) {
    }

    @Deprecated
    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void publishers(@DslContext(PublisherContext) Closure closure) {
    }
}
