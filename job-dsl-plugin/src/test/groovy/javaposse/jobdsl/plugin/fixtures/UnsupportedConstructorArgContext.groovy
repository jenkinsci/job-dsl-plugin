package javaposse.jobdsl.plugin.fixtures

import javaposse.jobdsl.dsl.Context

class UnsupportedConstructorArgContext implements Context {
    @SuppressWarnings('GroovyUnusedDeclaration')
    UnsupportedConstructorArgContext(String arg) {
    }
}
