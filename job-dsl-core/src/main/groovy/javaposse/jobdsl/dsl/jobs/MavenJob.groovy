package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.NoDoc
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation
import javaposse.jobdsl.dsl.helpers.publisher.MavenPublisherContext
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.dsl.helpers.triggers.MavenTriggerContext
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContext
import javaposse.jobdsl.dsl.helpers.wrapper.MavenWrapperContext
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext

import static javaposse.jobdsl.dsl.helpers.common.Threshold.THRESHOLD_COLOR_MAP
import static javaposse.jobdsl.dsl.helpers.common.Threshold.THRESHOLD_ORDINAL_MAP

class MavenJob extends Job {
    private final List<String> mavenGoals = []
    private final List<String> mavenOpts = []

    MavenJob(JobManagement jobManagement) {
        super(jobManagement)

        jobManagement.logPluginDeprecationWarning('maven-plugin', '2.3')
    }

    @Override
    @NoDoc
    void steps(@DslContext(StepContext) Closure closure) {
        throw new IllegalStateException('steps cannot be applied for Maven jobs')
    }

    @Override
    void triggers(@DslContext(MavenTriggerContext) Closure closure) {
        TriggerContext context = new MavenTriggerContext(withXmlActions, jobManagement, this)
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
    @Override
    void wrappers(@DslContext(MavenWrapperContext) Closure closure) {
        WrapperContext context = new MavenWrapperContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.wrapperNodes.each {
                project / 'buildWrappers' << it
            }
        }
    }

