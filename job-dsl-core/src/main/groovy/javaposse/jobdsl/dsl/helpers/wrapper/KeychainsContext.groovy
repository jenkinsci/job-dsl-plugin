package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class KeychainsContext implements Context {
    List<Node> keychains = []
    boolean delete = false
    boolean overwrite = false

    /**
     * Adds a keychain. Can be used multiple times to add more keychains.
     * With a single keychain, the prefix is optional.
     */
    void keychain(String keychain, String identity, String prefix = '') {
        keychains << new NodeBuilder().'com.sic.plugins.kpp.model.KPPKeychainCertificatePair' {
            delegate.keychain(keychain)
            codeSigningIdentity identity
            varPrefix prefix
        }
    }

    /**
     * Deletes copied keychains after build. Defaults to {@code false}.
     */
    void delete(boolean delete = true) {
        this.delete = delete
    }

    /**
     * Overwrites existing keychains. Defaults to {@code false}.
     */
    void overwrite(boolean overwrite = true) {
        this.overwrite = overwrite
    }
}
