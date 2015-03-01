package javaposse.jobdsl.dsl

interface DslFactory {
    @Deprecated
    Job job(@DslContext(Job) Closure closure)

    @Deprecated
    Job job(Map<String, Object> arguments, @DslContext(Job) Closure closure)

    Job job(String name, @DslContext(Job) Closure closure)

    Job job(Map<String, Object> arguments, String name, @DslContext(Job) Closure closure)

    @Deprecated
    View view(@DslContext(View) Closure closure)

    @Deprecated
    View view(Map<String, Object> arguments, @DslContext(View) Closure closure)

    View view(String name, @DslContext(View) Closure closure)

    View view(Map<String, Object> arguments, String name, @DslContext(View) Closure closure)

    @Deprecated
    Folder folder(@DslContext(Folder) Closure closure)

    Folder folder(String name, @DslContext(Folder) Closure closure)

    @Deprecated
    ConfigFile configFile(@DslContext(ConfigFile) Closure closure)

    @Deprecated
    ConfigFile configFile(Map<String, Object> arguments, @DslContext(ConfigFile) Closure closure)

    ConfigFile configFile(String name, @DslContext(ConfigFile) Closure closure)

    ConfigFile configFile(Map<String, Object> arguments, String name, @DslContext(ConfigFile) Closure closure)

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
