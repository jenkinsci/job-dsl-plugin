package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

abstract class AbstractGroovyContext implements Context {
    List<String> classpathEntries = []

    /**
     * Specifies the script classpath. Can be called multiple times to add more entries.
     */
    void classpath(String classpath) {
        classpathEntries << classpath
    }
}
