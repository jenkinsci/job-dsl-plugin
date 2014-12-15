package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

abstract class AbstractGroovyContext implements Context {
    List<String> classpathEntries = []

    void classpath(String classpath) {
        classpathEntries << classpath
    }
}
