package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class RefToPushContext implements Context {
    String targetRepo
    String name

    void targetRepo(String targetRepo) {
        this.targetRepo = targetRepo
    }

    void name(String name) {
        this.name = name
    }
}
