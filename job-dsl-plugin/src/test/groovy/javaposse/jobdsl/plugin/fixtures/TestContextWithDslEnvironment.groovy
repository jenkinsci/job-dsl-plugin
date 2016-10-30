package javaposse.jobdsl.plugin.fixtures

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.plugin.DslEnvironment

@SuppressWarnings('GroovyUnusedDeclaration')
class TestContextWithDslEnvironment implements Context {
    DslEnvironment dslEnvironment

    TestContextWithDslEnvironment(DslEnvironment dslEnvironment) {
        this.dslEnvironment = dslEnvironment
    }
}
