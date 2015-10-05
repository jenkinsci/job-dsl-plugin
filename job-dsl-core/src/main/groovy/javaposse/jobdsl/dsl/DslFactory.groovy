package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.BuildFlowJob
import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import javaposse.jobdsl.dsl.jobs.IvyJob
import javaposse.jobdsl.dsl.jobs.MatrixJob
import javaposse.jobdsl.dsl.jobs.MavenJob
import javaposse.jobdsl.dsl.jobs.MultiJob
import javaposse.jobdsl.dsl.jobs.WorkflowJob

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
    @RequiresPlugin(id = 'build-flow-plugin')
    BuildFlowJob buildFlowJob(String name)

    /**
     * Creates or update a job for managing Jenkins jobs orchestration using a dedicated DSL.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'build-flow-plugin')
    BuildFlowJob buildFlowJob(String name, @DslContext(BuildFlowJob) Closure closure)

    /**
     * Creates or updates a job to build an Ivy project.
     *
     * @since 1.38
     * @see #ivyJob(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'ivy', minimumVersion = '1.23')
    IvyJob ivyJob(String name)

    /**
     * Creates or updates a job to build an Ivy project.
     *
     * @since 1.38
     */
    @RequiresPlugin(id = 'ivy', minimumVersion = '1.23')
    IvyJob ivyJob(String name, @DslContext(IvyJob) Closure closure)

    /**
     * Creates or updates a multi-configuration job.
     *
     * @since 1.30
     * @see #matrixJob(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'matrix-project')
    MatrixJob matrixJob(String name)

    /**
     * Creates or updates a multi-configuration job.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'matrix-project')
    MatrixJob matrixJob(String name, @DslContext(MatrixJob) Closure closure)

    /**
     * Creates or updates a job to build a Maven project.
     *
     * @since 1.30
     * @see #mavenJob(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'maven-plugin')
    MavenJob mavenJob(String name)

    /**
     * Creates or updates a job to build a Maven project.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'maven-plugin')
    MavenJob mavenJob(String name, @DslContext(MavenJob) Closure closure)

    /**
     * Creates or updates a multi-job project, suitable for running other jobs.
     *
     * @since 1.30
     * @see #multiJob(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'jenkins-multijob-plugin')
    MultiJob multiJob(String name)

    /**
     * Creates or updates a multi-job project, suitable for running other jobs.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'jenkins-multijob-plugin')
    MultiJob multiJob(String name, @DslContext(MultiJob) Closure closure)

    /**
     * Create or updates a workflow job.
     *
     * @since 1.30
     * @see #workflowJob(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'workflow-aggregator')
    WorkflowJob workflowJob(String name)

    /**
     * Create or updates a workflow job.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'workflow-aggregator')
    WorkflowJob workflowJob(String name, @DslContext(WorkflowJob) Closure closure)

    /**
     * Creates or updates a folder.
     *
     * @since 1.30
     * @see #folder(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'cloudbees-folder')
    Folder folder(String name)

    /**
     * Creates or updates a folder.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'cloudbees-folder')
    Folder folder(String name, @DslContext(Folder) Closure closure)

    /**
     * Creates a managed custom file.
     *
     * @since 1.30
     * @see #customConfigFile(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'config-file-provider')
    ConfigFile customConfigFile(String name)

    /**
     * Creates a managed custom file.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'config-file-provider')
    ConfigFile customConfigFile(String name, @DslContext(ConfigFile) Closure closure)

    /**
     * Creates a managed Maven settings file.
     *
     * @since 1.30
     * @see #mavenSettingsConfigFile(java.lang.String, groovy.lang.Closure)
     */
    @RequiresPlugin(id = 'config-file-provider')
    ConfigFile mavenSettingsConfigFile(String name)

    /**
     * Creates a managed Maven settings file.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'config-file-provider')
    ConfigFile mavenSettingsConfigFile(String name, @DslContext(ConfigFile) Closure closure)

    /**
     * Creates a managed global Maven settings file.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'config-file-provider')
    ConfigFile globalMavenSettingsConfigFile(String name)

    /**
     * Creates a managed global Maven settings file.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'config-file-provider')
    ConfigFile globalMavenSettingsConfigFile(String name, @DslContext(ConfigFile) Closure closure)

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
