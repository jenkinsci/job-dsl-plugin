package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class SauceOnDemandContext implements Context {

    boolean useGeneratedTunnelIdentifier = false
    boolean sendUsageData = false
    boolean useChromeForAndroid = false
    boolean useOldSauceConnect = false
    boolean enableSauceConnect = true
    boolean launchSauceConnectOnSlave = false
    boolean useLatestVersion = false
    boolean verboseLogging = false
    String nativeAppPackage,
           sauceConnectPath,
           seleniumHost,
           seleniumPort,
           httpsProtocol,
           options

    List<String> webDriverBrowsers=[]
    List<String> appiumBrowsers=[]

    void appiumBrowsers(String... appiumBrowsers) {
        this.appiumBrowsers.addAll(appiumBrowsers)
    }

    void webDriverBrowsers(String... webDriverBrowsers) {
        this.webDriverBrowsers = webDriverBrowsers
    }

    void options(String options) {
        this.options = options
    }

    void httpsProtocol(String httpsProtocol) {
        this.httpsProtocol = httpsProtocol
    }

    void seleniumPort(String seleniumPort) {
        this.seleniumPort = seleniumPort
    }

    void seleniumHost(String seleniumHost) {
        this.seleniumHost = seleniumHost
    }

    void sauceConnectPath(String sauceConnectPath) {
        this.sauceConnectPath = sauceConnectPath
    }

    void nativeAppPackage(String nativeAppPackage) {
        this.nativeAppPackage = nativeAppPackage
    }

    void verboseLogging(boolean verboseLogging) {
        this.verboseLogging = verboseLogging
    }

    void useLatestVersion(boolean useLatestVersion) {
        this.useLatestVersion = useLatestVersion
    }

    void launchSauceConnectOnSlave(boolean launchSauceConnectOnSlave) {
        this.launchSauceConnectOnSlave = launchSauceConnectOnSlave
    }

    void useOldSauceConnect(boolean useOldSauceConnect) {
        this.useOldSauceConnect = useOldSauceConnect
    }

    void enableSauceConnect(boolean enableSauceConnect) {
        this.enableSauceConnect = enableSauceConnect
    }

    void useChromeForAndroid(boolean useChromeForAndroid) {
        this.useChromeForAndroid = useChromeForAndroid
    }

    void sendUsageData(boolean sendUsageData) {
        this.sendUsageData = sendUsageData
    }

    void useGeneratedTunnelIdentifier(boolean useGeneratedTunnelIdentifier) {
        this.useGeneratedTunnelIdentifier = useGeneratedTunnelIdentifier
    }

}
