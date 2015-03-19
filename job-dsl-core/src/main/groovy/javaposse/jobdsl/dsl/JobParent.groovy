package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import com.google.common.collect.Sets
import javaposse.jobdsl.dsl.jobs.BuildFlowJob
import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import javaposse.jobdsl.dsl.jobs.MatrixJob
import javaposse.jobdsl.dsl.jobs.MavenJob
import javaposse.jobdsl.dsl.jobs.MultiJob
import javaposse.jobdsl.dsl.jobs.WorkflowJob
import javaposse.jobdsl.dsl.views.BuildMonitorView
import javaposse.jobdsl.dsl.views.BuildPipelineView
import javaposse.jobdsl.dsl.views.DeliveryPipelineView
import javaposse.jobdsl.dsl.views.ListView
import javaposse.jobdsl.dsl.views.NestedView
import javaposse.jobdsl.dsl.views.SectionedView

abstract class JobParent extends Script implements DslFactory {
    JobManagement jm
    Set<Item> referencedJobs = Sets.newLinkedHashSet()
    Set<View> referencedViews = Sets.newLinkedHashSet()
    Set<ConfigFile> referencedConfigFiles = Sets.newLinkedHashSet()
    List<String> queueToBuild = []

    @Override
    FreeStyleJob job(String name, @DslContext(FreeStyleJob) Closure closure) {
        freeStyleJob(name, closure)
    }

    @Override
    FreeStyleJob freeStyleJob(String name, @DslContext(FreeStyleJob) Closure closure) {
        processJob(name, FreeStyleJob, closure)
    }

    @Override
    BuildFlowJob buildFlowJob(String name, @DslContext(BuildFlowJob) Closure closure) {
        processJob(name, BuildFlowJob, closure)
    }

    @Override
    MatrixJob matrixJob(String name, @DslContext(MatrixJob) Closure closure) {
        processJob(name, MatrixJob, closure)
    }

    @Override
    MavenJob mavenJob(String name, @DslContext(MavenJob) Closure closure) {
        processJob(name, MavenJob, closure)
    }

    @Override
    MultiJob multiJob(String name, @DslContext(MultiJob) Closure closure) {
        processJob(name, MultiJob, closure)
    }

    @Override
    WorkflowJob workflowJob(String name, @DslContext(WorkflowJob) Closure closure) {
        processJob(name, WorkflowJob, closure)
    }

    // this method cannot be private due to http://jira.codehaus.org/browse/GROOVY-6263
    protected <T extends Job> T processJob(String name, Class<T> jobClass, Closure closure) {
        T job = jobClass.newInstance(jm)
        job.name = name
        job.with(closure)
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

    @Override
    ListView listView(String name, @DslContext(ListView) Closure closure) {
        processView(name, ListView, closure)
    }

    @Override
    SectionedView sectionedView(String name, @DslContext(SectionedView) Closure closure) {
        processView(name, SectionedView, closure)
    }

    @Override
    NestedView nestedView(String name, @DslContext(NestedView) Closure closure) {
        processView(name, NestedView, closure)
    }

    @Override
    DeliveryPipelineView deliveryPipelineView(String name, @DslContext(DeliveryPipelineView) Closure closure) {
        processView(name, DeliveryPipelineView, closure)
    }

    @Override
    BuildPipelineView buildPipelineView(String name, @DslContext(BuildPipelineView) Closure closure) {
        processView(name, BuildPipelineView, closure)
    }

    @Override
    BuildMonitorView buildMonitorView(String name, @DslContext(BuildMonitorView) Closure closure) {
        processView(name, BuildMonitorView, closure)
    }

    // this method cannot be private due to http://jira.codehaus.org/browse/GROOVY-6263
    protected <T extends View> T processView(String name, Class<T> viewClass, Closure closure) {
        T view = viewClass.newInstance(jm)
        view.name = name
        view.with(closure)
        referencedViews << view
        view
    }

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

    @Override
    @Deprecated
    Folder folder(@DslContext(Folder) Closure closure) {
        jm.logDeprecationWarning()

        Folder folder = new Folder(jm)
        folder.with(closure)
        referencedJobs << folder
        folder
    }

    @Override
    Folder folder(String name, @DslContext(Folder) Closure closure) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), 'name must be specified')

        Folder folder = new Folder(jm)
        folder.name = name
        folder.with(closure)
        referencedJobs << folder
        folder
    }

    ConfigFile customConfigFile(String name, @DslContext(ConfigFile) Closure closure) {
        processConfigFile(name, ConfigFileType.Custom, closure)
    }

    ConfigFile mavenSettingsConfigFile(String name, @DslContext(ConfigFile) Closure closure) {
        processConfigFile(name, ConfigFileType.MavenSettings, closure)
    }

    @Override
    @Deprecated
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
        ConfigFile configFile = new ConfigFile(configFileType, jm)
        configFile.name = name
        configFile.with(closure)
        referencedConfigFiles << configFile
        configFile
    }

    @Override
    void queue(String jobName) {
        queueToBuild << jobName
    }

    @Override
    void queue(Job job) {
        Preconditions.checkArgument(job.name as Boolean)
        queueToBuild << job.name
    }

    @Override
    InputStream streamFileFromWorkspace(String filePath) {
        Preconditions.checkArgument(filePath as Boolean)
        jm.streamFileInWorkspace(filePath)
    }

    @Override
    String readFileFromWorkspace(String filePath) {
        Preconditions.checkArgument(filePath as Boolean)
        jm.readFileInWorkspace(filePath)
    }

    @Override
    String readFileFromWorkspace(String jobName, String filePath) {
        Preconditions.checkArgument(jobName as Boolean)
        Preconditions.checkArgument(filePath as Boolean)
        jm.readFileInWorkspace(jobName, filePath)
    }
}
