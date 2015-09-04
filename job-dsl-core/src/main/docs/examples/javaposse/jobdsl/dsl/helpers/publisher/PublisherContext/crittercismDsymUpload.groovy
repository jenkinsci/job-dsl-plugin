job('example') {
    publishers {
        crittercismDsymUpload {
            apiKey('secret')
            appID('c001abb')
            filePath('build/myapp.dSYM.zip')
        }
    }
}
