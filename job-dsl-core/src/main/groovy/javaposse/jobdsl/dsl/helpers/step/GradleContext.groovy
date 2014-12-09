package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context

class GradleContext implements Context {
    final List<String> tasks = []
    final List<String> switches = []
    boolean useWrapper = true
    String description = ''
    String rootBuildScriptDir = ''
    String buildFile = ''
    boolean fromRootBuildScriptDir = true
    boolean makeExecutable
    String gradleName = '(Default)'
    Closure configureBlock

    void tasks(String tasks) {
        this.tasks << tasks
    }

    void switches(String switches) {
        this.switches << switches
    }

    void useWrapper(boolean useWrapper = true) {
        this.useWrapper = useWrapper
    }

    void description(String description) {
        this.description = description
    }

    void rootBuildScriptDir(String rootBuildScriptDir) {
        this.rootBuildScriptDir = rootBuildScriptDir
    }

    void buildFile(String buildFile) {
        this.buildFile = buildFile
    }

    void fromRootBuildScriptDir(boolean fromRootBuildScriptDir = true) {
        this.fromRootBuildScriptDir = fromRootBuildScriptDir
    }

    void gradleName(String gradleName) {
        this.gradleName = gradleName
    }

    void makeExecutable(boolean makeExecutable = true) {
        this.makeExecutable = makeExecutable
    }

    void configure(Closure closure) {
        this.configureBlock = closure
    }
}
