package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.helpers.AuthorizationContext
import javaposse.jobdsl.dsl.helpers.BuildParametersContext
import javaposse.jobdsl.dsl.helpers.Permissions
import javaposse.jobdsl.dsl.helpers.PromotionContext
import javaposse.jobdsl.dsl.helpers.ScmContext
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.dsl.helpers.toplevel.EnvironmentVariableContext
import javaposse.jobdsl.dsl.helpers.toplevel.LockableResourcesContext
import javaposse.jobdsl.dsl.helpers.toplevel.NotificationContext
import javaposse.jobdsl.dsl.helpers.toplevel.ThrottleConcurrentBuildsContext
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContext
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext
import javaposse.jobdsl.dsl.jobs.Promotion

/**
 * DSL element representing a Jenkins job.
 */
abstract class Job extends Item {
    String templateName = null // Optional
    String previousNamesRegex = null // Optional
    private final List<Promotion> promotions = []

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

    @Deprecated
    void name(Closure nameClosure) {
        jobManagement.logDeprecationWarning()
        name(nameClosure.call().toString())
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

    void logRotator(int daysToKeepInt = -1, int numToKeepInt = -1,
                   int artifactDaysToKeepInt = -1, int artifactNumToKeepInt = -1) {
        withXmlActions << WithXmlAction.create { Node project ->
            project / logRotator {
                daysToKeep daysToKeepInt
                numToKeep numToKeepInt
                artifactDaysToKeep artifactDaysToKeepInt
                artifactNumToKeep artifactNumToKeepInt
            }
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
     * Priority of this job. Requires the
     * <a href="https://wiki.jenkins-ci.org/display/JENKINS/Priority+Sorter+Plugin">Priority Sorter Plugin</a>.
     * Default value is 100.
     */
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
     */
    void blockOnUpstreamProjects() {
        withXmlActions << WithXmlAction.create { Node project ->
            project / blockBuildWhenUpstreamBuilding(true)
        }
    }

    /**
     * Configures the job to block when downstream projects are building.
     */
    void blockOnDownstreamProjects() {
        withXmlActions << WithXmlAction.create { Node project ->
            project / blockBuildWhenDownstreamBuilding(true)
        }
    }

    /**
     * Configures the keep Dependencies Flag which can be set in the Fingerprinting action.
     */
    void keepDependencies(boolean keep = true) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('keepDependencies', keep)
            project / node
        }
    }

    /**
     * Configures the 'Execute concurrent builds if necessary' flag.
     */
    void concurrentBuild(boolean allowConcurrentBuild = true) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('concurrentBuild', allowConcurrentBuild)
            project / node
        }
    }

    /**
     * Configures the Notification Plugin.
     */
    void notifications(@DslContext(NotificationContext) Closure notificationClosure) {
        NotificationContext notificationContext = new NotificationContext(jobManagement)
        ContextHelper.executeInContext(notificationClosure, notificationContext)

        withXmlActions << WithXmlAction.create { Node project ->
            project / 'properties' / 'com.tikal.hudson.plugins.notification.HudsonNotificationProperty' {
                endpoints notificationContext.endpoints
            }
        }
    }

    void batchTask(String name, String script) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node batchTaskProperty = project / 'properties' / 'hudson.plugins.batch__task.BatchTaskProperty'
            batchTaskProperty / 'tasks' << 'hudson.plugins.batch__task.BatchTask' {
                delegate.name name
                delegate.script script
            }
        }
    }

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

    void authorization(@DslContext(AuthorizationContext) Closure closure) {
        AuthorizationContext context = new AuthorizationContext()
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            Node authorizationMatrixProperty = project / 'properties' / 'hudson.security.AuthorizationMatrixProperty'
            context.permissions.each { String perm ->
                authorizationMatrixProperty.appendNode('permission', perm)
            }
        }
    }

    @Deprecated
    void permission(String permission) {
        jobManagement.logDeprecationWarning()

        authorization {
            delegate.permission(permission)
        }
    }

    @Deprecated
    void permission(Permissions permission, String user) {
        jobManagement.logDeprecationWarning()

        authorization {
            delegate.permission(permission, user)
        }
    }

    @Deprecated
    void permission(String permissionEnumName, String user) {
        jobManagement.logDeprecationWarning()

        authorization {
            delegate.permission(permissionEnumName, user)
        }
    }

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
        ScmContext context = new ScmContext(false, withXmlActions, jobManagement)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            Node scm = project / scm
            if (scm) {
                // There can only be only one SCM, so remove if there
                project.remove(scm)
            }

            // Assuming append the only child
            project << context.scmNode
        }
    }

    void multiscm(@DslContext(ScmContext) Closure closure) {
        ScmContext context = new ScmContext(true, withXmlActions, jobManagement)
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
        TriggerContext context = new TriggerContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.triggerNodes.each {
                project / 'triggers' << it
            }
        }
    }

    void wrappers(@DslContext(WrapperContext) Closure closure) {
        WrapperContext context = new WrapperContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.wrapperNodes.each {
                project / 'buildWrappers' << it
            }
        }
    }

    void steps(@DslContext(StepContext) Closure closure) {
        StepContext context = new StepContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.stepNodes.each {
                project / 'builders' << it
            }
        }
    }

    void publishers(@DslContext(PublisherContext) Closure closure) {
        PublisherContext context = new PublisherContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.publisherNodes.each {
                project / 'publishers' << it
            }
        }
    }

    void promotion(String name, @DslContext(PromotionContext) Closure closure) {
        PromotionContext context = new PromotionContext(jobManagement, name)
        ContextHelper.executeInContext(closure, context)
        promotions.add(context.createPromotion())

        withXmlActions << WithXmlAction.create { Node project ->
            project / 'properties' / 'hudson.plugins.promoted__builds.JobPropertyImpl' / 'activeProcessNames' {
                string context.name
            }
        }
    }

    void providedSettings(String settingsName) {
        String settingsId = jobManagement.getConfigFileId(ConfigFileType.MavenSettings, settingsName)
        Preconditions.checkNotNull(settingsId, "Managed Maven settings with name '${settingsName}' not found")

        withXmlActions << WithXmlAction.create { Node project ->
            project / settings(class: 'org.jenkinsci.plugins.configfiles.maven.job.MvnSettingsProvider') {
                settingsConfigId(settingsId)
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
        loadJobTemplate()
    }

    List<Promotion> getPromotions() {
        Collections.unmodifiableList(promotions)
    }
}
