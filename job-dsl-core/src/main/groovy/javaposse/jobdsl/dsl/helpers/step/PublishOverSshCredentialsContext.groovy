package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class PublishOverSshCredentialsContext implements Context {
    final String username
    String pathToKey
    String key

    PublishOverSshCredentialsContext(String username) {
        this.username = username
    }

    void pathToKey(String pathToKey) {
        this.pathToKey = pathToKey
    }

    void key(String key) {
        this.key = key
    }
}
