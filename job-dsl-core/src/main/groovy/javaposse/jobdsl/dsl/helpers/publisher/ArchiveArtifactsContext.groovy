package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class ArchiveArtifactsContext implements Context {
    final List<String> patterns = []
    String excludesValue = null
    Boolean latestOnlyValue = false
    Boolean allowEmptyValue = null

    void pattern(String glob) {
        patterns << glob
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
