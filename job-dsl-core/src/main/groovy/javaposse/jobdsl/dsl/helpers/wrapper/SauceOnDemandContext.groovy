package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class SauceOnDemandContext implements Context {

    boolean useGeneratedTunnelIdentifier = false
    boolean sendUsageData = false
    boolean useChromeForAndroid = false
    boolean enableSauceConnect = true
    boolean launchSauceConnectOnSlave = false
    boolean useLatestVersion = false
    boolean verboseLogging = false
    String nativeAppPackage,
           sauceConnectPath,
           seleniumHost,
           seleniumPort,
           options

    List<String> webDriverBrowsers = []
    List<String> appiumBrowsers = []

    /**
     * Adds list of browsers configured to run  test with Appium.If a single browser is selected,
     * then it will be used to populate a SELENIUM_VERSION, SELENIUM_BROWSER, SELENIUM_PLATFORM and SELENIUM_DRIVER
     * variable.
     * If multiple browsers are selected, then they will be included in a SAUCE_ONDEMAND_BROWSERS environment variable
     * in JSON format.
     * These values can be extracted by your unit tests to invoke parallel Selenium tests.
     * @param appiumBrowsers
     */
    void appiumBrowsers(String... appiumBrowsers) {
        this.appiumBrowsers.addAll(appiumBrowsers)
    }

    /**
     * List of browsers you would like to SauceConnect to be configured. If a single browser is selected,
     * then the SELENIUM_VERSION, SELENIUM_BROWSER and SELENIUM_PLATFORM
     * environment variables will be populated to contain the details of the selected browser.
     * If you select multiple the SAUCE_ONDEMAND_BROWSERS environment variable will be populated with a
     * JSON-formatted string containing the attributes of the selected browsers.
     * @param webDriverBrowsers
     */
    void webDriverBrowsers(String... webDriverBrowsers) {
        this.webDriverBrowsers = webDriverBrowsers
    }

    /**
     * The command line options to be included when launching Sauce Connect.
     * @param options
     */
    void options(String options) {
        this.options = options
    }

    /**
     * If the enableSauceConnect is true, then the SELENIUM_HOST and SELENIUM_PORT varaiables will
     * default to localhost:4445. if the checkbox is not set, then the SELENIUM_HOST and SELENIUM_PORT variables
     * will be set to ondemand.saucelabs.com:4444. If you have a dedicated Sauce Connect instance running elsewhere,
     * you can override that here.
     * @param seleniumPort
     */
    void seleniumPort(String seleniumPort) {
        this.seleniumPort = seleniumPort
    }

    /**
     * If the enableSauceConnect is true, then the SELENIUM_HOST and SELENIUM_PORT varaiables will
     * default to localhost:4445. if the checkbox is not set, then the SELENIUM_HOST and SELENIUM_PORT variables
     * will be set to ondemand.saucelabs.com:4444. If you have a dedicated Sauce Connect instance running elsewhere,
     * you can override that here.
     * @param seleniumHost
     */
    void seleniumHost(String seleniumHost) {
        this.seleniumHost = seleniumHost
    }

    /**
     * Location of the Sauce Connect executable that you wish the Sauce plugin to launch.
     * Leave blank to use built-in Sauce Connect binary
     * @param sauceConnectPath
     */
    void sauceConnectPath(String sauceConnectPath) {
        this.sauceConnectPath = sauceConnectPath
    }
    /**
     * The path to the native app package to be tested.
     * The value entered here will be populated within the SAUCE_NATIVE_APP environment variable.
     * @param nativeAppPackage
     */
    void nativeAppPackage(String nativeAppPackage) {
        this.nativeAppPackage = nativeAppPackage
    }

    /**
     * If set to false turns off verbose logging. Default value is true.
     * If set to true, the output from the Sauce Connect process will be included in the console output for the Jenkins
     * job.
     */
    void verboseLogging(boolean verboseLogging = true) {
        this.verboseLogging = verboseLogging
    }
    /**
     * If this isset to true(default), the plugin will populate the SELENIUM_VERSION environment
     * variable with the most recent version number of the selected browser.
     * This is useful is you want to always test the latest version of a given browser.
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
     * Enabling Sauce Connect will launch a SSH tunnel, which creates a secure
     * and reliable tunnel from our cloud to your private network that can only
     * be accessed by you.
     * @param enableSauceConnect
     */
    void enableSauceConnect(boolean enableSauceConnect = true) {
        this.enableSauceConnect = enableSauceConnect
    }
    /**
     * If set the plugin will use Chrome for Android testing
     * @param useChromeForAndroid
     */
    void useChromeForAndroid(boolean useChromeForAndroid = true) {
        this.useChromeForAndroid = useChromeForAndroid
    }
    /**
     * If true(default),  the Sauce Jenkins plugin will send information about your
     * Jenkins build to Sauce Labs.
     * @param sendUsageData
     */
    void sendUsageData(boolean sendUsageData = true) {
        this.sendUsageData = sendUsageData
    }
    /**
     *  Generate a tunnel identifier of the form: <project name>-<current epoch time in milliseconds>
     *  The value of the tunnel identifier will be stored in the TUNNEL_IDENTIFIER environment variable.
     * @param useGeneratedTunnelIdentifier
     */
    void useGeneratedTunnelIdentifier(boolean useGeneratedTunnelIdentifier = true) {
        this.useGeneratedTunnelIdentifier = useGeneratedTunnelIdentifier
    }

}
