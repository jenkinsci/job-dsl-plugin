package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.helpers.step.StepEnvironmentVariableContext

class WrapperEnvironmentVariableContext extends StepEnvironmentVariableContext {
    String script = ''
    String scriptFilePath = ''
    boolean loadFilesFromMaster = false

    void script(String script) {
        this.script = script
    }

    void scriptFile(String scriptFilePath) {
        this.scriptFilePath = scriptFilePath
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
        builder.loadFilesFromMaster(loadFilesFromMaster)
    }
}
