package javaposse.jobdsl.dsl

/**
 * Runs provided DSL scripts via an external {@link JobManagement}.
 */
class DslScriptLoader extends AbstractDslScriptLoader<JobParent, GeneratedItems> {
    DslScriptLoader(JobManagement jobManagement) {
        super(jobManagement, JobParent, GeneratedItems)
    }
}
