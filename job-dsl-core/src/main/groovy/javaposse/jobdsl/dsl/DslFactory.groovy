package javaposse.jobdsl.dsl

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

interface DslFactory extends ViewFactory {
    /**
     * Creates or updates a free style job.
     *
     * @since 1.30
     * @see #freeStyleJob(java.lang.String, groovy.lang.Closure)
     */
    FreeStyleJob job(String name)

    /**
     * Creates or updates a free style job.
     *
     * @since 1.31
     * @see #freeStyleJob(java.lang.String, groovy.lang.Closure)
     */
    FreeStyleJob job(String name, @DslContext(FreeStyleJob) Closure closure)

    /**
     * Creates or updates a free style job.
     *
     * @since 1.30
     * @see #freeStyleJob(java.lang.String, groovy.lang.Closure)
     */
    FreeStyleJob freeStyleJob(String name)

    /**
     * Creates or updates a free style job.
     *
     * @since 1.31
     */
    FreeStyleJob freeStyleJob(String name, @DslContext(FreeStyleJob) Closure closure)

    /**
     * Creates or update a job for managing Jenkins jobs orchestration using a dedicated DSL.
     *
     * @since 1.30
     * @see #buildFlowJob(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'build-flow-plugin', failIfMissing = true)
    @Deprecated
    BuildFlowJob buildFlowJob(String name)

    /**
     * Creates or update a job for managing Jenkins jobs orchestration using a dedicated DSL.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'build-flow-plugin', failIfMissing = true)
    @Deprecated
    BuildFlowJob buildFlowJob(String name, @DslContext(BuildFlowJob) Closure closure)

    /**
     * Creates or updates a job to build an Ivy project.
     *
     * @since 1.38
     * @see #ivyJob(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'ivy', minimumVersion = '1.23', failIfMissing = true)
    IvyJob ivyJob(String name)

    /**
     * Creates or updates a job to build an Ivy project.
     *
     * @since 1.38
     */
    @RequiresPlugin(id = 'ivy', minimumVersion = '1.23', failIfMissing = true)
    IvyJob ivyJob(String name, @DslContext(IvyJob) Closure closure)

    /**
     * Creates or updates a multi-configuration job.
     *
     * @since 1.30
     * @see #matrixJob(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'matrix-project', failIfMissing = true)
    MatrixJob matrixJob(String name)

    /**
     * Creates or updates a multi-configuration job.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'matrix-project', failIfMissing = true)
    MatrixJob matrixJob(String name, @DslContext(MatrixJob) Closure closure)

    /**
     * Creates or updates a job to build a Maven project.
     *
     * @since 1.30
     * @see #mavenJob(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'maven-plugin', minimumVersion = '2.3', failIfMissing = true)
    MavenJob mavenJob(String name)

    /**
     * Creates or updates a job to build a Maven project.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'maven-plugin', minimumVersion = '2.3', failIfMissing = true)
    MavenJob mavenJob(String name, @DslContext(MavenJob) Closure closure)

    /**
     * Creates or updates a multi-job project, suitable for running other jobs.
     *
     * @since 1.30
     * @see #multiJob(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'jenkins-multijob-plugin', minimumVersion = '1.22', failIfMissing = true)
    MultiJob multiJob(String name)

    /**
     * Creates or updates a multi-job project, suitable for running other jobs.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'jenkins-multijob-plugin', minimumVersion = '1.22', failIfMissing = true)
    MultiJob multiJob(String name, @DslContext(MultiJob) Closure closure)

    /**
     * Create or updates a pipeline job.
     * Alias for #workflowJob(java.lang.String).
     *
     * @since 1.47
     * @see #pipelineJob(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'workflow-aggregator', failIfMissing = true)
    WorkflowJob pipelineJob(String name)

    /**
     * Create or updates a pipeline job.
     * Alias for #workflowJob(java.lang.String, groovy.lang.Closure)
     *
     * @since 1.47
     */
    @RequiresPlugin(id = 'workflow-aggregator', failIfMissing = true)
    WorkflowJob pipelineJob(String name, @DslContext(WorkflowJob) Closure closure)

    /**
     * Create or updates a multibranch pipeline job.
     *
     * @since 1.47
     * @see #multibranchPipelineJob(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'workflow-multibranch', minimumVersion = '1.12', failIfMissing = true)
    MultibranchWorkflowJob multibranchPipelineJob(String name)

    /**
     * Creates or updates an organization folder job.
     *
     * @since 1.58
     * @see #organizationFolder(java.lang.String)
     */
    @RequiresPlugin(id = 'branch-api', minimumVersion = '1.11', failIfMissing = true)
    OrganizationFolderJob organizationFolder(String name, @DslContext(OrganizationFolderJob) Closure closure)

    /**
     * Creates or updates an organization folder job.
     *
     * @since 1.58
     */
    @RequiresPlugin(id = 'branch-api', minimumVersion = '1.11', failIfMissing = true)
    OrganizationFolderJob organizationFolder(String name)

    /**
     * Creates or updates a multibranch pipeline job.
     *
     * @since 1.47
     */
    @RequiresPlugin(id = 'workflow-multibranch', minimumVersion = '1.12', failIfMissing = true)
    MultibranchWorkflowJob multibranchPipelineJob(String name, @DslContext(MultibranchWorkflowJob) Closure closure)

