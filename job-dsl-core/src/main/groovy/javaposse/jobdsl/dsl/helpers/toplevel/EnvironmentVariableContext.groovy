package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.helpers.wrapper.WrapperEnvironmentVariableContext

class EnvironmentVariableContext extends WrapperEnvironmentVariableContext {
    String groovyScript = ''
    boolean keepSystemVariables = true
    boolean keepBuildVariables = true

    void groovy(String script) {
        groovyScript = script
    }

    void loadFilesFromMaster(boolean loadFilesFromMaster) {
        this.loadFilesFromMaster = loadFilesFromMaster
    }

    void keepSystemVariables(boolean keepSystemVariables) {
        this.keepSystemVariables = keepSystemVariables
    }

    void keepBuildVariables(boolean keepBuildVariables) {
        this.keepBuildVariables = keepBuildVariables
    }

    @Override
    protected addInfoContentToBuilder(builder) {
        super.addInfoContentToBuilder(builder)
        if (groovyScript) {
            builder.groovyScriptContent(groovyScript)
        }
    }
}
