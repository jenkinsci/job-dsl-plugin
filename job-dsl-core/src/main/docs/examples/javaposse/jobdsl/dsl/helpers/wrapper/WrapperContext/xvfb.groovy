job('example') {
    wrappers {
        xvfb('default') {
            screen('1920x1080x24')
        }
    }
}
