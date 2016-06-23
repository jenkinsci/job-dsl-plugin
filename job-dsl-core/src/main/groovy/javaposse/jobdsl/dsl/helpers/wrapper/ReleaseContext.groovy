package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.BuildParametersContext
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext
import javaposse.jobdsl.dsl.helpers.step.StepContext

class ReleaseContext extends AbstractContext {
    protected final Item item
    String releaseVersionTemplate
    boolean doNotKeepLog
    boolean overrideBuildParameters
    List<Node> params = []
    List<Node> preBuildSteps = []
    List<Node> postSuccessfulBuildSteps = []
    List<Node> postBuildSteps = []
    List<Node> postFailedBuildSteps = []
    Closure configureBlock

    ReleaseContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        this.item = item
    }

    /**
     * Adds build steps to run before the release.
     */
    void preBuildSteps(@DslContext(StepContext) Closure closure) {
        StepContext stepContext = new StepContext(jobManagement, item)
        ContextHelper.executeInContext(closure, stepContext)
        preBuildSteps.addAll(stepContext.stepNodes)
    }

    /**
     * Adds publishers to run before the release.
     *
     * @since 1.48
     */
    @RequiresPlugin(id = 'release', minimumVersion = '2.5.3')
    void preBuildPublishers(@DslContext(PublisherContext) Closure closure) {
        PublisherContext publisherContext = new PublisherContext(jobManagement, item)
        ContextHelper.executeInContext(closure, publisherContext)
        preBuildSteps.addAll(publisherContext.publisherNodes)
    }

    /**
     * Adds build steps to run after a successful release.
     */
    void postSuccessfulBuildSteps(@DslContext(StepContext) Closure closure) {
        StepContext stepContext = new StepContext(jobManagement, item)
        ContextHelper.executeInContext(closure, stepContext)
        postSuccessfulBuildSteps.addAll(stepContext.stepNodes)
    }

    /**
     * Adds publishers to run after a successful release.
     *
     * @since 1.48
     */
    @RequiresPlugin(id = 'release', minimumVersion = '2.5.3')
    void postSuccessfulBuildPublishers(@DslContext(PublisherContext) Closure closure) {
        PublisherContext publisherContext = new PublisherContext(jobManagement, item)
        ContextHelper.executeInContext(closure, publisherContext)
        postSuccessfulBuildSteps.addAll(publisherContext.publisherNodes)
    }

    /**
     * Adds build steps to run after a successful or failed release.
     */
    void postBuildSteps(@DslContext(StepContext) Closure closure) {
        StepContext stepContext = new StepContext(jobManagement, item)
        ContextHelper.executeInContext(closure, stepContext)
        postBuildSteps.addAll(stepContext.stepNodes)
    }

    /**
     * Adds publishers to run after a successful or failed release.
     *
     * @since 1.48
     */
    @RequiresPlugin(id = 'release', minimumVersion = '2.5.3')
    void postBuildPublishers(@DslContext(PublisherContext) Closure closure) {
        PublisherContext publisherContext = new PublisherContext(jobManagement, item)
        ContextHelper.executeInContext(closure, publisherContext)
        postBuildSteps.addAll(publisherContext.publisherNodes)
    }

    /**
     * Adds build steps to run after a failed release.
     */
    void postFailedBuildSteps(@DslContext(StepContext) Closure closure) {
        StepContext stepContext = new StepContext(jobManagement, item)
        ContextHelper.executeInContext(closure, stepContext)
        postFailedBuildSteps.addAll(stepContext.stepNodes)
    }

    /**
     * Adds publishers to run after a failed release.
     *
     * @since 1.48
     */
    @RequiresPlugin(id = 'release', minimumVersion = '2.5.3')
    void postFailedBuildPublishers(@DslContext(PublisherContext) Closure closure) {
        PublisherContext publisherContext = new PublisherContext(jobManagement, item)
        ContextHelper.executeInContext(closure, publisherContext)
        postFailedBuildSteps.addAll(publisherContext.publisherNodes)
    }

    /**
     * Sets a template used to tell the release process how to formulate a release version string.
     */
    void releaseVersionTemplate(String releaseVersionTemplate) {
        this.releaseVersionTemplate = releaseVersionTemplate
    }

    /**
     * If set, release builds will not be automatically kept forever. Defaults to {@code false}.
     */
    void doNotKeepLog(boolean doNotKeepLog = true) {
        this.doNotKeepLog = doNotKeepLog
    }

    /**
     * If set, a release can override the build parameters.
     */
    void overrideBuildParameters(boolean overrideBuildParameters = true) {
        this.overrideBuildParameters = overrideBuildParameters
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code ReleaseWrapper} node is passed into the configure
     * block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        this.configureBlock = configureBlock
    }

    /**
     * Add parameters for the release.
     */
    void parameters(@DslContext(BuildParametersContext) Closure parametersClosure) {
        BuildParametersContext parametersContext = new BuildParametersContext(jobManagement, item)
        ContextHelper.executeInContext(parametersClosure, parametersContext)
        params.addAll(parametersContext.buildParameterNodes.values())
    }
}
