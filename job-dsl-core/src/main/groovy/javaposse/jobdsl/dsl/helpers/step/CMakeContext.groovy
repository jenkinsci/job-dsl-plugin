package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class CMakeContext implements Context {
    String cmakeName = 'InSearchPath'
    String generator = 'Unix Makefiles'
    boolean cleanBuild = false
    String sourceDir
    String buildDir
    String buildType
    String preloadScript
    final List<String> args = []
    final List<Node> buildToolStepNodes = []

    /**
     * Specifies the name of the CMake installation to be used for this build step. Defaults to {@code 'InSearchPath'}.
     */
    void cmakeInstallation(String cmakeInstallation) {
        cmakeName = cmakeInstallation
    }

    /**
     * Specifies the CMake´s script generator to use. Defaults to {@code 'Unix Makefiles'}.
     */
    void generator(String generator) {
        this.generator = generator
    }

    /**
     * If set to true, the build directory will be deleted before CMake invocation.
     * Defaults to {@code false}.
     */
    void cleanBuild(boolean cleanBuild = true) {
        this.cleanBuild = cleanBuild
    }

    /**
     * Specifies the source directory.
     */
    void sourceDir(String sourceDir) {
        this.sourceDir = sourceDir
    }

    /**
     * Pre-populate the CMake cache variable {@code CMAKE_BUILD_TYPE} upon CMake invocation.
     */
    void buildType(String buildType) {
        this.buildType = buildType
    }

    /**
     * Specifies the directory where the project will be build in.
     */
    void buildDir(String buildDir) {
        this.buildDir = buildDir
    }

    /**
     * Specifies optional path to a pre-load script file to populate the CMake cache.
     */
    void preloadScript(String preloadScript) {
        this.preloadScript = preloadScript
    }

    /**
     * Specifies other arguments to be added to the CMake call. Can be called multiple times to add more args.
     */
    void args(String args) {
        this.args << args
    }

    /**
     * Adds a build tool invocation. Can be called multiple times to add more invocations.
     */
    void buildToolStep(@DslContext(CMakeBuildToolStepContext) Closure closure) {
        CMakeBuildToolStepContext context = new CMakeBuildToolStepContext()
        ContextHelper.executeInContext(closure, context)

        buildToolStepNodes << new NodeBuilder().'hudson.plugins.cmake.BuildToolStep' {
            withCmake(context.useCmake)
            if (context.args) {
                delegate.args(context.args.join(' '))
            }
            if (context.vars) {
                delegate.vars(context.vars.collect { k, v -> "$k=$v" }.join('\n'))
            }
        }
    }
}
