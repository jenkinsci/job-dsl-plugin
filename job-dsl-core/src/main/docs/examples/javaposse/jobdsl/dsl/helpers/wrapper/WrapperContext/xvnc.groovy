job('example') {
    wrappers {
        xvnc {
            takeScreenshot()
            useXauthority(false)
        }
    }
}
