package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class SauceOnDemandContext implements Context {
    boolean useGeneratedTunnelIdentifier
    boolean sendUsageData
    boolean enableSauceConnect
    boolean launchSauceConnectOnSlave
    boolean useLatestVersion
    boolean verboseLogging
    String nativeAppPackage
    String sauceConnectPath
    String seleniumHost
    String seleniumPort
    String options
    List<String> webDriverBrowsers = []
    List<String> appiumBrowsers = []

    /**
     * Specifies the browsers to test with Appium. Can be called multiple times to add more browsers.
     */
    void appiumBrowsers(String... appiumBrowsers) {
        this.appiumBrowsers.addAll(appiumBrowsers)
    }

    /**
     * Specifies the browsers to test with WebDriver. Can be called multiple times to add more browsers.
     */
    void webDriverBrowsers(String... webDriverBrowsers) {
        this.webDriverBrowsers.addAll(webDriverBrowsers)
    }

    /**
     * Sets the command line options to be included when launching Sauce Connect.
     */
    void options(String options) {
        this.options = options
    }

    /**
     * Overrides the Selenium port when using a dedicated Sauce Connect instance.
     */
    void seleniumPort(String seleniumPort) {
        this.seleniumPort = seleniumPort
    }

    /**
     * Overrides the Selenium host when using a dedicated Sauce Connect instance.
     */
    void seleniumHost(String seleniumHost) {
        this.seleniumHost = seleniumHost
    }

    /**
     * Sets the location of the Sauce Connect executable to launch.
     * Leave blank to use the built-in Sauce Connect binary.
     */
    void sauceConnectPath(String sauceConnectPath) {
        this.sauceConnectPath = sauceConnectPath
    }

    /**
     * Sets the path to the native app package to be tested.
     */
    void nativeAppPackage(String nativeAppPackage) {
        this.nativeAppPackage = nativeAppPackage
    }

    /**
     * If set, the output from the Sauce Connect process will be included in the console output. Defaults to
     * {@code false}.
     */
    void verboseLogging(boolean verboseLogging = true) {
        this.verboseLogging = verboseLogging
    }

    /**
     * If set, tests always with the latest version of a given browser. Defaults to {@code false}.
     */
    void useLatestVersion(boolean useLatestVersion = true) {
        this.useLatestVersion = useLatestVersion
    }

    /**
     * If set, launches Sauce Connect on the slave. Defaults to {@code false}.
     */
    void launchSauceConnectOnSlave(boolean launchSauceConnectOnSlave = true) {
        this.launchSauceConnectOnSlave = launchSauceConnectOnSlave
    }

    /**
     * Enables Sauce Connect and launches a SSH tunnel. Defaults to {@code false}.
     */
    void enableSauceConnect(boolean enableSauceConnect = true) {
        this.enableSauceConnect = enableSauceConnect
    }

    /**
     * If set, the Sauce Jenkins plugin will send information about the Jenkins build to Sauce Labs. Defaults to
     * {@code false}.
     */
    void sendUsageData(boolean sendUsageData = true) {
        this.sendUsageData = sendUsageData
    }

    /**
     * If set, generates a unique tunnel identifier for every build. Defaults to {@code false}.
     */
    void useGeneratedTunnelIdentifier(boolean useGeneratedTunnelIdentifier = true) {
        this.useGeneratedTunnelIdentifier = useGeneratedTunnelIdentifier
    }
}
