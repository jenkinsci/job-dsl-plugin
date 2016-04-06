package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class MSBuildContext implements Context {
    String msBuildName = '(Default)'
    String buildFile
    final List<String> args = []
    boolean passBuildVariables = false
    boolean continueOnBuildFailure = false
    boolean unstableIfWarnings = false

    /**
     * Specifies the name of the MSBuild installation to be used for this build step.
     */
    void msBuildInstallation(String msBuildInstallation) {
        msBuildName = msBuildInstallation
    }

    /**
     * Specifies the name of the MSBuild build script file.
     */
    void buildFile(String buildFile) {
        this.buildFile = buildFile
    }

    /**
     * Specifies the command line arguments. Can be called multiple times to add more args.
     */
    void args(String args) {
        this.args << args
    }

    /**
     * If set to true, Jenkins build variables will be passed to MSBuild as {@code /p:name=value} pairs.
     * Defaults to {@code false}.
     */
    void passBuildVariables(boolean passBuildVariables = true) {
        this.passBuildVariables = passBuildVariables
    }

    /**
     * If set to true, the job will continue despite of MSBuild build failures. Defaults to {@code false}.
     */
    void continueOnBuildFailure(boolean continueOnBuildFailure = true) {
        this.continueOnBuildFailure = continueOnBuildFailure
    }

    /**
     * If set to true, the build will be set unstable on build warnings. Defaults to {@code false}.
     */
    void unstableIfWarnings(boolean unstableIfWarnings = true) {
        this.unstableIfWarnings = unstableIfWarnings
    }
}
