package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class PhabricatorNotifierContext implements Context {
    boolean commentOnSuccess
    boolean enableUberalls = true
    String commentFile = '.phabricator-comment'
    boolean preserveFormatting
    int commentSize = 1000
    boolean commentWithConsoleLinkOnFailure

    /**
     * Post a differential comment on successful builds. Defaults to {@code false}.
     */
    void commentOnSuccess(boolean commentOnSuccess = true) {
        this.commentOnSuccess = commentOnSuccess
    }

    /**
     * Enable code coverage reporting. Defaults to {@code true}.
     */
    void enableUberalls(boolean enableUberalls = true) {
        this.enableUberalls = enableUberalls
    }

    /**
     * Add additional context to Phabricator comment by outputting to this file.
     * Defaults to {@code '.phabricator-comment'}.
     */
    void commentFile(String commentFile) {
        this.commentFile = commentFile
    }

    /**
     * Preserve comment formatting. Defaults to {@code false}.
     */
    void preserveFormatting(boolean preserveFormatting = true) {
        this.preserveFormatting = preserveFormatting
    }

    /**
     * Maximum comment character length. Defaults to {@code 1000}.
     */
    void commentSize(int commentSize) {
        this.commentSize = commentSize
    }

    /**
     * Post a differential comment on failed builds with a link to the raw Console Output.
     * Defaults to {@code false}.
     */
    void commentWithConsoleLinkOnFailure(boolean commentWithConsoleLinkOnFailure = true) {
        this.commentWithConsoleLinkOnFailure = commentWithConsoleLinkOnFailure
    }
}
