package javaposse.jobdsl.dsl

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

interface DslFactory {

    @Deprecated
    Job job(@DslContext(Job) Closure closure)

    @Deprecated
    Job job(Map<String, Object> arguments, @DslContext(Job) Closure closure)

    FreeStyleJob job(String name)

    FreeStyleJob job(String name, @DslContext(FreeStyleJob) Closure closure)

    FreeStyleJob freeStyleJob(String name)

    FreeStyleJob freeStyleJob(String name, @DslContext(FreeStyleJob) Closure closure)

    BuildFlowJob buildFlowJob(String name)

    BuildFlowJob buildFlowJob(String name, @DslContext(BuildFlowJob) Closure closure)

    MatrixJob matrixJob(String name)

    MatrixJob matrixJob(String name, @DslContext(MatrixJob) Closure closure)

    MavenJob mavenJob(String name)

    MavenJob mavenJob(String name, @DslContext(MavenJob) Closure closure)

    MultiJob multiJob(String name)

    MultiJob multiJob(String name, @DslContext(MultiJob) Closure closure)

    WorkflowJob workflowJob(String name)

    WorkflowJob workflowJob(String name, @DslContext(WorkflowJob) Closure closure)

    @Deprecated
    View view(@DslContext(View) Closure closure)

    @Deprecated
    View view(Map<String, Object> arguments, @DslContext(View) Closure closure)

    ListView listView(String name)

    ListView listView(String name, @DslContext(ListView) Closure closure)

    SectionedView sectionedView(String name)

    SectionedView sectionedView(String name, @DslContext(SectionedView) Closure closure)

    NestedView nestedView(String name)

    NestedView nestedView(String name, @DslContext(NestedView) Closure closure)

    DeliveryPipelineView deliveryPipelineView(String name)

    DeliveryPipelineView deliveryPipelineView(String name, @DslContext(DeliveryPipelineView) Closure closure)

    BuildPipelineView buildPipelineView(String name)

    BuildPipelineView buildPipelineView(String name, @DslContext(BuildPipelineView) Closure closure)

    BuildMonitorView buildMonitorView(String name)

    BuildMonitorView buildMonitorView(String name, @DslContext(BuildMonitorView) Closure closure)

    @Deprecated
    Folder folder(@DslContext(Folder) Closure closure)

    Folder folder(String name)

    Folder folder(String name, @DslContext(Folder) Closure closure)

    @Deprecated
    ConfigFile configFile(@DslContext(ConfigFile) Closure closure)

    @Deprecated
    ConfigFile configFile(Map<String, Object> arguments, @DslContext(ConfigFile) Closure closure)

    ConfigFile customConfigFile(String name)

    ConfigFile customConfigFile(String name, @DslContext(ConfigFile) Closure closure)

    ConfigFile mavenSettingsConfigFile(String name)

    ConfigFile mavenSettingsConfigFile(String name, @DslContext(ConfigFile) Closure closure)

    /**
     * Schedule a job to be run later. Validation of the job name isn't done until after the DSL has run.
     * @param jobName the name of the job to be queued
     */
    void queue(String jobName)

    /**
     * Schedule a job to be run later.
     * @param job the job to be queued
     */
    void queue(Job job)

    InputStream streamFileFromWorkspace(String filePath)

    String readFileFromWorkspace(String filePath)

    String readFileFromWorkspace(String jobName, String filePath)
}
