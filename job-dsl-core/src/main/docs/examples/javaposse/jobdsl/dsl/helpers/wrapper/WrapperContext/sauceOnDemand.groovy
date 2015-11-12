job('example') {
    wrappers {
        sauceOnDemand {
            webDriverBrowsers('Linuxchrome43', 'Linuxchrome44')
            appiumBrowsers('Amazon_Kindle_Fire_Emulatorlandscapeandroid2_3_7_')
            useLatestVersion()
            launchSauceConnectOnSlave()
        }
    }
}
