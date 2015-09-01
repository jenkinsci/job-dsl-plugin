job('example') {
    wrappers {
        keychains {
            keychain('test1', 'test2')
            delete()
            overwrite()
        }
    }
}
