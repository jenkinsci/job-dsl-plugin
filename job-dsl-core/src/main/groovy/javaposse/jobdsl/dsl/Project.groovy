package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.helpers.ScmContext
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.dsl.helpers.toplevel.LockableResourcesContext
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNull
import static javaposse.jobdsl.dsl.Preconditions.checkState

abstract class Project extends Job {
    protected Project(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Label which specifies which nodes this job can run on. If {@code null} is passed in, the label is cleared out and
     * the job can roam.
     */
    void label(String labelExpression = null) {
        configure { Node project ->
            if (labelExpression) {
                project / assignedNode(labelExpression)
                project / canRoam(false) // If canRoam is true, the label will not be used
            } else {
                project / assignedNode('')
                project / canRoam(true)
            }
        }
    }

    /**
     * Locks resources while a job is running.
     *
     * @since 1.25
     */
    @RequiresPlugin(id = 'lockable-resources', minimumVersion = '1.7')
    void lockableResources(String resources, @DslContext(LockableResourcesContext) Closure lockClosure = null) {
        LockableResourcesContext lockContext = new LockableResourcesContext(jobManagement)
        ContextHelper.executeInContext(lockClosure, lockContext)

        checkArgument(resources || lockContext.label, 'Either resource or label have to be specified')

        configure { Node project ->
            project / 'properties' / 'org.jenkins.plugins.lockableresources.RequiredResourcesProperty' {
                if (resources) {
                    resourceNames(resources)
                }
                if (lockContext.resourcesVariable) {
                    resourceNamesVar(lockContext.resourcesVariable)
                }
                if (lockContext.label) {
                    labelName(lockContext.label)
                }
                if (lockContext.resourceNumber != null) {
                    resourceNumber(lockContext.resourceNumber)
                }
            }
        }
    }

    /**
     * Locks resources while a job is running.
     *
     * @since 1.44
     */
    @RequiresPlugin(id = 'lockable-resources', minimumVersion = '1.7')
    void lockableResources(@DslContext(LockableResourcesContext) Closure lockClosure = null) {
        lockableResources(null, lockClosure)
    }

    /**
     * Specifies the number of executors to block for this job.
     *
     * @since 1.36
     */
    @RequiresPlugin(id = 'heavy-job', minimumVersion = '1.1')
    void weight(int weight) {
        configure { Node project ->
            Node node = methodMissing('weight', weight)
            project / 'properties' / 'hudson.plugins.heavy__job.HeavyJobProperty' / node
        }
    }

    /**
     * Name of the JDK installation to use for this job. The name must match the name of a JDK installation defined in
     * the Jenkins system configuration. The default JDK will be used when the jdk method is omitted.
     */
    void jdk(String jdk) {
        configure { Node project ->
            Node node = methodMissing('jdk', jdk)
            project / node
        }
    }

    /**
     * Sets the number of times the SCM checkout is retried on errors.
     *
     * @since 1.16
     */
    void checkoutRetryCount(int times = 3) {
        configure { Node project ->
            Node node = methodMissing('scmCheckoutRetryCount', times)
            project / node
        }
    }

    /**
     * Defines that a project should use the given directory as a workspace instead of the default workspace location.
     *
     * @since 1.16
     */
    void customWorkspace(String workspacePath) {
        checkNotNull(workspacePath, 'Workspace path must not be null')
        configure { Node project ->
            Node node = methodMissing('customWorkspace', workspacePath)
            project / node
        }
    }

    /**
     * Configures the job to block when upstream projects are building.
     *
     * @since 1.16
     */
    void blockOnUpstreamProjects() {
        configure { Node project ->
            project / blockBuildWhenUpstreamBuilding(true)
        }
    }

    /**
     * Configures the job to block when downstream projects are building.
     *
     * @since 1.16
     */
    void blockOnDownstreamProjects() {
        configure { Node project ->
            project / blockBuildWhenDownstreamBuilding(true)
        }
    }

    /**
     * Allows Jenkins to schedule and execute multiple builds concurrently.
     *
     * @since 1.21
     */
    void concurrentBuild(boolean allowConcurrentBuild = true) {
        configure { Node project ->
            Node node = methodMissing('concurrentBuild', allowConcurrentBuild)
            project / node
        }
    }

    /**
     * Adds batch tasks that are not regularly executed to projects, such as releases, integration, archiving.
     * Can be called multiple times to add more batch tasks.
     *
     * @since 1.24
     */
    @RequiresPlugin(id = 'batch-task')
    void batchTask(String name, String script) {
        configure { Node project ->
            Node batchTaskProperty = project / 'properties' / 'hudson.plugins.batch__task.BatchTaskProperty'
            batchTaskProperty / 'tasks' << 'hudson.plugins.batch__task.BatchTask' {
                delegate.name name
                delegate.script script
            }
        }
    }

    /**
     * Allows a job to check out sources from an SCM provider.
     */
    void scm(@DslContext(ScmContext) Closure closure) {
        ScmContext context = new ScmContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        if (!context.scmNodes.empty) {
            checkState(context.scmNodes.size() == 1, 'Outside "multiscm", only one SCM can be specified')

            configure { Node project ->
                Node scm = project / scm
                if (scm) {
                    // There can only be only one SCM, so remove if there
                    project.remove(scm)
                }

                // Assuming append the only child
                project << context.scmNodes[0]
            }
        }
    }

    /**
     * Allows a job to check out sources from multiple SCM providers.
     */
    @RequiresPlugin(id = 'multiple-scms')
    void multiscm(@DslContext(ScmContext) Closure closure) {
        ScmContext context = new ScmContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            Node scm = project / scm
            if (scm) {
                // There can only be only one SCM, so remove if there
                project.remove(scm)
            }

            Node multiscmNode = new NodeBuilder().scm(class: 'org.jenkinsci.plugins.multiplescms.MultiSCM')
            Node scmsNode = multiscmNode / scms
            context.scmNodes.each {
                scmsNode << it
            }

            // Assuming append the only child
            project << multiscmNode
        }
    }

    /**
     * Adds pre/post actions to the job.
     *
     * @since 1.19
     */
    void wrappers(@DslContext(WrapperContext) Closure closure) {
        WrapperContext context = new WrapperContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            context.wrapperNodes.each {
                project / 'buildWrappers' << it
            }
        }
    }

    /**
     * Adds build steps to the jobs.
     */
    void steps(@DslContext(StepContext) Closure closure) {
        StepContext context = new StepContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            context.stepNodes.each {
                project / 'builders' << it
            }
        }
    }

    /**
     * Adds post-build actions to the job.
     */
    void publishers(@DslContext(PublisherContext) Closure closure) {
        PublisherContext context = new PublisherContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            context.publisherNodes.each {
                project / 'publishers' << it
            }
        }
    }
}
