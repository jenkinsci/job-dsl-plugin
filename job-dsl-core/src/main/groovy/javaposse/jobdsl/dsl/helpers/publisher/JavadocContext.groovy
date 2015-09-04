package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class JavadocContext implements Context {
    String javadocDir = ''
    boolean keepAll

    /**
     * Sets the path to the Javadoc directory in the workspace.
     */
    void javadocDir(String javadocDir) {
        this.javadocDir = javadocDir
    }

    /**
     * If set, retains Javadoc for all successful builds. Defaults to {@code false}.
     */
    void keepAll(boolean keepAll) {
        this.keepAll = keepAll
    }
}
