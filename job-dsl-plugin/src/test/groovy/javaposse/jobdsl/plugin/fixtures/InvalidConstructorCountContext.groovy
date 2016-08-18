package javaposse.jobdsl.plugin.fixtures

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement

class InvalidConstructorCountContext implements Context {
    @SuppressWarnings('GroovyUnusedDeclaration')
    InvalidConstructorCountContext() {
    }

    @SuppressWarnings('GroovyUnusedDeclaration')
    InvalidConstructorCountContext(JobManagement jobManagement) {
    }
}
