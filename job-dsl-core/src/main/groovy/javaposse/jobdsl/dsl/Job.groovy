package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.helpers.BuildParametersContext
import javaposse.jobdsl.dsl.helpers.JobAuthorizationContext
import javaposse.jobdsl.dsl.helpers.ScmContext
import javaposse.jobdsl.dsl.helpers.properties.PropertiesContext
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.dsl.helpers.toplevel.EnvironmentVariableContext
import javaposse.jobdsl.dsl.helpers.toplevel.LockableResourcesContext
import javaposse.jobdsl.dsl.helpers.toplevel.NotificationContext
import javaposse.jobdsl.dsl.helpers.toplevel.ThrottleConcurrentBuildsContext
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContext
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext
import javaposse.jobdsl.dsl.jobs.MatrixJob

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNull
import static javaposse.jobdsl.dsl.Preconditions.checkState

/**
 * DSL element representing a Jenkins job.
 */
abstract class Job extends Item {
    String templateName = null // Optional
    String previousNamesRegex = null // Optional

    protected Job(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Creates a new job configuration, based on the job template referenced by the parameter and stores this.
     *
     * When the template is changed, the seed job will attempt to re-run, which has the side-effect of cascading changes
     * of the template the jobs generated from it.
     */
    void using(String templateName) throws JobTemplateMissingException {
        checkArgument(this.templateName == null, 'Can only use "using" once')
        this.templateName = templateName
    }

    /**
     * Sets a description for the job.
     */
    void description(String descriptionString) {
        configure { Node project ->
            Node node = methodMissing('description', descriptionString)
            project / node
        }
    }

    /**
     * Renames jobs matching the regular expression to the name of this job before the configuration is updated.
     * The regular expression needs to match the full name of the job, i.e. with folders included.
     * This can be useful to keep the build history.
     *
     * @since 1.29
     */
    void previousNames(String regex) {
        this.previousNamesRegex = regex
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
     * Adds environment variables to the build.
     */
    void environmentVariables(@DslContext(EnvironmentVariableContext) Closure envClosure) {
        environmentVariables(null, envClosure)
    }

    /**
     * Adds environment variables to the build.
     */
    @RequiresPlugin(id = 'envinject')
    void environmentVariables(Map<Object, Object> vars,
                              @DslContext(EnvironmentVariableContext) Closure envClosure = null) {
        EnvironmentVariableContext envContext = new EnvironmentVariableContext(jobManagement, this)
        if (vars) {
            envContext.envs(vars)
        }
        ContextHelper.executeInContext(envClosure, envContext)

        configure { Node project ->
            project / 'properties' / 'EnvInjectJobProperty' {
                envContext.addInfoToBuilder(delegate)
                on(true)
                keepJenkinsSystemVariables(envContext.keepSystemVariables)
                keepBuildVariables(envContext.keepBuildVariables)
                overrideBuildParameters(envContext.overrideBuildParameters)
                contributors().children().addAll(envContext.contributorsContext.contributors)
            }
        }
    }

    /**
     * Throttles the number of concurrent builds of a project running per node or globally.
     *
     * @since 1.20
     */
    @RequiresPlugin(id = 'throttle-concurrents')
    void throttleConcurrentBuilds(@DslContext(ThrottleConcurrentBuildsContext) Closure throttleClosure) {
        ThrottleConcurrentBuildsContext throttleContext = new ThrottleConcurrentBuildsContext(jobManagement, this)
        ContextHelper.executeInContext(throttleClosure, throttleContext)

        configure { Node project ->
            project / 'properties' / 'hudson.plugins.throttleconcurrents.ThrottleJobProperty' {
                maxConcurrentPerNode(throttleContext.maxConcurrentPerNode)
                maxConcurrentTotal(throttleContext.maxConcurrentTotal)
                throttleEnabled(!throttleContext.throttleDisabled)
                throttleOption(throttleContext.categories.empty ? 'project' : 'category')
                categories {
                    throttleContext.categories.each { c ->
                        string(c)
                    }
                }
                if (jobManagement.isMinimumPluginVersionInstalled('throttle-concurrents', '1.8.3')
                        && this instanceof MatrixJob) {
                    matrixOptions {
                        throttleMatrixBuilds(throttleContext.throttleMatrixBuilds)
                        throttleMatrixConfigurations(throttleContext.throttleMatrixConfigurations)
                    }
                }
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
     * Disables the job, so that no new builds will be executed until the project is re-enabled.
     */
    void disabled(boolean shouldDisable = true) {
        configure { Node project ->
            Node node = methodMissing('disabled', shouldDisable)
            project / node
        }
    }

    /**
     * Manages how long to keep records of the builds.
     */
    void logRotator(int daysToKeep = -1, int numToKeep = -1, int artifactDaysToKeep = -1, int artifactNumToKeep = -1) {
        logRotator {
            delegate.daysToKeep(daysToKeep)
            delegate.numToKeep(numToKeep)
            delegate.artifactDaysToKeep(artifactDaysToKeep)
            delegate.artifactNumToKeep(artifactNumToKeep)
        }
    }

    /**
     * Manages how long to keep records of the builds.
     *
     * @since 1.35
     */
    void logRotator(@DslContext(LogRotatorContext) Closure closure) {
        LogRotatorContext context = new LogRotatorContext()
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            Node node = methodMissing('logRotator') {
                daysToKeep(context.daysToKeep)
                numToKeep(context.numToKeep)
                artifactDaysToKeep(context.artifactDaysToKeep)
                artifactNumToKeep(context.artifactNumToKeep)
            }
            project / node
        }
    }

    /**
     * Block build if certain jobs are running.
     *
     * @see #blockOn(java.lang.String, groovy.lang.Closure)
     */
    void blockOn(Iterable<String> projectNames) {
        blockOn(projectNames, null)
    }

    /**
     * Block build if certain jobs are running.
     *
     * @since 1.36
     * @see #blockOn(java.lang.String, groovy.lang.Closure)
     */
    void blockOn(Iterable<String> projectNames, @DslContext(BuildBlockerContext) Closure closure) {
        blockOn(projectNames.collect().join('\n'), closure)
    }

    /**
     * Block build if certain jobs are running.
     *
     * @see #blockOn(java.lang.String, groovy.lang.Closure)
     */
    void blockOn(String projectName) {
        blockOn(projectName, null)
    }

    /**
     * Block build if certain jobs are running.
     *
     * Regular expressions can be used for the project names, e.g. {@code /.*-maintenance/} will match all maintenance
     * jobs.
     *
     * @since 1.36
     */
    @RequiresPlugin(id = 'build-blocker-plugin', minimumVersion = '1.7.1')
    void blockOn(String projectName, @DslContext(BuildBlockerContext) Closure closure) {
        BuildBlockerContext context = new BuildBlockerContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            project / 'properties' / 'hudson.plugins.buildblocker.BuildBlockerProperty' {
                useBuildBlocker(true)
                blockingJobs(projectName)
                blockLevel(context.blockLevel)
                scanQueueFor(context.scanQueueFor)
            }
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
     * Defines a timespan (in seconds) to wait for additional events (pushes, check-ins) before triggering a build.
     *
     * @since 1.16
     */
    void quietPeriod(int seconds = 5) {
        configure { Node project ->
            Node node = methodMissing('quietPeriod', seconds)
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
     * Sets a display name for the project.
     *
     * @since 1.16
     */
    void displayName(String displayName) {
        checkNotNull(displayName, 'Display name must not be null.')
        configure { Node project ->
            Node node = methodMissing('displayName', displayName)
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
     * Protects all builds that are referenced from builds of this project (via fingerprint) from log rotation.
     *
     * @since 1.17
     */
    void keepDependencies(boolean keep = true) {
        configure { Node project ->
            Node node = methodMissing('keepDependencies', keep)
            project / node
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
     * Compresses the log file after build completion.
     *
     * @since 1.36
     */
    @RequiresPlugin(id = 'compress-buildlog', minimumVersion = '1.0')
    void compressBuildLog() {
        configure { Node project ->
            project / 'properties' / 'org.jenkinsci.plugins.compressbuildlog.BuildLogCompressor'
        }
    }

    /**
     * Configures notifications for the build.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'notification', minimumVersion = '1.8')
    void notifications(@DslContext(NotificationContext) Closure notificationClosure) {
        NotificationContext notificationContext = new NotificationContext(jobManagement)
        ContextHelper.executeInContext(notificationClosure, notificationContext)

        configure { Node project ->
            project / 'properties' / 'com.tikal.hudson.plugins.notification.HudsonNotificationProperty' {
                endpoints notificationContext.endpoints
            }
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
     * Sets the stage name and task name for the delivery pipeline view. Each of the parameters can be set to
     * {@code null} to use the job name as stage or task name.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'delivery-pipeline-plugin')
    void deliveryPipelineConfiguration(String stageName, String taskName = null) {
        jobManagement.logPluginDeprecationWarning('delivery-pipeline-plugin', '0.10.0')
        if (stageName || taskName) {
            configure { Node project ->
                project / 'properties' / 'se.diabol.jenkins.pipeline.PipelineProperty' {
                    if (taskName) {
                        delegate.taskName(taskName)
                    }
                    if (stageName) {
                        delegate.stageName(stageName)
                    }
                }
            }
        }
    }

    /**
     * Provide an authorization token in the form of a string so that only those who know it would be able to remotely
     * trigger this project's builds. Global security must be enabled to trigger builds remotely.
     *
     * For security reasons, do not use a hard-coded token. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     *
     * @since 1.39
     */
    void authenticationToken(String token) {
        configure { Node project ->
            project / authToken(token)
        }
    }

    /**
     * Creates permission records.
     */
    @RequiresPlugin(id = 'matrix-auth', minimumVersion = '1.2')
    void authorization(@DslContext(JobAuthorizationContext) Closure closure) {
        JobAuthorizationContext context = new JobAuthorizationContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            Node authorizationMatrixProperty = project / 'properties' / 'hudson.security.AuthorizationMatrixProperty'
            authorizationMatrixProperty / blocksInheritance(context.blocksInheritance)
            context.permissions.each { String perm ->
                authorizationMatrixProperty.appendNode('permission', perm)
            }
        }
    }

    /**
     * Allows to parameterize the job.
     *
     * @since 1.15
     */
    void parameters(@DslContext(BuildParametersContext) Closure closure) {
        BuildParametersContext context = new BuildParametersContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            Node node = project / 'properties' / 'hudson.model.ParametersDefinitionProperty' / 'parameterDefinitions'
            context.buildParameterNodes.values().each {
                node << it
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
     * Adds build triggers to the job.
     */
    void triggers(@DslContext(TriggerContext) Closure closure) {
        TriggerContext context = new TriggerContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            context.triggerNodes.each {
                project / 'triggers' << it
            }
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
     * Adds custom properties to the job.
     */
    void properties(@DslContext(PropertiesContext) Closure closure) {
        PropertiesContext context = new PropertiesContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            context.propertiesNodes.each {
                project / 'properties' << it
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

    @Override
    protected Node getNodeTemplate() {
        templateName == null ? super.nodeTemplate : executeUsing()
    }

    private Node executeUsing() {
        String configXml
        try {
            configXml = jobManagement.getConfig(templateName)
        } catch (JobConfigurationNotFoundException ignore) {
            throw new JobTemplateMissingException(templateName)
        }

        Node templateNode = new XmlParser().parse(new StringReader(configXml))
        Node emptyTemplateNode = super.nodeTemplate

        if (emptyTemplateNode.name() != templateNode.name()) {
            throw new JobTypeMismatchException(name, templateName)
        }

        templateNode
    }
}
