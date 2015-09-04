package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class ClangScanBuildContext implements Context {
    String targetSdk = 'iphonesimulator'
    String configuration = 'Debug'
    String clangInstallationName
    String workspace
    String scheme
    String scanBuildArgs = '--use-analyzer Xcode'
    String xcodeBuildArgs = '-derivedDataPath $WORKSPACE/build'

    /**
     * Sets the simulator version of a currently installed SDK. Defaults to {@code 'iphonesimulator'}.
     */
    void targetSdk(String targetSdk) {
        this.targetSdk = targetSdk
    }

    /**
     * Sets the XCode config to execute scan-build against. Defaults to {@code Debug}.
     */
    void configuration(String configuration) {
        this.configuration = configuration
    }

    /**
     * Sets the name of the Clang installation to use.
     */
    void clangInstallationName(String clangInstallationName) {
        this.clangInstallationName = clangInstallationName
    }

    /**
     * Specifies the XCode workspace.
     */
    void workspace(String workspace) {
        this.workspace = workspace
    }

    /**
     * Specifies the XCode scheme.
     */
    void scheme(String scheme) {
        this.scheme = scheme
    }

    /**
     * Sets additional arguments for clang scan-build before the xcodebuild sub command.
     * Defaults to {@code '--use-analyzer Xcode'}.
     */
    void scanBuildArgs(String scanBuildArgs) {
        this.scanBuildArgs = scanBuildArgs
    }

    /**
     * Sets additional arguments for clang scan-build after the xcodebuild sub command.
     * Defaults to {@code '-derivedDataPath $WORKSPACE/build'}.
     */
    void xcodeBuildArgs(String xcodeBuildArgs) {
        this.xcodeBuildArgs = xcodeBuildArgs
    }
}
