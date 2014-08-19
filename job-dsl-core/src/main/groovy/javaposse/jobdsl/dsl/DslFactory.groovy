package javaposse.jobdsl.dsl

interface DslFactory {
    Job job(Closure closure)

    Job job(Map<String, Object> arguments, Closure closure)

    View view(Closure closure)

    View view(Map<String, Object> arguments, Closure closure)

    Folder folder(Closure closure)

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
