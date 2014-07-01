package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.helpers.Context

class ExclusionContext implements Context {
    List<String> names = []

    void resource(String name) {
        this.names << name
    }
}
