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
     * @since 1.30
     */
    FreeStyleJob job(String name)

    /**
     * @since 1.31
     */
    FreeStyleJob job(String name, @DslContext(FreeStyleJob) Closure closure)

    /**
     * @since 1.30
     */
    FreeStyleJob freeStyleJob(String name)

    /**
     * @since 1.31
     */
    FreeStyleJob freeStyleJob(String name, @DslContext(FreeStyleJob) Closure closure)

    /**
     * @since 1.30
     */
    BuildFlowJob buildFlowJob(String name)

    /**
     * @since 1.31
     */
    BuildFlowJob buildFlowJob(String name, @DslContext(BuildFlowJob) Closure closure)

    /**
     * @since 1.38
     */
    IvyJob ivyJob(String name)

    /**
     * @since 1.38
     */
    IvyJob ivyJob(String name, @DslContext(IvyJob) Closure closure)

    /**
     * @since 1.30
     */
    MatrixJob matrixJob(String name)

    /**
     * @since 1.31
     */
    MatrixJob matrixJob(String name, @DslContext(MatrixJob) Closure closure)

    /**
     * @since 1.30
     */
    MavenJob mavenJob(String name)

    /**
     * @since 1.31
     */
    MavenJob mavenJob(String name, @DslContext(MavenJob) Closure closure)

    /**
     * @since 1.30
     */
    MultiJob multiJob(String name)

    /**
     * @since 1.31
     */
    MultiJob multiJob(String name, @DslContext(MultiJob) Closure closure)

    /**
     * @since 1.30
     */
    WorkflowJob workflowJob(String name)

    /**
     * @since 1.31
     */
    WorkflowJob workflowJob(String name, @DslContext(WorkflowJob) Closure closure)

    /**
     * @since 1.30
     */
    Folder folder(String name)

    /**
     * @since 1.31
     */
    Folder folder(String name, @DslContext(Folder) Closure closure)

    /**
     * @since 1.30
     */
    ConfigFile customConfigFile(String name)

    /**
     * @since 1.31
     */
    ConfigFile customConfigFile(String name, @DslContext(ConfigFile) Closure closure)

    /**
     * @since 1.30
     */
    ConfigFile mavenSettingsConfigFile(String name)

    /**
     * @since 1.31
     */
    ConfigFile mavenSettingsConfigFile(String name, @DslContext(ConfigFile) Closure closure)

    /**
     * Upload the stream as <a href="https://wiki.jenkins-ci.org/display/JENKINS/User+Content">user content</a>.
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

    InputStream streamFileFromWorkspace(String filePath)

    String readFileFromWorkspace(String filePath)

    String readFileFromWorkspace(String jobName, String filePath)
}
