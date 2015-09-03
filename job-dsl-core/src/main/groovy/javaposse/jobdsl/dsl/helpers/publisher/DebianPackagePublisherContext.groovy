package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class DebianPackagePublisherContext implements Context {
    String commitMessage

    /**
     * If set, commits changes made to package back to SCM with the specified commit message.
     */
    void commitMessage(String commitMessage) {
        this.commitMessage = commitMessage
    }
}
