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

    void targetSdk(String targetSdk) {
        this.targetSdk = targetSdk
    }

    void configuration(String configuration) {
        this.configuration = configuration
    }

    void clangInstallationName(String clangInstallationName) {
        this.clangInstallationName = clangInstallationName
    }

    void workspace(String workspace) {
        this.workspace = workspace
    }

    void scheme(String scheme) {
        this.scheme = scheme
    }

    void scanBuildArgs(String scanBuildArgs) {
        this.scanBuildArgs = scanBuildArgs
    }

    void xcodeBuildArgs(String xcodeBuildArgs) {
        this.xcodeBuildArgs = xcodeBuildArgs
    }
}
