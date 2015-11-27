package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class SlocCountContext implements Context {
    String pattern
    String encoding
    int buildsInGraph
    boolean commentIsCode
    boolean ignoreBuildFailure

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
     * If set, counts blank lines and comments to code lines. Defaults to {@code false}.
     */
    void commentIsCode(boolean commentIsCode = true) {
        this.commentIsCode = commentIsCode
    }

    /**
     * If set, processes the report files even if the build is not successful. Defaults to {@code false}.
     */
    void ignoreBuildFailure(boolean ignoreBuildFailure = true) {
        this.ignoreBuildFailure = ignoreBuildFailure
    }
}
