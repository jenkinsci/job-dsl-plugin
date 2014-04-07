package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.helpers.wrapper.WrapperEnvironmentVariableContext

class EnvironmentVariableContext extends WrapperEnvironmentVariableContext {
    String groovyScript = ''
    boolean keepSystemVariables = true
    boolean keepBuildVariables = true

    def groovy(String script) {
        groovyScript = script
    }

    def loadFilesFromMaster(boolean loadFilesFromMaster) {
        this.loadFilesFromMaster = loadFilesFromMaster
    }

    def keepSystemVariables(boolean keepSystemVariables) {
        this.keepSystemVariables = keepSystemVariables
    }

    def keepBuildVariables(boolean keepBuildVariables) {
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
