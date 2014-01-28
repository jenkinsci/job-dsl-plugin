package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class ArchiveArtifactsContext implements Context {
    String patternValue = ''
    String excludesValue = null
    Boolean latestOnlyValue = false

    // default null for compatibility with jenkins <= 1.480
    // when not specified, the relevant child node *will not* be generated
    // The behavior is the same when false or absent.
    Boolean allowEmptyValue = null

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
        // N.B. not compatible with jenkins <= 1.480
        allowEmptyValue = val
    }
}
