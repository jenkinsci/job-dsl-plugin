package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class XCodeContext implements Context {
    boolean cleanBeforeBuild = false
    boolean cleanTestReports = false
    String configuration = 'Release'
    String target = ''
    boolean interpretTargetAsRegEx = false
    String sdk = ''
    String symRoot = ''
    String configurationBuildDir = ''
    String xcodebuildArguments = ''
    String xcodeProjectPath = ''
    String xcodeProjectFile = ''
    String xcodeSchema = ''
    String xcodeWorkspaceFile = ''
    String embeddedProfileFile = ''
    String cfBundleVersionValue = ''
    String cfBundleShortVersionStringValue = ''
    boolean buildIpa = false
    boolean generateArchive = false
    boolean unlockKeychain = false
    String keychainName = 'none (specify one below)'
    String keychainPath = ''
    String keychainPwd = ''
    String codeSigningIdentity = ''
    boolean allowFailingBuildResults = false
    String ipaName = ''
    String ipaOutputDirectory = ''
    boolean provideApplicationVersion = false
    boolean changeBundleID = false
    String bundleID = ''
    String bundleIDInfoPlistPath = ''
    String ipaManifestPlistUrl = ''

    void cleanBeforeBuild(boolean cleanBeforeBuild) {
        this.cleanBeforeBuild = cleanBeforeBuild
    }

    void cleanTestReports(boolean cleanTestReports) {
        this.cleanTestReports = cleanTestReports
    }

    void configuration(String configuration) {
        this.configuration = configuration
    }

    void target(String target) {
        this.target = target
    }

    void interpretTargetAsRegEx(boolean interpretTargetAsRegEx) {
        this.interpretTargetAsRegEx = interpretTargetAsRegEx
    }

    void sdk(String sdk) {
        this.sdk = sdk
    }

    void symRoot(String symRoot) {
        this.symRoot = symRoot
    }

    void configurationBuildDir(String configurationBuildDir) {
        this.configurationBuildDir = configurationBuildDir
    }

    void xcodeProjectPath(String xcodeProjectPath) {
        this.xcodeProjectPath = xcodeProjectPath
    }

    void xcodeProjectFile(String xcodeProjectFile) {
        this.xcodeProjectFile = xcodeProjectFile
    }

    void xcodebuildArguments(String xcodebuildArguments) {
        this.xcodebuildArguments = xcodebuildArguments
    }

    void xcodeSchema(String xcodeSchema) {
        this.xcodeSchema = xcodeSchema
    }

    void xcodeWorkspaceFile(String xcodeWorkspaceFile) {
        this.xcodeWorkspaceFile = xcodeWorkspaceFile
    }

    void embeddedProfileFile(String embeddedProfileFile) {
        this.embeddedProfileFile = embeddedProfileFile
    }

    void cfBundleVersionValue(String cfBundleVersionValue) {
        this.cfBundleVersionValue = cfBundleVersionValue
    }

    void cfBundleShortVersionStringValue(String cfBundleShortVersionStringValue) {
        this.cfBundleShortVersionStringValue = cfBundleShortVersionStringValue
    }

    void buildIpa(boolean buildIpa) {
        this.buildIpa = buildIpa
    }

    void generateArchive(boolean generateArchive) {
        this.generateArchive = generateArchive
    }

    void unlockKeychain(boolean unlockKeychain) {
        this.unlockKeychain = unlockKeychain
    }

    void keychainName(String keychainName) {
        this.keychainName = keychainName
    }

    void keychainPath(String keychainPath) {
        this.keychainPath = keychainPath
    }

    void keychainPwd(String keychainPwd) {
        this.keychainPwd = keychainPwd
    }

    void codeSigningIdentity(String codeSigningIdentity) {
        this.codeSigningIdentity = codeSigningIdentity
    }

    void allowFailingBuildResults(boolean allowFailingBuildResults) {
        this.allowFailingBuildResults = allowFailingBuildResults
    }

    void ipaName(String ipaName) {
        this.ipaName = ipaName
    }

    void ipaOutputDirectory(String ipaOutputDirectory) {
        this.ipaOutputDirectory = ipaOutputDirectory
    }

    void provideApplicationVersion(boolean provideApplicationVersion) {
        this.provideApplicationVersion = provideApplicationVersion
    }

    void changeBundleID(boolean changeBundleID) {
        this.changeBundleID = changeBundleID
    }

    void bundleID(String bundleID) {
        this.bundleID = bundleID
    }

    void bundleIDInfoPlistPath(String bundleIDInfoPlistPath) {
        this.bundleIDInfoPlistPath = bundleIDInfoPlistPath
    }

    void ipaManifestPlistUrl(String ipaManifestPlistUrl) {
        this.ipaManifestPlistUrl = ipaManifestPlistUrl
    }

}
