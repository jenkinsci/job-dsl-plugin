package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.Context

class PublishOverSshCredentialsContext implements Context {
    final String username
    String pathToKey
    String key

    PublishOverSshCredentialsContext(String username) {
        this.username = username
    }

    /**
     * Sets the path to the private key.
     */
    void pathToKey(String pathToKey) {
        this.pathToKey = pathToKey
    }

    /**
     * Specifies the private key.
     *
     * For security reasons, do not use a hard-coded keys. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     */
    void key(String key) {
        this.key = key
    }
}
