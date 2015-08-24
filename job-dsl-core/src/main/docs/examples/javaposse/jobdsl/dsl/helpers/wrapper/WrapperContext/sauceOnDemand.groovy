//configures sauceOnDemand plugin
job('example') {
    wrappers {
        sauceOnDemandConfig {
            useGeneratedTunnelIdentifier(false)
            sendUsageData(false)
            nativeAppPackage()
            useChromeForAndroid(true)
            sauceConnectPath()
            useOldSauceConnect(false)
            enableSauceConnect(false)
            seleniumHost()
            seleniumPort()
            webDriverBrowsers('Linuxchrome43', 'Linuxchrome44')
            appiumBrowsers('Amazon_Kindle_Fire_Emulatorlandscapeandroid2_3_7_')
            useLatestVersion(true)
            launchSauceConnectOnSlave(true)
            httpsProtocol()
            options()
            verboseLogging(false)
        }
    }
}
