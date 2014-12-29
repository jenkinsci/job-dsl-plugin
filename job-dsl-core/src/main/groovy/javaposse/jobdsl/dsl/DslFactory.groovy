package javaposse.jobdsl.dsl

interface DslFactory {
    Job job(@DslContext(Job) Closure closure)

    Job job(Map<String, Object> arguments, @DslContext(Job) Closure closure)

    View view(@DslContext(View) Closure closure)

    View view(Map<String, Object> arguments, @DslContext(View) Closure closure)

    Folder folder(@DslContext(Folder) Closure closure)

    ConfigFile configFile(@DslContext(ConfigFile) Closure closure)

    ConfigFile configFile(Map<String, Object> arguments, @DslContext(ConfigFile) Closure closure)

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