    /**
     * Creates or updates a folder.
     *
     * @since 1.30
     * @see #folder(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'cloudbees-folder', minimumVersion = '5.0', failIfMissing = true)
    Folder folder(String name)

    /**
     * Creates or updates a folder.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'cloudbees-folder', minimumVersion = '5.0', failIfMissing = true)
    Folder folder(String name, @DslContext(Folder) Closure closure)

    /**
     * Creates a managed custom file.
     *
     * @since 1.30
     * @see #customConfigFile(java.lang.String, groovy.lang.Closure)
     * @deprecated use {@link #configFiles(groovy.lang.Closure)} instead
     */
    @RequiresPlugin(id = 'config-file-provider', failIfMissing = true)
    @Deprecated
    ConfigFile customConfigFile(String name)

    /**
     * Creates a managed custom file.
     *
     * @since 1.31
     * @deprecated use {@link #configFiles(groovy.lang.Closure)} instead
     */
    @RequiresPlugin(id = 'config-file-provider', failIfMissing = true)
    @Deprecated
    ConfigFile customConfigFile(String name, @DslContext(ConfigFile) Closure closure)

    /**
     * Creates a managed Maven settings file.
     *
     * @since 1.30
     * @see #mavenSettingsConfigFile(java.lang.String, groovy.lang.Closure)
     * @deprecated use {@link #configFiles(groovy.lang.Closure)} instead
     */
    @RequiresPlugin(id = 'config-file-provider', failIfMissing = true)
    @Deprecated
    MavenSettingsConfigFile mavenSettingsConfigFile(String name)

    /**
     * Creates a managed Maven settings file.
     *
     * @since 1.31
     * @deprecated use {@link #configFiles(groovy.lang.Closure)} instead
     */
    @RequiresPlugin(id = 'config-file-provider', failIfMissing = true)
    @Deprecated
    MavenSettingsConfigFile mavenSettingsConfigFile(String name, @DslContext(ConfigFile) Closure closure)

    /**
     * Creates a managed global Maven settings file.
     *
     * @since 1.39
     * @deprecated use {@link #configFiles(groovy.lang.Closure)} instead
     */
    @RequiresPlugin(id = 'config-file-provider', failIfMissing = true)
    @Deprecated
    MavenSettingsConfigFile globalMavenSettingsConfigFile(String name)

    /**
     * Creates a managed global Maven settings file.
     *
     * @since 1.39
     * @deprecated use {@link #configFiles(groovy.lang.Closure)} instead
     */
    @RequiresPlugin(id = 'config-file-provider', failIfMissing = true)
    @Deprecated
    MavenSettingsConfigFile globalMavenSettingsConfigFile(String name, @DslContext(ConfigFile) Closure closure)

    /**
     * Creates a managed script file.
     *
     * @since 1.40
     * @deprecated use {@link #configFiles(groovy.lang.Closure)} instead
     */
    @RequiresPlugin(id = 'managed-scripts', minimumVersion = '1.2.1', failIfMissing = true)
    @Deprecated
    ConfigFile managedScriptConfigFile(String name)

    /**
     * Creates a managed script file.
     *
     * @since 1.40
     * @deprecated use {@link #configFiles(groovy.lang.Closure)} instead
     */
    @RequiresPlugin(id = 'managed-scripts', minimumVersion = '1.2.1', failIfMissing = true)
    @Deprecated
    ParametrizedConfigFile managedScriptConfigFile(String name, @DslContext(ParametrizedConfigFile) Closure closure)

    /**
     * Creates managed config files.
     *
     * @since 1.58
     */
    @NoDoc(embeddedOnly = true)
    @RequiresPlugin(id = 'config-file-provider')
    void configFiles(@DslContext(ConfigFilesContext) Closure closure)

    /**
     * Upload the stream as <a href="https://wiki.jenkins-ci.org/display/JENKINS/User+Content">user content</a>.
     * Use {@link DslFactory#streamFileFromWorkspace(java.lang.String)} to read the content from a file.
     *
     * @param path relative destination path within the Jenkins userContent directory
     * @param content stream of the content to upload
     * @since 1.33
     */
    void userContent(String path, InputStream content)

    /**
     * Schedule a job to be run later. Validation of the job name isn't done until after the DSL has run.
     *
     * @param jobName the name of the job to be queued
     */
    void queue(String jobName)

    /**
     * Schedule a job to be run later.
     *
     * @param job the job to be queued
     */
    void queue(Job job)

    /**
     * Streams a file from the workspace of the seed job.
     *
     * @param filePath path of the file relative to the workspace root
     */
    InputStream streamFileFromWorkspace(String filePath)

    /**
     * Streams a file from the workspace of the seed job.
     *
     * @param filePath path of the file relative to the workspace root
     */
    String readFileFromWorkspace(String filePath)

    /**
     * Reads a file from the workspace of a job.
     *
     * @param jobName the job from which to read a file
     * @param filePath path of the file relative to the workspace root
     */
    String readFileFromWorkspace(String jobName, String filePath)
}
