package javaposse.jobdsl.dsl

import groovy.transform.ThreadInterrupt
import javaposse.jobdsl.dsl.helpers.ConfigFilesContext
import javaposse.jobdsl.dsl.jobs.BuildFlowJob
import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import javaposse.jobdsl.dsl.jobs.IvyJob
import javaposse.jobdsl.dsl.jobs.MatrixJob
import javaposse.jobdsl.dsl.jobs.MavenJob
import javaposse.jobdsl.dsl.jobs.MultiJob
import javaposse.jobdsl.dsl.jobs.OrganizationFolderJob
import javaposse.jobdsl.dsl.jobs.WorkflowJob
import javaposse.jobdsl.dsl.jobs.MultibranchWorkflowJob
import javaposse.jobdsl.dsl.views.BuildMonitorView
import javaposse.jobdsl.dsl.views.BuildPipelineView
import javaposse.jobdsl.dsl.views.CategorizedJobsView
import javaposse.jobdsl.dsl.views.DashboardView
import javaposse.jobdsl.dsl.views.DeliveryPipelineView
import javaposse.jobdsl.dsl.views.ListView
import javaposse.jobdsl.dsl.views.NestedView
import javaposse.jobdsl.dsl.views.SectionedView

import static javaposse.jobdsl.dsl.Preconditions.checkNotNull
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

@ThreadInterrupt
abstract class JobParent extends Script implements DslFactory {
    JobManagement jm
    Set<Item> referencedJobs = new LinkedHashSet<>()
    Set<View> referencedViews = new LinkedHashSet<>()
    @Deprecated
    Set<ConfigFile> referencedConfigFiles = new LinkedHashSet<>()
    Set<UserContent> referencedUserContents = new LinkedHashSet<>()
    List<String> queueToBuild = []

