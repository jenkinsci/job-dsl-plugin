package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

/**
 * DSL for https://wiki.jenkins-ci.org/display/JENKINS/SLOCCount+Plugin
 */
class SlocCountContext implements Context {
    String pattern
    String encoding
    int buildsInGraph = 0
    boolean commentIsCode = false
    boolean ignoreBuildFailure = false

    /**
     * Specifies the generated raw SLOCCount or cloc report files to publish.
     */
    void pattern(String pattern) {
        this.pattern = pattern
    }

    /**
     * Sets the character encoding of SLOCCount result files.
     */
    void encoding(String encoding) {
        this.encoding = encoding
    }

    /**
     * Sets the maximal number of last successful builds, that are displayed in the trend graphs.
     */
    void buildsInGraph(int buildsInGraph) {
        this.buildsInGraph = buildsInGraph
    }

    /**
     * If set, results of counting blank lines and comments to code lines by default. Defaults to {@code false}.
     */
    void commentIsCode(boolean commentIsCode = true) {
        this.commentIsCode = commentIsCode
    }

    /**
     * If set, process the report files even if the build is not successful Defaults to {@code false}.
     */
    void ignoreBuildFailure(boolean ignoreBuildFailure = true) {
        this.ignoreBuildFailure = ignoreBuildFailure
    }
}