    @Override
    void publishers(@DslContext(MavenPublisherContext) Closure closure) {
        MavenPublisherContext context = new MavenPublisherContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            context.publisherNodes.each {
                project / 'publishers' << it
            }
        }
    }

    /**
     * Specifies the path to the root POM.
     *
     * @param rootPOM path to the root POM
     */
    void rootPOM(String rootPOM) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('rootPOM', rootPOM)
            project / node
        }
    }

    /**
     * Specifies the goals to execute including other command line options.
     * When specified multiple times, the goals and options will be concatenated.
     *
     * @param goals the goals to execute
     */
    void goals(String goals) {
        if (mavenGoals.empty) {
            withXmlActions << WithXmlAction.create { Node project ->
                Node node = methodMissing('goals', this.mavenGoals.join(' '))
                project / node
            }
        }
        mavenGoals << goals
    }

    /**
     * Specifies the JVM options needed when launching Maven as an external process.
     *
     * When specified multiple times, the options will be concatenated.
     *
     * @param mavenOpts JVM options needed when launching Maven
     */
    void mavenOpts(String mavenOpts) {
        if (this.mavenOpts.empty) {
            withXmlActions << WithXmlAction.create { Node project ->
                Node node = methodMissing('mavenOpts', this.mavenOpts.join(' '))
                project / node
            }
        }
        this.mavenOpts << mavenOpts
    }

    /**
     * If set, Jenkins  will not automatically archive all artifacts generated by this project, defaults to
     * <code>false</code>.
     *
     * @param archivingDisabled set to <code>true</code> to disable automatic archiving
     */
    void archivingDisabled(boolean archivingDisabled) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('archivingDisabled', archivingDisabled)
            project / node
        }
    }

    /**
     * If set, Jenkins  will not automatically archive all site artifacts generated by this project, defaults to
     * <code>false</code>.
     *
     * @param siteArchivingDisabled set to <code>true</code> to disable automatic archiving
     */
    void siteArchivingDisabled(boolean siteArchivingDisabled) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('siteArchivingDisabled', siteArchivingDisabled)
            project / node
        }
    }

    /**
     * If set, Jenkins  will not automatically fingerprint all artifacts generated by this project, defaults to
     * <code>false</code>.
     *
     * @param fingerprintingDisabled set to <code>true</code> to disable automatic fingerprinting
     */
    void fingerprintingDisabled(boolean fingerprintingDisabled) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('fingerprintingDisabled', fingerprintingDisabled)
            project / node
        }
    }

    /**
     * Set to allow Jenkins to configure the build process in headless mode, defaults to <code>false</code>.
     *
     * @param runHeadless set to <code>true</code> to run the build process in headless mode
     */
    void runHeadless(boolean runHeadless) {
        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('runHeadless', runHeadless)
            project / node
        }
    }

    /**
     * Set to use isolated local Maven repositories. Defaults to {@code LocalRepositoryLocation.LOCAL_TO_EXECUTOR}.
     *
     * @param location the local repository to use for isolation
     * @since 1.31
     */
    void localRepository(LocalRepositoryLocation location) {
        Preconditions.checkNotNull(location, 'localRepository can not be null')

        withXmlActions << WithXmlAction.create { Node project ->
            Node node = methodMissing('localRepository', [class: location.type])
            project / node
        }
    }

    /**
     * Adds build steps to run before the Maven execution.
     *
     * @since 1.20
     */
    void preBuildSteps(@DslContext(StepContext) Closure preBuildClosure) {
        StepContext preBuildContext = new StepContext(jobManagement, this)
        ContextHelper.executeInContext(preBuildClosure, preBuildContext)

        withXmlActions << WithXmlAction.create { Node project ->
            preBuildContext.stepNodes.each {
                project / 'prebuilders' << it
            }
        }
    }

    /**
     * Adds build steps to run after the Maven execution.
     *
     * @since 1.20
     */
    void postBuildSteps(@DslContext(StepContext) Closure postBuildClosure) {
        postBuildSteps('FAILURE', postBuildClosure)
    }

    /**
     * Adds build steps to run after the Maven execution. The steps will only run of the build result is equal or
     * better than the threshold.
     *
     * The threshold can be one of three values: {@code 'SUCCESS'}, {@code 'UNSTABLE'} or {@code 'FAILURE'}.
     *
     * @since 1.35
     */
    void postBuildSteps(String thresholdName, @DslContext(StepContext) Closure postBuildClosure) {
        Preconditions.checkArgument(
            THRESHOLD_COLOR_MAP.containsKey(thresholdName),
            "thresholdName must be one of these values ${THRESHOLD_COLOR_MAP.keySet().join(',')}"
        )

        StepContext postBuildContext = new StepContext(jobManagement, this)
        ContextHelper.executeInContext(postBuildClosure, postBuildContext)

        withXmlActions << WithXmlAction.create { Node project ->
            project / runPostStepsIfResult {
                delegate.name(thresholdName)
                ordinal(THRESHOLD_ORDINAL_MAP[thresholdName])
                color(THRESHOLD_COLOR_MAP[thresholdName])
                completeBuild(true)
            }
            postBuildContext.stepNodes.each {
                project / 'postbuilders' << it
            }
        }
    }

    /**
     * Selects which installation of Maven to use.
     *
     * @since 1.20
     */
    void mavenInstallation(String name) {
        Preconditions.checkNotNull(name, 'name can not be null')

        withXmlActions << WithXmlAction.create { Node project ->
            project / 'mavenName'(name)
        }
    }

    /**
     * Use managed Maven settings.
     *
     * @since 1.25
     */
    @RequiresPlugin(id = 'config-file-provider')
    void providedSettings(String settingsName) {
        String settingsId = jobManagement.getConfigFileId(ConfigFileType.MavenSettings, settingsName)
        Preconditions.checkNotNull(settingsId, "Managed Maven settings with name '${settingsName}' not found")

        withXmlActions << WithXmlAction.create { Node project ->
            project / settings(class: 'org.jenkinsci.plugins.configfiles.maven.job.MvnSettingsProvider') {
                settingsConfigId(settingsId)
            }
        }
    }

    /**
     * Use managed global Maven settings.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'config-file-provider')
    void providedGlobalSettings(String settingsName) {
        String settingsId = jobManagement.getConfigFileId(ConfigFileType.GlobalMavenSettings, settingsName)
        Preconditions.checkNotNull(settingsId, "Managed global Maven settings with name '${settingsName}' not found")

        withXmlActions << WithXmlAction.create { Node project ->
            project / globalSettings(class: 'org.jenkinsci.plugins.configfiles.maven.job.MvnGlobalSettingsProvider') {
                settingsConfigId(settingsId)
            }
        }
    }

    /**
     * If set, Jenkins will not automatically trigger downstream builds, defaults to <code>false</code>.
     *
     * @param disableDownstreamTrigger set to <code>true</code> to disable automatic triggering of downstream builds
     * @since 1.35
     */
    void disableDownstreamTrigger(boolean disableDownstreamTrigger = true) {
        withXmlActions << WithXmlAction.create { Node project ->
            project / 'disableTriggerDownstreamProjects'(disableDownstreamTrigger)
        }
    }
}
