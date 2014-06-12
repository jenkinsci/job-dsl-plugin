package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.Context

abstract class AbstractGroovyContext implements Context {
    def classpathEntries = []

    def classpath(String classpath) {
        classpathEntries << classpath
    }
}
