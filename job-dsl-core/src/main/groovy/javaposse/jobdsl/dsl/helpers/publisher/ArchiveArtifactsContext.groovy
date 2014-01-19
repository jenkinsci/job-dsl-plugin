package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class ArchiveArtifactsContext implements Context {
    String patternValue = ''
    String excludesValue = null
    Boolean latestOnlyValue = false
    Boolean allowEmptyValue = false

    void pattern(String glob) {
        patternValue = glob
    }

    void exclude(String glob) {
        excludesValue = glob
    }

    void latestOnly(Boolean val = true) {
        latestOnlyValue = val
    }

    void allowEmpty(Boolean val = true) {
        allowEmptyValue = val
    }
}
