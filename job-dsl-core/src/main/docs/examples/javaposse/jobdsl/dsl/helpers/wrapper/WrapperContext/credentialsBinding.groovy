job('example') {
    wrappers {
        credentialsBinding {
            file('KEYSTORE', 'keystore.jks')
            usernamePassword('PASSWORD', 'keystore password')
        }
    }
}
