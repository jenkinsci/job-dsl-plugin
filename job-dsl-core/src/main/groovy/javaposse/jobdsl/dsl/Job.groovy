package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions
import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.helpers.BuildParametersContext
import javaposse.jobdsl.dsl.helpers.JobAuthorizationContext
import javaposse.jobdsl.dsl.helpers.properties.PropertiesContext
import javaposse.jobdsl.dsl.helpers.ScmContext
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.dsl.helpers.toplevel.EnvironmentVariableContext
import javaposse.jobdsl.dsl.helpers.toplevel.LockableResourcesContext
import javaposse.jobdsl.dsl.helpers.toplevel.NotificationContext
import javaposse.jobdsl.dsl.helpers.toplevel.ThrottleConcurrentBuildsContext
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContext
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext

/**
 * DSL element representing a Jenkins job.
 */
abstract class Job extends Item {
    String templateName = null // Optional
    String previousNamesRegex = null // Optional

    protected Job(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Creates a new job configuration, based on the job template referenced by the parameter and stores this.
     *
     * @param templateName the name of the template upon which to base the new job
     * @return a new graph of groovy.util.Node objects, representing the job configuration structure
     * @throws JobTemplateMissingException
     */
    void using(String templateName) throws JobTemplateMissingException {
        Preconditions.checkState(this.templateName == null, 'Can only use "using" once')
        this.templateName = templateName
    }

    void description(String descriptionString) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('description', descriptionString)
            project / node
        }
    }

    /**
     * Renames jobs matching the regular expression (fullName) to the name of
     * this job before the configuration is updated.
     * This can be useful to keep the build history.
     *
     * @since 1.29
     */
    void previousNames(String regex) {
        this.previousNamesRegex = regex
    }

    /**
     * "Restrict where this project can be run"
     *
     * @param labelExpression Label of node to use, if null is passed in, the label is cleared out and it can roam
     * @return
     */
    void label(String labelExpression = null) {
        withXmlActions << WithXmlAction.create { Node project ->
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
     * Add environment variables to the build.
     */
    void environmentVariables(@DslContext(EnvironmentVariableContext) Closure envClosure) {
        environmentVariables(null, envClosure)
    }

    @RequiresPlugin(id = 'envinject')
    void environmentVariables(Map<Object, Object> vars,
                              @DslContext(EnvironmentVariableContext) Closure envClosure = null) {
        EnvironmentVariableContext envContext = new EnvironmentVariableContext(jobManagement)
        if (vars) {
            envContext.envs(vars)
        }
        ContextHelper.executeInContext(envClosure, envContext)

        withXmlActions << WithXmlAction.create { Node project ->
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
     * @since 1.20
     */
    @RequiresPlugin(id = 'throttle-concurrents')
    void throttleConcurrentBuilds(@DslContext(ThrottleConcurrentBuildsContext) Closure throttleClosure) {
        ThrottleConcurrentBuildsContext throttleContext = new ThrottleConcurrentBuildsContext()
        ContextHelper.executeInContext(throttleClosure, throttleContext)

        withXmlActions << WithXmlAction.create { Node project ->
            project / 'properties' / 'hudson.plugins.throttleconcurrents.ThrottleJobProperty' {
                maxConcurrentPerNode throttleContext.maxConcurrentPerNode
                maxConcurrentTotal throttleContext.maxConcurrentTotal
                throttleEnabled throttleContext.throttleDisabled ? 'false' : 'true'
                if (throttleContext.categories.isEmpty()) {
                    throttleOption 'project'
                } else {
                    throttleOption 'category'
                }
                categories {
                    throttleContext.categories.each { c ->
                        string c
                    }
                }
            }
        }
    }

    /**
     * @since 1.25
     */
    @RequiresPlugin(id = 'lockable-resources')
    void lockableResources(String resources, @DslContext(LockableResourcesContext) Closure lockClosure = null) {
        LockableResourcesContext lockContext = new LockableResourcesContext()
        ContextHelper.executeInContext(lockClosure, lockContext)

        withXmlActions << WithXmlAction.create { Node project ->
            project / 'properties' / 'org.jenkins.plugins.lockableresources.RequiredResourcesProperty' {
                resourceNames resources
                if (lockContext.resourcesVariable) {
                    resourceNamesVar lockContext.resourcesVariable
                }
                if (lockContext.resourceNumber != null) {
                    resourceNumber lockContext.resourceNumber
                }
            }
        }
    }

    void disabled(boolean shouldDisable = true) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('disabled', shouldDisable)
            project / node
        }
    }

    void logRotator(int daysToKeep = -1, int numToKeep = -1, int artifactDaysToKeep = -1, int artifactNumToKeep = -1) {
        logRotator {
            delegate.daysToKeep(daysToKeep)
            delegate.numToKeep(numToKeep)
            delegate.artifactDaysToKeep(artifactDaysToKeep)
            delegate.artifactNumToKeep(artifactNumToKeep)
        }
    }

    /**
     * @since 1.35
     */
    void logRotator(@DslContext(LogRotatorContext) Closure closure) {
        LogRotatorContext context = new LogRotatorContext()
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
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
     */
    void blockOn(Iterable<String> projectNames) {
        blockOn(projectNames.join('\n'))
    }

    /**
     * Block build if certain jobs are running.
     *
     * @param projectName Can be regular expressions. Newline delimited.
     */
    @RequiresPlugin(id = 'build-blocker-plugin')
    void blockOn(String projectName) {
        withXmlActions << WithXmlAction.create { Node project ->
            project / 'properties' / 'hudson.plugins.buildblocker.BuildBlockerProperty' {
                useBuildBlocker 'true'
                blockingJobs projectName
            }
        }
    }

    /**
     * Name of the JDK installation to use for this job.
     *
     * @param jdkArg name of the JDK installation to use for this job.
     */
    void jdk(String jdkArg) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('jdk', jdkArg)
            project / node
        }
    }

    /**
     * Priority of this job.
     * Default value is 100.
     *
     * @since 1.15
     */
    @RequiresPlugin(id = 'PrioritySorter')
    void priority(int value) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = new Node(project / 'properties', 'hudson.queueSorter.PrioritySorterJobProperty')
            node.appendNode('priority', value)
        }
    }

    /**
     * Adds a quiet period to the project.
     *
     * @param seconds number of seconds to wait
     * @since 1.16
     */
    void quietPeriod(int seconds = 5) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('quietPeriod', seconds)
            project / node
        }
    }

    /**
     * Sets the number of times the SCM checkout is retried on errors.
     *
     * @param times number of attempts
     * @since 1.16
     */
    void checkoutRetryCount(int times = 3) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('scmCheckoutRetryCount', times)
            project / node
        }
    }

    /**
     * Sets a display name for the project.
     *
     * @param displayName name to display
     * @since 1.16
     */
    void displayName(String displayName) {
        Preconditions.checkNotNull(displayName, 'Display name must not be null.')
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('displayName', displayName)
            project / node
        }
    }

    /**
     * Configures a custom workspace for the project.
     *
     * @param workspacePath workspace path to use
     * @since 1.16
     */
    void customWorkspace(String workspacePath) {
        Preconditions.checkNotNull(workspacePath, 'Workspace path must not be null')
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('customWorkspace', workspacePath)
            project / node
        }
    }

    /**
     * Configures the job to block when upstream projects are building.
     * @since 1.16
     */
    void blockOnUpstreamProjects() {
        withXmlActions << WithXmlAction.create { Node project ->
            project / blockBuildWhenUpstreamBuilding(true)
        }
    }

    /**
     * Configures the job to block when downstream projects are building.
     * @since 1.16
     */
    void blockOnDownstreamProjects() {
        withXmlActions << WithXmlAction.create { Node project ->
            project / blockBuildWhenDownstreamBuilding(true)
        }
    }

    /**
     * Configures the keep Dependencies Flag which can be set in the Fingerprinting action.
     *
     * @since 1.17
     */
    void keepDependencies(boolean keep = true) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('keepDependencies', keep)
            project / node
        }
    }

    /**
     * Configures the 'Execute concurrent builds if necessary' flag.
     *
     * @since 1.21
     */
    void concurrentBuild(boolean allowConcurrentBuild = true) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('concurrentBuild', allowConcurrentBuild)
            project / node
        }
    }

    /**
     * Configures the Notification Plugin.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'notification')
    void notifications(@DslContext(NotificationContext) Closure notificationClosure) {
        NotificationContext notificationContext = new NotificationContext(jobManagement)
        ContextHelper.executeInContext(notificationClosure, notificationContext)

        withXmlActions << WithXmlAction.create { Node project ->
            project / 'properties' / 'com.tikal.hudson.plugins.notification.HudsonNotificationProperty' {
                endpoints notificationContext.endpoints
            }
        }
    }

    /**
     * @since 1.24
     */
    @RequiresPlugin(id = 'batch-task')
    void batchTask(String name, String script) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node batchTaskProperty = project / 'properties' / 'hudson.plugins.batch__task.BatchTaskProperty'
            batchTaskProperty / 'tasks' << 'hudson.plugins.batch__task.BatchTask' {
                delegate.name name
                delegate.script script
            }
        }
    }

    /**
     * @since 1.26
     */
    @RequiresPlugin(id = 'delivery-pipeline-plugin')
    void deliveryPipelineConfiguration(String stageName, String taskName = null) {
        if (stageName || taskName) {
            withXmlActions << WithXmlAction.create { Node project ->
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

    @RequiresPlugin(id = 'matrix-auth')
    void authorization(@DslContext(JobAuthorizationContext) Closure closure) {
        if (jobManagement.getPluginVersion('matrix-auth')?.isOlderThan(new VersionNumber('1.2'))) {
            jobManagement.logDeprecationWarning(
                    'support for Matrix Authorization Strategy plugin versions older than 1.2'
            )
        }

        JobAuthorizationContext context = new JobAuthorizationContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            Node authorizationMatrixProperty = project / 'properties' / 'hudson.security.AuthorizationMatrixProperty'
            if (!jobManagement.getPluginVersion('matrix-auth')?.isOlderThan(new VersionNumber('1.2'))) {
                authorizationMatrixProperty / blocksInheritance(context.blocksInheritance)
            }
            context.permissions.each { String perm ->
                authorizationMatrixProperty.appendNode('permission', perm)
            }
        }
    }

    /**
     * @since 1.15
     */
    void parameters(@DslContext(BuildParametersContext) Closure closure) {
        BuildParametersContext context = new BuildParametersContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            Node node = project / 'properties' / 'hudson.model.ParametersDefinitionProperty' / 'parameterDefinitions'
            context.buildParameterNodes.values().each {
                node << it
            }
        }
    }

    void scm(@DslContext(ScmContext) Closure closure) {
        ScmContext context = new ScmContext(withXmlActions, jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        if (!context.scmNodes.empty) {
            Preconditions.checkState(context.scmNodes.size() == 1, 'Outside "multiscm", only one SCM can be specified')

            withXmlActions << WithXmlAction.create { Node project ->
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

    @RequiresPlugin(id = 'multiple-scms')
    void multiscm(@DslContext(ScmContext) Closure closure) {
        ScmContext context = new ScmContext(withXmlActions, jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
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

    void triggers(@DslContext(TriggerContext) Closure closure) {
        TriggerContext context = new TriggerContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.triggerNodes.each {
                project / 'triggers' << it
            }
        }
    }

    /**
     * @since 1.19
     */
    void wrappers(@DslContext(WrapperContext) Closure closure) {
        WrapperContext context = new WrapperContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.wrapperNodes.each {
                project / 'buildWrappers' << it
            }
        }
    }

    void properties(@DslContext(PropertiesContext) Closure closure) {
        PropertiesContext context = new PropertiesContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.propertiesNodes.each {
                project / 'properties' << it
            }
        }
    }

    void steps(@DslContext(StepContext) Closure closure) {
        StepContext context = new StepContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.stepNodes.each {
                project / 'builders' << it
            }
        }
    }

    void publishers(@DslContext(PublisherContext) Closure closure) {
        PublisherContext context = new PublisherContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.publisherNodes.each {
                project / 'publishers' << it
            }
        }
    }

    Node getNode() {
        Node project = templateName == null ? executeEmptyTemplate() : executeUsing()

        executeWithXmlActions(project)

        project
    }

    void executeWithXmlActions(final Node root) {
        // Create builder, based on what we already have
        withXmlActions.each { WithXmlAction withXmlClosure ->
            withXmlClosure.execute(root)
        }
    }

    private Node executeUsing() {
        String configXml
        try {
            configXml = jobManagement.getConfig(templateName)
            if (configXml == null) {
                throw new JobConfigurationNotFoundException()
            }
        } catch (JobConfigurationNotFoundException jcnfex) {
            throw new JobTemplateMissingException(templateName)
        }

        Node templateNode = new XmlParser().parse(new StringReader(configXml))
        Node emptyTemplateNode = executeEmptyTemplate()

        if (emptyTemplateNode.name() != templateNode.name()) {
            throw new JobTypeMismatchException(name, templateName)
        }

        templateNode
    }

    private Node executeEmptyTemplate() {
        new XmlParser().parse(this.class.getResourceAsStream("${this.class.simpleName}-template.xml"))
    }
}