    /**
     * @since 1.30
     */
    @Override
    FreeStyleJob job(String name, @DslContext(FreeStyleJob) Closure closure = null) {
        freeStyleJob(name, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    FreeStyleJob freeStyleJob(String name, @DslContext(FreeStyleJob) Closure closure = null) {
        processItem(name, FreeStyleJob, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    @Deprecated
    BuildFlowJob buildFlowJob(String name, @DslContext(BuildFlowJob) Closure closure = null) {
        processItem(name, BuildFlowJob, closure)
    }

    /**
     * @since 1.38
     */
    @Override
    IvyJob ivyJob(String name, @DslContext(IvyJob) Closure closure = null) {
        processItem(name, IvyJob, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    MatrixJob matrixJob(String name, @DslContext(MatrixJob) Closure closure = null) {
        processItem(name, MatrixJob, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    MavenJob mavenJob(String name, @DslContext(MavenJob) Closure closure = null) {
        processItem(name, MavenJob, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    MultiJob multiJob(String name, @DslContext(MultiJob) Closure closure = null) {
        processItem(name, MultiJob, closure)
    }

    /**
     * @since 1.47
     */
    @Override
    WorkflowJob pipelineJob(String name, @DslContext(WorkflowJob) Closure closure = null) {
        processItem(name, WorkflowJob, closure)
    }

    /**
     * @since 1.47
     */
    @Override
    MultibranchWorkflowJob multibranchPipelineJob(String name,
                                                  @DslContext(MultibranchWorkflowJob) Closure closure = null) {
        processItem(name, MultibranchWorkflowJob, closure)
    }

    // this method cannot be private due to http://jira.codehaus.org/browse/GROOVY-6263
    protected <T extends Item> T processItem(String name, Class<T> jobClass, Closure closure) {
        checkNotNullOrEmpty(name, 'name must be specified')

        T job = jobClass.newInstance(jm, name)
        if (closure) {
            job.with(closure)
        }
        referencedJobs << job
        job
    }

    /**
     * @since 1.30
     */
    @Override
    ListView listView(String name, @DslContext(ListView) Closure closure = null) {
        processView(name, ListView, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    SectionedView sectionedView(String name, @DslContext(SectionedView) Closure closure = null) {
        processView(name, SectionedView, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    NestedView nestedView(String name, @DslContext(NestedView) Closure closure = null) {
        processView(name, NestedView, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    DeliveryPipelineView deliveryPipelineView(String name, @DslContext(DeliveryPipelineView) Closure closure = null) {
        jm.logPluginDeprecationWarning('delivery-pipeline-plugin', '0.10.0')
        processView(name, DeliveryPipelineView, closure)
    }

    /**
     * @since 1.58
     */
    OrganizationFolderJob organizationFolder(String name, @DslContext(OrganizationFolderJob) Closure closure = null) {
        processItem(name, OrganizationFolderJob, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    BuildPipelineView buildPipelineView(String name, @DslContext(BuildPipelineView) Closure closure = null) {
        processView(name, BuildPipelineView, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    BuildMonitorView buildMonitorView(String name, @DslContext(BuildMonitorView) Closure closure = null) {
        processView(name, BuildMonitorView, closure)
    }

    /**
     * @since 1.31
     */
    @Override
    CategorizedJobsView categorizedJobsView(String name, @DslContext(CategorizedJobsView) Closure closure = null) {
        processView(name, CategorizedJobsView, closure)
    }

    /**
     * @since 1.42
     */
    @Override
    DashboardView dashboardView(String name, @DslContext(DashboardView) Closure closure = null) {
        processView(name, DashboardView, closure)
    }

    // this method cannot be private due to http://jira.codehaus.org/browse/GROOVY-6263
    protected <T extends View> T processView(String name, Class<T> viewClass, Closure closure) {
        checkNotNullOrEmpty(name, 'name must be specified')

        T view = viewClass.newInstance(jm, name)
        if (closure) {
            view.with(closure)
        }
        referencedViews << view
        view
    }

    /**
     * @since 1.30
     */
    @Override
    Folder folder(String name, @DslContext(Folder) Closure closure = null) {
        processItem(name, Folder, closure)
    }

    /**
     * @since 1.30
     */
    @Deprecated
    ConfigFile customConfigFile(String name, @DslContext(ConfigFile) Closure closure = null) {
        processConfigFile(name, ConfigFileType.Custom, closure)
    }

    /**
     * @since 1.30
     */
    @Deprecated
    MavenSettingsConfigFile mavenSettingsConfigFile(String name, @DslContext(ConfigFile) Closure closure = null) {
        processConfigFile(name, ConfigFileType.MavenSettings, MavenSettingsConfigFile, closure)
    }

    /**
     * @since 1.39
     */
    @Deprecated
    MavenSettingsConfigFile globalMavenSettingsConfigFile(String name, @DslContext(ConfigFile) Closure closure = null) {
        processConfigFile(name, ConfigFileType.GlobalMavenSettings, MavenSettingsConfigFile, closure)
    }

    /**
     * @since 1.40
     */
    @Deprecated
    ParametrizedConfigFile managedScriptConfigFile(String name,
                                                   @DslContext(ParametrizedConfigFile) Closure closure = null) {
        processConfigFile(name, ConfigFileType.ManagedScript, ParametrizedConfigFile, closure)
    }

    // this method cannot be private due to http://jira.codehaus.org/browse/GROOVY-6263
    protected <T extends ConfigFile> T processConfigFile(String name, ConfigFileType configFileType,
                                                         Class<T> type = ConfigFile, Closure closure) {
        checkNotNullOrEmpty(name, 'name must be specified')

        T configFile = type.newInstance(configFileType, jm)
        configFile.name = name
        if (closure) {
            configFile.with(closure)
        }
        referencedConfigFiles << configFile
        configFile
    }

    @Override
    void configFiles(@DslContext(ConfigFilesContext) Closure closure) {
    }

    @Override
    void userContent(String path, InputStream content) {
        referencedUserContents << new UserContent(path, content)
    }

    /**
     * @since 1.16
     */
    @Override
    void queue(String jobName) {
        queueToBuild << jobName
    }

    /**
     * @since 1.16
     */
    @Override
    void queue(Job job) {
        checkNotNull(job, 'job must not be null')
        checkNotNullOrEmpty(job.name, 'job name must not be null or empty')
        queueToBuild << job.name
    }

    /**
     * @since 1.16
     */
    @Override
    InputStream streamFileFromWorkspace(String filePath) {
        checkNotNullOrEmpty(filePath, 'filePath must not be null or empty')
        jm.streamFileInWorkspace(filePath)
    }

    /**
     * @since 1.16
     */
    @Override
    String readFileFromWorkspace(String filePath) {
        checkNotNullOrEmpty(filePath, 'filePath must not be null or empty')
        jm.readFileInWorkspace(filePath)
    }

    /**
     * @since 1.25
     */
    @Override
    String readFileFromWorkspace(String jobName, String filePath) {
        checkNotNullOrEmpty(jobName, 'jobName must not be null or empty')
        checkNotNullOrEmpty(filePath, 'filePath must not be null or empty')
        jm.readFileInWorkspace(jobName, filePath)
    }
}
