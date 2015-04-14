package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.StepEnvironmentVariableContext

class WrapperEnvironmentVariableContext extends StepEnvironmentVariableContext {
    String script = ''
    String scriptFilePath = ''
    String groovyScript = ''
    boolean loadFilesFromMaster = false

    WrapperEnvironmentVariableContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void script(String script) {
        this.script = script
    }

    void scriptFile(String scriptFilePath) {
        this.scriptFilePath = scriptFilePath
    }

    /**
     * @since 1.30
     */
    void groovy(String script) {
        groovyScript = script
    }

    @Override
    protected addInfoContentToBuilder(builder) {
        super.addInfoContentToBuilder(builder)
        if (scriptFilePath) {
            builder.scriptFilePath(scriptFilePath)
        }
        if (script) {
            builder.scriptContent(script)
        }
        if (groovyScript) {
            builder.groovyScriptContent(groovyScript)
        }
        builder.loadFilesFromMaster(loadFilesFromMaster)
    }
}
