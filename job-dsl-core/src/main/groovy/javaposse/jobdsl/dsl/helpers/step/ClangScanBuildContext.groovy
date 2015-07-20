package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class ClangScanBuildContext implements Context {
    String targetSdk = ''
    String config = ''
    String clangInstallationName = ''
    String workspace = ''
    String scheme = ''
    String xcodebuildargs = ''

    void targetSdk(String targetSdk) {
        this.targetSdk = targetSdk
    }

    void config(String config) {
        this.config = config
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

    void xcodebuildargs(String xcodebuildargs) {
        this.xcodebuildargs = xcodebuildargs
    }

}
