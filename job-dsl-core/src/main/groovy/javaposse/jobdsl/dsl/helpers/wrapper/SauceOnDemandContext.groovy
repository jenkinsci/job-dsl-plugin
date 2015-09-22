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

    List<String> webDriverBrowsers = []
    List<String> appiumBrowsers = []

    /**
     *  * Adds list of browsers to test with Appium.
     * @param appiumBrowsers
     */
    void appiumBrowsers(String... appiumBrowsers) {
        this.appiumBrowsers.addAll(appiumBrowsers)
    }

    /**
     * Adds list of browsers with Sauce.
     * @param webDriverBrowsers
     */
    void webDriverBrowsers(String... webDriverBrowsers) {
        this.webDriverBrowsers = webDriverBrowsers
    }

    /**
     * Adds options
     * @param options
     */
    void options(String options) {
        this.options = options
    }

    /**
     * adds https protocol
     * @param httpsProtocol
     */
    void httpsProtocol(String httpsProtocol) {
        this.httpsProtocol = httpsProtocol
    }

    /**
     * Adds port configuration
     * @param seleniumPort
     */
    void seleniumPort(String seleniumPort) {
        this.seleniumPort = seleniumPort
    }
    /**
     * Adds Selenium Host
     * @param seleniumHost
     */
    void seleniumHost(String seleniumHost) {
        this.seleniumHost = seleniumHost
    }
    /**
     * Adds SauceConnect Path
     * @param sauceConnectPath
     */
    void sauceConnectPath(String sauceConnectPath) {
        this.sauceConnectPath = sauceConnectPath
    }
    /**
     * Adds nativeApp package
     * @param nativeAppPackage
     */
    void nativeAppPackage(String nativeAppPackage) {
        this.nativeAppPackage = nativeAppPackage
    }

    /**
     * Adds Verbose loggin
     */
    void verboseLogging(boolean verboseLogging = true) {
        this.verboseLogging = verboseLogging
    }
    /**
     * adds latest version flag
     * @param useLatestVersion
     */
    void useLatestVersion(boolean useLatestVersion = true) {
        this.useLatestVersion = useLatestVersion
    }
    /**
     * Adds launch sauce on slave flag
     * @param launchSauceConnectOnSlave
     */
    void launchSauceConnectOnSlave(boolean launchSauceConnectOnSlave = true) {
        this.launchSauceConnectOnSlave = launchSauceConnectOnSlave
    }
    /**
     * adds use legacy sauce connect flag
     * @param useOldSauceConnect
     */
    void useOldSauceConnect(boolean useOldSauceConnect = true) {
        this.useOldSauceConnect = useOldSauceConnect
    }
    /**
     * enables sauce connect
     * @param enableSauceConnect
     */
    void enableSauceConnect(boolean enableSauceConnect = true) {
        this.enableSauceConnect = enableSauceConnect
    }
    /**
     * adds use Chrome for Android flag
     * @param useChromeForAndroid
     */
    void useChromeForAndroid(boolean useChromeForAndroid = true) {
        this.useChromeForAndroid = useChromeForAndroid
    }
    /**
     * adds send usage flag
     * @param sendUsageData
     */
    void sendUsageData(boolean sendUsageData = true) {
        this.sendUsageData = sendUsageData
    }
    /**
     * adds use Generated Tunnel Identifier
     * @param useGeneratedTunnelIdentifier
     */
    void useGeneratedTunnelIdentifier(boolean useGeneratedTunnelIdentifier = true) {
        this.useGeneratedTunnelIdentifier = useGeneratedTunnelIdentifier
    }

}
