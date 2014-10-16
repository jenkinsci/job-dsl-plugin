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

    def tasks(String tasks) {
        this.tasks << tasks
    }

    def switches(String switches) {
        this.switches << switches
    }

    def useWrapper(boolean useWrapper = true) {
        this.useWrapper = useWrapper
    }

    def description(String description) {
        this.description = description
    }

    def rootBuildScriptDir(String rootBuildScriptDir) {
        this.rootBuildScriptDir = rootBuildScriptDir
    }

    def buildFile(String buildFile) {
        this.buildFile = buildFile
    }

    def fromRootBuildScriptDir(boolean fromRootBuildScriptDir = true) {
        this.fromRootBuildScriptDir = fromRootBuildScriptDir
    }

    def gradleName(String gradleName) {
        this.gradleName = gradleName
    }

    def makeExecutable(boolean makeExecutable = true) {
        this.makeExecutable = makeExecutable
    }

    def configure(Closure closure) {
        this.configureBlock = closure
    }
}
