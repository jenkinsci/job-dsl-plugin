package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.StepEnvironmentVariableContext

class WrapperEnvironmentVariableContext extends StepEnvironmentVariableContext {
    String script = ''
    String scriptFilePath = ''
    String groovyScript = ''
    boolean groovyScriptSandboxed = false
    boolean loadFilesFromMaster = false

    WrapperEnvironmentVariableContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Executes a script aimed at setting an environment such as creating folders, copying files, and so on.
     */
    void script(String script) {
        this.script = script
    }

    /**
     * Executes a script file aimed at setting an environment such as create folders, copy files, and so on.
     */
    void scriptFile(String scriptFilePath) {
        this.scriptFilePath = scriptFilePath
    }

    /**
     * Evaluates a Groovy script and inject a map result. The groovy script must return a map Java object.
     *
     * @since 1.30
     */
    void groovy(String script) {
        groovyScript = script
    }

    /**
     * Runs the Groovy script in a sandbox with limited privileges.
     * @since 1.73
     */
    void sandbox(boolean sandbox) {
        groovyScriptSandboxed = sandbox
    }

    @Override
    protected void addInfoContentToBuilder(Object builder) {
        super.addInfoContentToBuilder(builder)
        if (scriptFilePath) {
            builder.scriptFilePath(scriptFilePath)
        }
        if (script) {
            builder.scriptContent(script)
        }
        if (groovyScript) {
            builder.secureGroovyScript {
                builder.script(groovyScript)
                builder.sandbox(groovyScriptSandboxed)
            }
        }
        builder.loadFilesFromMaster(loadFilesFromMaster)
    }
}
