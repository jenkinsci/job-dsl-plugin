package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class JavadocContext implements Context {
    String javadocDir = ''
    boolean keepAll

    void javadocDir(String javadocDir) {
        this.javadocDir = javadocDir
    }

    void keepAll(boolean keepAll) {
        this.keepAll = keepAll
    }
}
