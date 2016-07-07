package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class SauceOnDemandContext extends AbstractContext {
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
    String credentialsId
    List<String> webDriverBrowsers = []
    List<String> appiumBrowsers = []

    SauceOnDemandContext(JobManagement jobManagement) {
        super(jobManagement)
    }

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
     * Sets the credentials a build should use.
     *
     * @since 1.49
     */
    @RequiresPlugin(id = 'sauce-ondemand', minimumVersion = '1.148')
    void credentials(String credentialsId) {
        this.credentialsId = credentialsId
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
