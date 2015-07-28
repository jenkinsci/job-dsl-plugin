package javaposse.jobdsl.dsl

import com.google.common.collect.Sets
import javaposse.jobdsl.dsl.jobs.BuildFlowJob
import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import javaposse.jobdsl.dsl.jobs.MatrixJob
import javaposse.jobdsl.dsl.jobs.MavenJob
import javaposse.jobdsl.dsl.jobs.MultiJob
import javaposse.jobdsl.dsl.jobs.WorkflowJob
import javaposse.jobdsl.dsl.views.BuildMonitorView
import javaposse.jobdsl.dsl.views.BuildPipelineView
import javaposse.jobdsl.dsl.views.CategorizedJobsView
import javaposse.jobdsl.dsl.views.DeliveryPipelineView
import javaposse.jobdsl.dsl.views.ListView
import javaposse.jobdsl.dsl.views.NestedView
import javaposse.jobdsl.dsl.views.SectionedView

import static javaposse.jobdsl.dsl.Preconditions.checkNotNull
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

abstract class JobParent extends Script implements DslFactory {
    JobManagement jm
    Set<Item> referencedJobs = Sets.newLinkedHashSet()
    Set<View> referencedViews = Sets.newLinkedHashSet()
    Set<ConfigFile> referencedConfigFiles = Sets.newLinkedHashSet()
    Set<UserContent> referencedUserContents = Sets.newLinkedHashSet()
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
        processJob(name, FreeStyleJob, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    @RequiresPlugin(id = 'build-flow-plugin')
    BuildFlowJob buildFlowJob(String name, @DslContext(BuildFlowJob) Closure closure = null) {
        processJob(name, BuildFlowJob, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    @RequiresPlugin(id = 'matrix-project')
    MatrixJob matrixJob(String name, @DslContext(MatrixJob) Closure closure = null) {
        processJob(name, MatrixJob, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    @RequiresPlugin(id = 'maven-plugin')
    MavenJob mavenJob(String name, @DslContext(MavenJob) Closure closure = null) {
        processJob(name, MavenJob, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    @RequiresPlugin(id = 'jenkins-multijob-plugin')
    MultiJob multiJob(String name, @DslContext(MultiJob) Closure closure = null) {
        processJob(name, MultiJob, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    @RequiresPlugin(id = 'workflow-aggregator')
    WorkflowJob workflowJob(String name, @DslContext(WorkflowJob) Closure closure = null) {
        processJob(name, WorkflowJob, closure)
    }

    // this method cannot be private due to http://jira.codehaus.org/browse/GROOVY-6263
    protected <T extends Job> T processJob(String name, Class<T> jobClass, Closure closure) {
        checkNotNullOrEmpty(name, 'name must be specified')

        T job = jobClass.newInstance(jm)
        job.name = name
        if (closure) {
            job.with(closure)
        }
        referencedJobs << job
        job
    }

    @Override
    @Deprecated
    Job job(Map<String, Object> arguments = [:], @DslContext(Job) Closure closure) {
        jm.logDeprecationWarning()

        Object typeArg = arguments['type'] ?: JobType.Freeform
        JobType jobType = (typeArg instanceof JobType) ? typeArg : JobType.find(typeArg)

        Job job = jobType.jobClass.newInstance(jm)
        job.with(closure)
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
    @RequiresPlugin(id = 'sectioned-view')
    SectionedView sectionedView(String name, @DslContext(SectionedView) Closure closure = null) {
        processView(name, SectionedView, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    @RequiresPlugin(id = 'nested-view')
    NestedView nestedView(String name, @DslContext(NestedView) Closure closure = null) {
        processView(name, NestedView, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    @RequiresPlugin(id = 'delivery-pipeline-plugin')
    DeliveryPipelineView deliveryPipelineView(String name, @DslContext(DeliveryPipelineView) Closure closure = null) {
        processView(name, DeliveryPipelineView, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    @RequiresPlugin(id = 'build-pipeline-plugin')
    BuildPipelineView buildPipelineView(String name, @DslContext(BuildPipelineView) Closure closure = null) {
        processView(name, BuildPipelineView, closure)
    }

    /**
     * @since 1.30
     */
    @Override
    @RequiresPlugin(id = 'build-monitor-plugin')
    BuildMonitorView buildMonitorView(String name, @DslContext(BuildMonitorView) Closure closure = null) {
        processView(name, BuildMonitorView, closure)
    }

    /**
     * @since 1.31
     */
    @Override
    @RequiresPlugin(id = 'categorized-view', minimumVersion = '1.8')
    CategorizedJobsView categorizedJobsView(String name, @DslContext(CategorizedJobsView) Closure closure = null) {
        processView(name, CategorizedJobsView, closure)
    }

    // this method cannot be private due to http://jira.codehaus.org/browse/GROOVY-6263
    protected <T extends View> T processView(String name, Class<T> viewClass, Closure closure) {
        checkNotNullOrEmpty(name, 'name must be specified')

        T view = viewClass.newInstance(jm)
        view.name = name
        if (closure) {
            view.with(closure)
        }
        referencedViews << view
        view
    }

    /**
     * @since 1.21
     */
    @Override
    @Deprecated
    View view(Map<String, Object> arguments = [:], @DslContext(View) Closure closure) {
        jm.logDeprecationWarning()

        ViewType viewType = arguments['type'] as ViewType ?: ViewType.ListView

        View view = viewType.viewClass.newInstance(jm)
        view.with(closure)
        referencedViews << view
        view
    }

    /**
     * @since 1.23
     */
    @Override
    @Deprecated
    @RequiresPlugin(id = 'cloudbees-folder')
    Folder folder(@DslContext(Folder) Closure closure) {
        jm.logDeprecationWarning()

        Folder folder = new Folder(jm)
        folder.with(closure)
        referencedJobs << folder
        folder
    }

    /**
     * @since 1.30
     */
    @Override
    @RequiresPlugin(id = 'cloudbees-folder')
    Folder folder(String name, @DslContext(Folder) Closure closure = null) {
        checkNotNullOrEmpty(name, 'name must be specified')

        Folder folder = new Folder(jm)
        folder.name = name
        if (closure) {
            folder.with(closure)
        }
        referencedJobs << folder
        folder
    }

    /**
     * @since 1.30
     */
    @RequiresPlugin(id = 'config-file-provider')
    ConfigFile customConfigFile(String name, @DslContext(ConfigFile) Closure closure = null) {
        processConfigFile(name, ConfigFileType.Custom, closure)
    }

    /**
     * @since 1.30
     */
    @RequiresPlugin(id = 'config-file-provider')
    ConfigFile mavenSettingsConfigFile(String name, @DslContext(ConfigFile) Closure closure = null) {
        processConfigFile(name, ConfigFileType.MavenSettings, closure)
    }

    /**
     * @since 1.25
     */
    @Override
    @Deprecated
    @RequiresPlugin(id = 'config-file-provider')
    ConfigFile configFile(Map<String, Object> arguments = [:], @DslContext(ConfigFile) Closure closure) {
        jm.logDeprecationWarning()

        ConfigFileType configFileType = arguments['type'] as ConfigFileType ?: ConfigFileType.Custom

        ConfigFile configFile = new ConfigFile(configFileType, jm)
        configFile.with(closure)
        referencedConfigFiles << configFile
        configFile
    }

    // this method cannot be private due to http://jira.codehaus.org/browse/GROOVY-6263
    protected ConfigFile processConfigFile(String name, ConfigFileType configFileType, Closure closure) {
        checkNotNullOrEmpty(name, 'name must be specified')

        ConfigFile configFile = new ConfigFile(configFileType, jm)
        configFile.name = name
        if (closure) {
            configFile.with(closure)
        }
        referencedConfigFiles << configFile
        configFile
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
