package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class DebianPackagePublisherContext implements Context {
    String commitMessage = ''

    void commitMessage(String commitMessage) {
        this.commitMessage = commitMessage
    }
}
