package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.helpers.Context

class CodeSigningContext implements Context {
    def certPairs     = []
    boolean delete    = false
    boolean overwrite = false

    void delete(boolean delete = true) {
        this.delete = delete
    }

    void overwrite(boolean overwrite = true) {
        this.overwrite = overwrite
    }

    void certPair(String keychain, String identity, String prefix = '') {
        certPairs << new CertPair(keychain, identity, prefix)
    }

    class CertPair {
        String keychain
        String identity
        String prefix

        CertPair(keychain, identity, prefix) {
            this.keychain = keychain
            this.identity = identity
            this.prefix   = prefix
        }
    }
}
