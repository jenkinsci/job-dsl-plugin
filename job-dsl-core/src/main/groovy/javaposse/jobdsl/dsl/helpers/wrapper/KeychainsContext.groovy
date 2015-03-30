package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class KeychainsContext implements Context {
    List<Node> keychains = []
    boolean delete = false
    boolean overwrite = false

    void keychain(String keychain, String identity, String prefix = '') {
        keychains << new NodeBuilder().'com.sic.plugins.kpp.model.KPPKeychainCertificatePair' {
            delegate.keychain(keychain)
            codeSigningIdentity identity
            varPrefix prefix
        }
    }

    void delete(boolean delete = true) {
        this.delete = delete
    }

    void overwrite(boolean overwrite = true) {
        this.overwrite = overwrite
    }
}
